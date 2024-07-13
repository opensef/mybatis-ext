package com.opensef.mybatisext.page.dialect;

import com.alibaba.druid.pool.DruidDataSource;
import com.opensef.dynamicdatasource.DynamicDataSource;
import com.opensef.mybatisext.exception.MybatisExtException;
import com.opensef.mybatisext.util.ExtStringUtil;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方言分页工厂
 */
public class DialectFactory {

    /**
     * druid数据源是在项目中存在
     */
    private static boolean DRUID_EXISTS;

    /**
     * hikari数据源是否在项目中存在
     */
    private static boolean HIKARI_EXISTS;

    /**
     * 动态数据源是否在项目中存在
     */
    private static boolean DYNAMIC_DATA_SOURCE_EXISTS;

    static {
        try {
            Class.forName("com.alibaba.druid.pool.DruidDataSource");
            DRUID_EXISTS = true;
        } catch (ClassNotFoundException var2) {
            DRUID_EXISTS = false;
        }

        try {
            Class.forName("com.zaxxer.hikari.HikariDataSource");
            HIKARI_EXISTS = true;
        } catch (ClassNotFoundException var1) {
            HIKARI_EXISTS = false;
        }

        try {
            Class.forName("com.opensef.dynamicdatasource.DynamicDataSource");
            DYNAMIC_DATA_SOURCE_EXISTS = true;
        } catch (ClassNotFoundException var1) {
            DYNAMIC_DATA_SOURCE_EXISTS = false;
        }

    }

    public static Map<Class<?>, String> DATA_SOURCE_DATABASE_NAME_MAP = new ConcurrentHashMap<>();

    private static DialectProperties dialectProperties;

    /**
     * 方言Map集合 key:数据库名称 value:方言
     */
    public static Map<String, Dialect> DIALECT_MAP = new ConcurrentHashMap<>();

    public DialectFactory(List<DialectRegister> dialectRegisters, DialectProperties dialectProperties) {
        DialectFactory.dialectProperties = dialectProperties;

        initDialect();
        if (null != dialectRegisters && dialectRegisters.size() > 0) {
            for (DialectRegister dialectRegister : dialectRegisters) {
                dialectRegister.register(DIALECT_MAP);
            }
        }
    }

    /**
     * 注册方言
     *
     * @param code    数据库名称
     * @param dialect 方言
     */
    public void registerDialect(String code, Dialect dialect) {
        DIALECT_MAP.put(code, dialect);
    }

    /**
     * 根据数据源获取方言
     *
     * @param dataSource 数据源
     * @return 方言
     */
    public static Dialect get(DataSource dataSource) {
        String databaseName = getDatabaseName(dataSource);
        return DIALECT_MAP.get(databaseName);
    }

    // 注册默认方言
    private void initDialect() {
        registerDialect("mysql", new MySqlDialect());
        registerDialect("mariadb", new MySqlDialect());
        registerDialect("clickhouse", new MySqlDialect());

        registerDialect("oracle", new OracleDialect());
        registerDialect("oracle12c", new Oracle12cDialect());
        // 达梦
        registerDialect("dm", new OracleDialect());

        registerDialect("sqlserver", new SqlServerDialect());
        registerDialect("sqlserver2005", new SqlServer2005Dialect());

        registerDialect("postgresql", new PostgreSqlDialect());
    }

    /**
     * 根据数据源获取数据库名称
     *
     * @param dataSource 数据源
     * @return 数据库产品名称
     */
    public static String getDatabaseName(DataSource dataSource) {
        if (null == dataSource) {
            throw new MybatisExtException("数据源不能为空");
        }

        String databaseName = DATA_SOURCE_DATABASE_NAME_MAP.get(dataSource.getClass());
        if (ExtStringUtil.hasText(databaseName)) {
            return databaseName;
        }

        // 如果是动态数据源，则获取当前真实的数据源
        if (DYNAMIC_DATA_SOURCE_EXISTS && dataSource instanceof DynamicDataSource) {
            DynamicDataSource dynamicDataSource = (DynamicDataSource) dataSource;
            dataSource = dynamicDataSource.getCurrentDataSource();
            String currentDatabaseKey = dynamicDataSource.getCurrentDatabaseKey();

            // 获取手动在配置中设置的方言名称
            if (!dynamicDataSource.getResolvedDataSources().isEmpty()) {
                // 根据数据源key名称获取方言
                Map<String, DialectProperties.DialectInfo> dynamicMap = dialectProperties.getDynamic();
                if (null != dynamicMap && dynamicMap.size() > 0) {
                    DialectProperties.DialectInfo dialectInfo = dynamicMap.get(currentDatabaseKey);
                    if (null != dialectInfo) {
                        databaseName = dialectInfo.getName();
                    }
                }

            } else {
                databaseName = dialectProperties.getName();
            }
        } else {
            databaseName = dialectProperties.getName();
        }

        if (!ExtStringUtil.hasText(databaseName)) {
            databaseName = getDatabaseNameByUrl(dataSource);
        }

        DATA_SOURCE_DATABASE_NAME_MAP.put(dataSource.getClass(), databaseName);
        return databaseName;
    }

    /**
     * 从数据库url中获取数据库产品名称
     *
     * @param dataSource 数据源
     * @return 数据库产品名称
     */
    public static String getDatabaseNameByUrl(DataSource dataSource) {
        String jdbcUrl;
        if (HIKARI_EXISTS && dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            jdbcUrl = hikariDataSource.getJdbcUrl();
        } else if (DRUID_EXISTS && dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            jdbcUrl = druidDataSource.getUrl();
        } else {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                jdbcUrl = connection.getMetaData().getURL();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        String databaseName = null;
        if (ExtStringUtil.hasText(jdbcUrl)) {
            // jdbc连接url都是一jdbc:开头的
            int startIndex = "jdbc:".length();
            int endIndex = jdbcUrl.indexOf(":", startIndex);
            databaseName = jdbcUrl.substring(startIndex, endIndex);
        }
        return databaseName;
    }

}
