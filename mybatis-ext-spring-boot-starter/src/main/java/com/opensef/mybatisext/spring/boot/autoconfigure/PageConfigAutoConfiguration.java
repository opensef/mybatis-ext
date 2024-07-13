package com.opensef.mybatisext.spring.boot.autoconfigure;

import com.opensef.mybatisext.Page;
import com.opensef.mybatisext.exception.MybatisExtException;
import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;
import com.opensef.mybatisext.page.dialect.DialectFactory;
import com.opensef.mybatisext.page.dialect.DialectProperties;
import com.opensef.mybatisext.page.dialect.DialectRegister;
import com.opensef.mybatisext.page.spring.PageObjectFactory;
import com.opensef.mybatisext.page.spring.PageObjectWrapperFactory;
import com.opensef.mybatisext.util.MybatisExtUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分页配置，支持mybatis直接返回Page对象
 */
@Configuration
@ConditionalOnClass(SqlSessionFactory.class)
public class PageConfigAutoConfiguration {

    private final List<SqlSessionFactory> sqlSessionFactoryList;

    public PageConfigAutoConfiguration(List<SqlSessionFactory> sqlSessionFactoryList) {
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

    /**
     * 分页返回对象处理，支持mybatis直接返回Page对象
     * 配置自定义的 ObjectFactory和ObjectWrapperFactory
     *
     * @return List<SqlSessionFactory>
     */
    @SuppressWarnings("all")
    @Bean
    public List<SqlSessionFactory> sqlSessionFactoryList() {
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            configuration.setObjectFactory(new PageObjectFactory());
            configuration.setObjectWrapperFactory(new PageObjectWrapperFactory());

            // 这里必须用Object接收，否则会报Configuration$StrictMap$Ambiguity cannot be cast to class错误
            for (Object mappedStatement : configuration.getMappedStatements()) {
                if (mappedStatement instanceof MappedStatement) {
                    try {
                        parseResultMapping((MappedStatement) mappedStatement);
                    } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
                             IllegalAccessException e) {
                        throw new MybatisExtException(e);
                    }
                }
            }
        }
        return sqlSessionFactoryList;
    }

    /**
     * 方言配置
     *
     * @return DialectProperties
     */
    @Bean
    public DialectProperties dialectProperties() {
        return new DialectProperties();
    }

    /**
     * 方言工厂
     *
     * @param dialectRegisters 方言注册器
     * @return DialectFactory
     */
    @Bean
    public DialectFactory dialectFactory(List<DialectRegister> dialectRegisters, DialectProperties dialectProperties) {
        return new DialectFactory(dialectRegisters, dialectProperties);
    }

    /**
     * ResultMapping处理
     * 1、解决BaseMapper里返回Page泛型处理，默认识别到的是Page，这里将其处理成Page里的泛型对象
     * 2、实体类添加TypeHandler注解处理
     *
     * @param mappedStatement MappedStatement
     * @throws NoSuchMethodException     异常
     * @throws InvocationTargetException 异常
     * @throws InstantiationException    异常
     * @throws IllegalAccessException    异常
     */
    private void parseResultMapping(MappedStatement mappedStatement) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        if (resultMaps.size() == 0) {
            return;
        }

        MetaObject metaObject = SystemMetaObject.forObject(mappedStatement);


        ResultMap resultMap = resultMaps.get(0);
        // 将处理器的ResultMapping转为Map
        Map<String, ResultMapping> resultMappingMap = resultMap.getResultMappings().stream().collect(Collectors.toMap(ResultMapping::getProperty, resultMapping -> resultMapping));

        Class<?> returnType = resultMap.getType();
        // BaseMapper里返回Page泛型处理，默认识别到的是Page，这里将其改为Page里的泛型对象
        if (returnType.equals(Page.class)) {
            Method currentMethod = MybatisExtUtil.getCurrentMethod(mappedStatement);
            if (currentMethod != null) {
                Type resolvedReturnType = TypeParameterResolver.resolveReturnType(currentMethod, MybatisExtUtil.getCurrentClass(mappedStatement));
                ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Type returnTypeParameter = actualTypeArguments[0];
                    returnType = (Class<?>) returnTypeParameter;
                }
            }
        }

        EntityInfo entityInfo = EntityManager.getEntityInfo(returnType);
        List<EntityInfo.FieldInfo> fieldInfoList = entityInfo.getFieldInfoList();

        List<ResultMapping> resultMappingList = new ArrayList<>(resultMap.getResultMappings());

        if (null != fieldInfoList && fieldInfoList.size() > 0) {
            for (EntityInfo.FieldInfo fieldInfo : fieldInfoList) {
                // 实体类上自定义了TypeHandler，且没有手动在xml里指定TypeHandler时，则程序自动添加
                if (null != fieldInfo.getTypeHandler() && !fieldInfo.getTypeHandler().equals(UnknownTypeHandler.class)
                        && !resultMappingMap.containsKey(fieldInfo.getFieldName())) {
                    TypeHandler<?> typeHandler = fieldInfo.getTypeHandler().getDeclaredConstructor().newInstance();
                    ResultMapping resultMapping = new ResultMapping.Builder(mappedStatement.getConfiguration(), fieldInfo.getFieldName(), fieldInfo.getColumnName(), typeHandler).build();
                    resultMappingList.add(resultMapping);
                }
            }
        }

        if (resultMappingList.size() > 0 || resultMap.getType().equals(Page.class)) {
            ResultMap newResultMap = new ResultMap.Builder(mappedStatement.getConfiguration(), resultMap.getId(), returnType, resultMappingList).build();
            List<ResultMap> newResultMaps = new ArrayList<>();
            newResultMaps.add(newResultMap);
            metaObject.setValue("resultMaps", newResultMaps);
        }
    }

}
