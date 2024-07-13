package com.opensef.mybatisext.resultmap;

import com.opensef.mybatisext.Page;
import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;
import com.opensef.mybatisext.util.MybatisExtUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * ResultMap映射处理
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
})
public class ResultMapPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = MybatisExtUtil.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        if (resultMaps.size() == 0) {
            invocation.proceed();
        }


        ResultMap resultMap = resultMaps.get(0);
        // 将处理器的ResultMapping转为Map
        Map<String, ResultMapping> resultMappingMap = resultMap.getResultMappings().stream().collect(Collectors.toMap(ResultMapping::getProperty, resultMapping -> resultMapping));

        Class<?> returnType = resultMap.getType();
        //BaseMapper里返回Page泛型处理，默认识别到的是Page，这里将其改为Page里的泛型对象
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
            metaObject.setValue("delegate.mappedStatement.resultMaps", newResultMaps);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
