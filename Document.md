# 简介

mybatis-ext 是一个mybatis的扩展框架，提供了通用的增删改查方法、分页等功能，可以更方便操作数据库。

下面是一个最基本的示例：

```java

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

  Page<SysUser> findListPage(PageRequest pageRequest);

}
```

```java
@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    public SysUser findById(Long id) {
        return sysUserMapper.findById(id);
    }

    public SysUser findByUsername(String username) {
        LambdaQuery<SysUser> query = new LambdaQuery<>();
        query.eq(SysUser::getUsername, "test");
        return sysUserMapper.findOne(query);
    }

}
```

<div style="font-size:18pt; font-weight:bold; ">特性</div>

- 通用CRUD
- 支持通过Lambda方式构建查询
- 自定义扩展插件
- 逻辑删除
- 自动填充
- 数据权限
- 多租户

# 快速开始

## 环境要求

- 本项目基于JDK11构建，请使用JDK11及以上版本
- 推荐使用springboot 2.7以上版本

## 安装及配置

添加maven依赖

```xml

<dependency>
    <groupId>com.opensefcom.opensef</groupId>
    <artifactId>mybatis-ext-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

# Demo示例

接下来通过一个demo，来演示mybatis-ext的基本使用方式。

> 说明：本项目基于maven构建，使用mysql8数据库

<div style="font-size:16pt; font-weight:bold; ">1、创建数据库test，新建用户表、用户详情表</div>

```sql
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT 'id',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户名',
  `full_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '姓名',
  `age` int DEFAULT NULL COMMENT '年龄',
  `created_date` datetime DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint DEFAULT NULL COMMENT '创建人',
  `updated_date` datetime DEFAULT NULL COMMENT '修改时间',
  `updated_by` bigint DEFAULT NULL COMMENT '修改人',
  `is_deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='用户';
```

```sql
INSERT INTO sys_user
(id, username, full_name, age, created_date, created_by, updated_date, updated_by, is_deleted)
VALUES(1, 'Aaron Cole', 'Aaron Cole', 25, '2005-01-13 01:27:12', 534, '2021-04-30 10:52:41', 44, 0);
INSERT INTO sys_user
(id, username, full_name, age, created_date, created_by, updated_date, updated_by, is_deleted)
VALUES(2, 'Kojima Sakura', 'Kojima Sakura', 29, '2008-01-28 22:37:42', 280, '2014-09-21 01:51:51', 430, 0);
INSERT INTO sys_user
(id, username, full_name, age, created_date, created_by, updated_date, updated_by, is_deleted)
VALUES(3, 'Jia Xiuying', 'Jia Xiuying', 22, '2019-07-30 02:47:43', 144, '2014-11-21 15:04:08', 701, 0);
INSERT INTO sys_user
(id, username, full_name, age, created_date, created_by, updated_date, updated_by, is_deleted)
VALUES(4, 'Mak Cho Yee', 'Mak Cho Yee', 11, '2010-08-07 23:05:28', 474, '2017-03-30 13:19:27', 759, 0);
INSERT INTO sys_user
(id, username, full_name, age, created_date, created_by, updated_date, updated_by, is_deleted)
VALUES(5, 'Yeow Yu Ling', 'Yeow Yu Ling', 23, '2012-03-26 15:30:57', 434, '2014-01-06 12:48:32', 513, 0);
```

<div style="font-size:16pt; font-weight:bold; ">2、新建一个springboot工程，添加pom依赖</div>

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.opensefcom.opensef</groupId>
        <artifactId>mybatis-ext-spring-boot-starter</artifactId>
        <version>${mybatis-ext-spring-boot-starter.version}</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </dependency>
</dependencies>
```

> 同时引入lombok依赖，用于自动生成Getter、Setter

<div style="font-size:16pt; font-weight:bold; ">3、配置</div>

```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.0.100:3306/test?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      initialization-fail-timeout: 5000
      max-lifetime: 60000
mybatis:
  mapper-locations: classpath*:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

<div style="font-size:16pt; font-weight:bold; ">4、根据上述用户表，新建1个对应的实体类</div>

```java
@Data
public class SysUser {

    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    @TableColumn(typeHandler = DataTypeHandler.class)
    private String fullName;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 创建时间
     */
    private LocalDateTime createdDate;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 修改时间
     */
    @AutoFillLogicDelete
    private LocalDateTime updatedDate;

    /**
     * 修改人
     */
    @AutoFillLogicDelete
    private Long updatedBy;

    /**
     * 是否删除
     */
    @TableColumn("is_deleted")
    @Deleted(logicDelete = true)
    private Boolean isDeleted;

}
```



<div style="font-size:16pt; font-weight:bold; ">5、创建SysUserMapper类，继承BaseMapper</div>

```java
@Mapper
public class SysUserMapper extends BaseMapper<User> {

}
```

> 此时，一个基本的程序已经创建完成，下面可以基于SysUserMapper进行数据库操作。

<div style="font-size:16pt; font-weight:bold; ">6、测试用例</div>

<div style="font-size:14pt; font-weight:bold; ">根据用户名查询</div>

```java
public SysUser findByUsername(String username) {
    LambdaQuery<SysUser> query = new LambdaQuery<>();
    query.eq(SysUser::getUsername, username);
    query.eq(SysUser::getIsDeleted, false);
    return sysUserMapper.findOne(query);
}
```

- 输入参数：test

- 响应数据：

```json
{
    "id": 1,
    "username": "test",
    "fullName": "test",
    "age": 25,
    "createdDate": "2005-01-13 01:27:12",
    "createdBy": 1,
    "updatedDate": "2021-04-30 10:52:41",
    "updatedBy": 1,
    "isDeleted": false
}
```

<div style="font-size:14pt; font-weight:bold; ">列表查询</div>

```java
public List<SysUser> findList() {
    LambdaQuery<SysUser> query = new LambdaQuery<>();
    query.gt(SysUser::getId, 4);
    query.or(orExpression -> {
        orExpression.eq(SysUser::getAge, 22);
    });
    return sysUserMapper.findList(query);
}
```

- 响应数据：

```json
[
    {
        "id": 3,
        "username": "Jia Xiuying",
        "fullName": "Jia Xiuying",
        "age": 22,
        "createdDate": "2019-07-30 02:47:43",
        "createdBy": 1,
        "updatedDate": "2014-11-21 15:04:08",
        "updatedBy": 1,
        "isDeleted": false
    },
    {
        "id": 5,
        "username": "Yeow Yu Ling",
        "fullName": "Yeow Yu Ling",
        "age": 23,
        "createdDate": "2012-03-26 15:30:57",
        "createdBy": 1,
        "updatedDate": "2014-01-06 12:48:32",
        "updatedBy": 1,
        "isDeleted": false
    }
]
```

<div style="font-size:14pt; font-weight:bold; ">分页查询</div>

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    Page<SysUser> findSysUserPage(PageRequest pageRequest);

}
```

- 输入参数：pageNum=1，pageSize=2

- 响应数据：

```json
{
    "pageNum": 1,
    "pageSize": 2,
    "total": 5,
    "pages": 3,
    "list": [
        {
            "id": 1,
            "username": "test",
            "fullName": "test",
            "age": 25,
            "createdDate": "2005-01-13 01:27:12",
            "createdBy": 1,
            "updatedDate": "2021-04-30 10:52:41",
            "updatedBy": 1,
            "isDeleted": false
        },
        {
            "id": 2,
            "username": "Kojima Sakura",
            "fullName": "Kojima Sakura",
            "age": 29,
            "createdDate": "2008-01-28 22:37:42",
            "createdBy": 1,
            "updatedDate": "2014-09-21 01:51:51",
            "updatedBy": 1,
            "isDeleted": false
        }
    ]
}
```



------


<div style="font-size:14pt; font-weight:bold;">下一步</div>

通过上面的示例，向你展示了mybatis-ext的基本使用方法，接下来的章节将根据此示例，进一步介绍mybatis-ext的用法。

# 基础


## 注解说明

<div style="font-size:16pt; font-weight:bold; ">实体注解</div>

<div style="font-size:12pt; font-weight:bold; ">@TableName</div>
表名，通过在实体类上添加`@TableName("user")`方式设置，不设置时，默认通过实体类名称将驼峰转下划线，自动匹配到数据库表名

<div style="font-size:12pt; font-weight:bold; ">@TableColumn</div>
列名，通过在实体属性上添加`@TableColumn("is_deleted")`方式设置，不设置时，默认通过属性名称将驼峰转下划线，自动匹配到数据库字段名。还可以通过`@TableColumn(ignore = true)`设置忽略属性，通过BaseMapper执行数据库操作时会忽略该字段。

<div style="font-size:12pt; font-weight:bold; ">@TableId</div>
数据库表主键标识，当需要使用BaseMapper默认提供的方法时，必须设置

<div style="font-size:12pt; font-weight:bold; ">@Deleted</div>
逻辑删除，通过在实体属性上添加`@Delete(logicDelete = true)`方式设置，`logicDelete=true`为逻辑删除，不设置或`logicDelete=false`时为物理删除。设置为逻辑删除时，配置中必须指定逻辑删除正常值和删除值。

<div style="font-size:12pt; font-weight:bold; ">@AutoFillLogicDelete</div>

自动填充，通过在实体属性上添加`@AutoFillLogicDelete`方式设置。当逻辑删除需要更新自动填充的字段时，需要在自动填充的属性上添加此注解；逻辑删除不需要更新自动填充的字段，则不需要添加此注解。


<div style="font-size:16pt; font-weight:bold; ">插件注解</div>

<div style="font-size:12pt; font-weight:bold; ">@DataScope</div>

自动添加数据权限，通过在方法上添加@DataScope方式设置。详细说明参考[数据权限](#数据权限)章节



## ID生成策略

mybatis-ext支持4中ID生成策略，分别为数据库自增、自动赋值（雪花算法）、UUID、自定义。默认为雪花算法，如需使用其他策略，需要通过@TableId手动指定。例如，想使用UUID算法，则需要设置`@TableId(type = IdType.UUID)`。

> 当ID生成策略为自增时，需要自己继承BaseMapper，在插入的方法上增加如下注解

```
@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
```


## 自定义ID生成器

### 全局自定义ID生成器

实现全局自定义ID生成策略，则需实现IdHandler接口，将实现类通过@Bean方式注入或者用@Component方式注入，在实体ID上设置`@TableId(type = IdType.CUSTOM)`

```java
@Component
public class MyIdHandler implements IdHandler<Long> {

    @Override
    public Long getId() {
        return System.currentTimeMillis();
    }

}
```

### 手动指定ID生成器

除了全局自定义ID生成策略外，还可以给每个类单独设置自定义ID生成策略。在ID字段添加`@TableId(type = IdType.CUSTOM, idHandler = TestIdHandler.class)`注解。type必须为IdType.CUSTOM，当设置了idHandler时，则生成ID时会使用指定的策略，否则会使用全局自定义ID生成器。

第一步、创建ID生成策略

```java
// 注意，这里不需要添加@Component或者使用@Bean注入到Spring中
public class MyIdHandler implements IdHandler<Long> {

    @Override
    public Long getId() {
        return System.currentTimeMillis();
    }

}
```

第二步、在实体类手动指定ID生成器

```java
@Data
public class SysUser {

    @TableId(type = IdType.CUSTOM, idHandler = TestIdHandler.class)
    private String id;

    private String name;

}
```





## 通用BaseMapper

在上面的Demo示例中，我们在自己的UserMapper类中继承了BaseMapper，此类中提供了通用的CRUD方法，便于快速开发。
> 示例

`userMapper.insert(user);`
`userMapper.insertBatch(userList);`
`userMapper.update(user);`
`userMapper.delete(user);`
`userMapper.findById(11);`

```java
public List<SysUser> findList() {
    LambdaQuery<SysUser> query = new LambdaQuery<>();
    query.gt(SysUser::getId, 4);
    return sysUserMapper.findList(query);
    }
```



# 扩展

## 扩展BaseMapper

第一步：继承BaseMapper。

```java
@Mapper
public interface JdbcMapper<T> extends BaseMapper<T> {

    @DeleteProvider(type = CustomSqlProvider.class, method = "deleteByQuery")
    int deleteByQuery(LambdaQuery<T> query);

}
```



第二步：编写Provider类

```
public class CustomSqlProvider {

    public static String deleteByQuery(ProviderContext providerContext, @Param(BaseMapperParamConstant.QUERY) LambdaQuery<?> lambdaQuery) {
        // 下面的delete字符串，表示provider中的方法名
        return MapperManager.getSql(providerContext, "deleteByQuery", lambdaQuery);
    }

}
```

> 注意：MapperManager.getSql为不走缓存获取sql，适用于LambdaQuery这种动态入参的场景。如果查询条件是固定的，可以使用MapperManager.getSqlUseCache的方式提升查询效率。



第三步：编写sql构建器，实现MapperSqlBuilder接口

```
public class CustomMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
        String sql = "";
        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

}
```



第四步：将sql构建器注入到sql构建工厂中

```
@Configuration
public class CustomMapperConfiguration {

    @PostConstruct
    public void register() {
        MapperManager.registerSqlBuilder("deleteByQuery", new CustomMapperSqlBuilder());
    }

}
```



将自定义的方法名和SQL构建器注册到工厂中。

```java

@Component
public class MySqlBuilderRegister implements RepositorySqlBuilderFactoryRegister {

    @Override
    public void register(Map<String, RepositorySqlBuilder> methodSqlBuilderMap) {
        methodSqlBuilderMap.put("methodName", new CountByExampleRepositorySqlBuilder());
    }

}
```

## 逻辑删除

在实体类中设置了`@Delete(logicDelete = true)`
，既启用了逻辑删除。需要实现AutoFillHandler接口，实现logicDeletedNormalValue、logicDeletedValue两个方法，设置（逻辑删除-正常值，默认为false）和（逻辑删除-删除值，默认为true）。

> 逻辑删除只有使用BaseMapper中的删除方法执行时有效，其他方式执行不会生效。
>
>
mybatis-ext的逻辑删除不是全局的，查询时不会区分是否是逻辑删除，被删除的数据依然会查询出来，需要手动指定删除状态。如果你不认可这个逻辑，认为BaseMapper中的查询方法应该过滤掉被删除的数据，可以继承BaseMapper，重写里面的查询方法。
>
> 逻辑删除时，如果需要更新自动填充字段，需要给自动填充字段的属性添加@AutoFillLogicDelete注解，一般是给修改时间、修改人2个字段添加。

## 自动填充

我们做设计时，通常会给实体添加一些公共字段，将这些字段放在一个BaseEntity类里，其他实体继承BaseEntity。

```java

@Data
public class BaseEntity {

    /**
     * 创建时间
     */
    private LocalDateTime createdDate;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 修改时间
     */
    @AutoFillLogicDelete
    private LocalDateTime updatedDate;

    /**
     * 修改人
     */
    @AutoFillLogicDelete
    private Long updatedBy;

    /**
     * 是否删除
     */
    @Deleted(logicDelete = true)
    @TableColumn("is_deleted")
    private Boolean deleted;

}
```

类似这样的公共字段，我们没必要每次新增或修改时都手动对其赋值，mybat-ext可自动为其设置属性值。

操作步骤：

第一步、新建一个类，实现AutoFillHandler接口。接口的实现方法分为新增操作自动填充和修改操作自动填充，Map参数的key为需要自动填充字段的属性名，value为属性值。

```java
public class ProjectAutoFillHandler implements AutoFillHandler {

    @Override
    public Map<String, Object> getInsertAutoFillPropertyValue() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> map = new HashMap<>();
        map.put("createdDate", now);
        map.put("createdUserId", UserDataUtil.getUserId());
        map.put("updatedDate", now);
        map.put("updatedUserId", UserDataUtil.getUserId());
        return map;
    }

    @Override
    public Map<String, Object> getUpdateAutoFillPropertyValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("updatedDate", LocalDateTime.now());
        map.put("updatedUserId", UserDataUtil.getUserId());
        return map;
    }

}
```

第二步、将该类注入到Spring中。

```java
@Bean
public AutoFillHandler autoFillHandler(){
    return new ProjectAutoFillHandler();
}
```

## 数据权限

mybatis-ext的数据权限是基于功能编码进行控制，需要做数据权限的功能需要有一个唯一编码。通过@DataScope(functionCode = '
functionCode')方式进行控制。

第一步、新建DataScopeCreator类，实现DataScopeHandler接口，此实现类用于根据功能编码生成数据范围控制的SQL片段。

```
@Component
public class DataScopeCreator implements DataScopeHandler {

    @Override
    public DataScopeInfo create(String functionCode) {
        DataScopeInfo dataScopeInfo = new DataScopeInfo();
        // do something

        return dataScopeInfo;
    }

}
```

第二步、通过@Bean方式或给实现类添加@Component注解方式将DataScopeCreator注入到Spring中。

第三步、注入数据权限插件

```java
/**
 * 数据权限
 *
 * @return DataScopePlugin
 */
@Bean
public DataScopePlugin dataScopePlugin() {
	return new DataScopePlugin();
}
```

第四步、在需要做数据权限控制的方法上添加@DataScope(functionCode = 'functionCode')注解，`functionCode`为对应的功能编码。



## 多租户

注入如下Bean，并配置相关信息。

```java
@Bean
public TenantPlugin tenantPlugin() {
	List<String> whiteList = new ArrayList<>();
	// 加入白名单的具体方法路径不添加租户
	whiteList.add("com.opensef.mybatisext.test.mapper.SysUserMapper.findAllPage");
	whiteList.add("com.opensef.mybatisext.test.mapper.TestMapper.*");

	whiteList.add("com.opensef.mybatisext.system.*");

	TenantHandler<Long> tenantHandler = new TenantHandler<>(whiteList) {

		@Override
		public String getTenantColumnName() {
			// 租户数据库字段名
			return "tenant_id";
		}

		@Override
		public Long getTenantColumnValue() {
			// 租户值
			return UserDataUtils.getTenantId();
		}
	};

	return new TenantPlugin(tenantHandler);
}
```

## 设置分页方言

mybatis-ext会根据jdbc url自动设置分页方言，可以不单独设置。如果需要设置指定的方言，可在application.properties或application.yml配置文件中增加如下配置。

```
# 分页方言，非必须，系统会根据jdbc url自动判断
page.dialect.name=mysql
```
>如果使用了动态数据源插件，则使用如下配置
```
# 分页方言，非必须，系统会根据jdbc url自动判断
page.dialect.dynamic.ds1.name=mysql
```


## 自定义分页方言

mybatis-ext内置了常用数据库的分页方言。当内置的方言无法满足时，可实现Dialect接口自定义方言。

第一步、新建一个类，实现Dialect接口。

```
public class MyPageDialect implements Dialect {

    @Override
    public String toPageSql(String originalSql) {
    	// do something
        return null;
    }

    @Override
    public void setSqlAndParams(MetaObject metaObject, MappedStatement mappedStatement, BoundSql boundSql, long pageNum, long pageSize) {
		// do something
    }

}
```

第二步、将自定义方言注册到方言工厂，`dbname`为数据库驱动中的数据库名称，程序将根据数据库连接url解析出数据库名称，自动选择方言。

```java

@Component
public class MyDialectRegister implements DialectRegister {

    @Override
    public void register(Map<String, Dialect> dialectMap) {
        dialectMap.put("mysql", new MyPageDialect());
    }

}
```

