package com.opensef.mybatisext.autofill;

import com.opensef.mybatisext.annotation.TableId;
import com.opensef.mybatisext.exception.MybatisExtException;
import com.opensef.mybatisext.idhandler.*;
import com.opensef.mybatisext.mapper.BaseMapperParamConstant;
import com.opensef.mybatisext.util.ExtTypeUtil;
import com.opensef.mybatisext.util.MybatisExtUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公共字段填充
 * id自动赋值也在此类中处理
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class AutoFillPlugin implements Interceptor {

    private AutoFillHandler autoFillHandler;
    // 自定义全局ID处理器
    private IdHandler<?> customIdHandler;

    private static final IdHandler<Long> longIdHandler = new IdGeneratorLong();
    private static final IdHandler<String> uuidIdHandler = new IdGeneratorUUID();

    private AutoFillFieldInfoManager autoFillFieldInfoManager;

    // 自定义ID处理器（不包括自定义全局ID处理器）
    private static final Map<Class<?>, IdHandler<?>> customIdHandlerMap = new ConcurrentHashMap<>();

    public AutoFillPlugin(AutoFillHandler autoFillHandler, IdHandler<?> customIdHandler) {
        this.autoFillHandler = autoFillHandler;
        this.customIdHandler = customIdHandler;
        this.autoFillFieldInfoManager = new AutoFillFieldInfoManager(autoFillHandler);
    }

    public AutoFillPlugin() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // 获取SQL类型
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        // 获取参数
        Object parameter = invocation.getArgs()[1];

        // 如果参数为null，将其设置为Map
        if (parameter == null) {
            MetaObject metaObject = SystemMetaObject.forObject(invocation);
            metaObject.setValue("args[1]", new LinkedHashMap<>() {
            });
            parameter = invocation.getArgs()[1];
        }
        // 如果是一个基本类型参数，将args1的参数转为Map
        else if (ExtTypeUtil.isBuiltInType(parameter)) {
            Method currentMethod = MybatisExtUtil.getCurrentMethod(mappedStatement);

            Parameter[] parameters = currentMethod.getParameters();

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(parameters[0].getName(), parameter);

            MetaObject metaObject = SystemMetaObject.forObject(invocation);
            metaObject.setValue("args[1]", paramMap);
            parameter = invocation.getArgs()[1];
        }

        // 如果参数是Map类型，如@Param参数传参，或者直接用Map传参
        if (parameter instanceof Map) {
            Map<String, Object> params = (Map<String, Object>) parameter;
            // 处理map类型参数
            setMapParamAutoFill(mappedStatement, sqlCommandType, params);

            // 如果包含实体参数，则对实体进行进一步处理
            if (params.containsKey(BaseMapperParamConstant.ENTITY_LIST)) {
                if (ExtTypeUtil.isCollection(params.get(BaseMapperParamConstant.ENTITY_LIST))) {
                    Collection<?> collection = (Collection<?>) params.get(BaseMapperParamConstant.ENTITY_LIST);
                    for (Object object : collection) {
                        // 设置实体填充信息
                        setEntityAutoFill(object, sqlCommandType);
                    }
                } else if (ExtTypeUtil.isArray(params.get(BaseMapperParamConstant.ENTITY_LIST))) {
                    Object[] array = (Object[]) params.get(BaseMapperParamConstant.ENTITY_LIST);
                    for (Object object : array) {
                        // 设置实体填充信息
                        setEntityAutoFill(object, sqlCommandType);
                    }
                }
            }
        } else if (!ExtTypeUtil.isCollectionOrArray(parameter)) {
            // 设置实体填充信息
            setEntityAutoFill(parameter, sqlCommandType);
        } else if (ExtTypeUtil.isCollection(parameter)) {
            Collection<?> collection = (Collection<?>) parameter;
            for (Object object : collection) {
                // 设置实体填充信息
                setEntityAutoFill(object, sqlCommandType);
            }
        } else if (ExtTypeUtil.isArray(parameter)) {
            Object[] array = (Object[]) parameter;
            for (Object object : array) {
                // 设置实体填充信息
                setEntityAutoFill(object, sqlCommandType);
            }
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

    // 设置实体填充信息
    private void setEntityAutoFill(Object entity, SqlCommandType sqlCommandType) {
        AutoFillFieldInfo autoFillFieldInfo = autoFillFieldInfoManager.getAutoFillFieldInfo(entity.getClass());

        if (sqlCommandType.equals(SqlCommandType.INSERT)) {
            // 获取id属性 Field
            Field idField = autoFillFieldInfo.getIdField();
            if (null != idField) {
                setIdAutoFill(idField, entity);
            }

            // 获取逻辑删除属性 Field
            Field logicDeletedField = autoFillFieldInfo.getLogicDeletedField();
            if (null != logicDeletedField) {
                setLogicDeletedAutoFill(logicDeletedField, autoFillHandler.logicDeletedNormalValue(), entity);
            }

            // 设置自动填充字段
            if (null != autoFillFieldInfo.getInsertAutoFillFieldList() && autoFillFieldInfo.getInsertAutoFillFieldList().size() > 0) {
                setAutoFill(autoFillFieldInfo.getInsertAutoFillFieldList(), autoFillHandler.getInsertAutoFillPropertyValue(), entity);
            }
        } else if (sqlCommandType.equals(SqlCommandType.UPDATE)) {
            // 设置自动填充字段
            if (null != autoFillFieldInfo.getUpdateAutoFillFieldList() && autoFillFieldInfo.getUpdateAutoFillFieldList().size() > 0) {
                setAutoFill(autoFillFieldInfo.getUpdateAutoFillFieldList(), autoFillHandler.getUpdateAutoFillPropertyValue(), entity);
            }
        } else if (sqlCommandType.equals(SqlCommandType.DELETE)) {
            // 如果是逻辑删除，执行下面的逻辑
            Field logicDeletedField = autoFillFieldInfo.getLogicDeletedField();
            if (null != logicDeletedField) {
                setLogicDeletedAutoFill(logicDeletedField, autoFillHandler.logicDeletedValue(), entity);
            }

            // 设置自动填充字段
            if (null != autoFillFieldInfo.getLogicDeleteAutoFillFieldList() && autoFillFieldInfo.getLogicDeleteAutoFillFieldList().size() > 0) {
                setAutoFill(autoFillFieldInfo.getLogicDeleteAutoFillFieldList(), autoFillHandler.getUpdateAutoFillPropertyValue(), entity);
            }
        }
    }

    // 设置Map类型参数的实体填充信息
    private void setMapParamAutoFill(MappedStatement mappedStatement, SqlCommandType sqlCommandType, Map<String, Object> params) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf(".")));
        if (clazz.getGenericInterfaces().length == 0) {
            return;
        }

        // 获取泛型的实体
        Class<?> baseMapperClass = MybatisExtUtil.getGenericInterface(clazz);
        AutoFillFieldInfo autoFillFieldInfo = autoFillFieldInfoManager.getAutoFillFieldInfo(baseMapperClass);

        if (sqlCommandType.equals(SqlCommandType.UPDATE)) {
            Map<String, Object> autoFillMap = autoFillHandler.getUpdateAutoFillPropertyValue();
            params.putAll(autoFillMap);
        } else if (sqlCommandType.equals(SqlCommandType.DELETE)) {
            Map<String, Object> autoFillMap = autoFillHandler.getUpdateAutoFillPropertyValue();
            params.putAll(autoFillMap);

            // 如果是逻辑删除，执行下面的逻辑
            Field logicDeletedField = autoFillFieldInfo.getLogicDeletedField();
            if (null != logicDeletedField) {
                params.put(logicDeletedField.getName(), autoFillHandler.logicDeletedValue());
            }

            // 设置逻辑删除自动填充字段
            if (null != autoFillFieldInfo.getLogicDeleteAutoFillFieldList() && autoFillFieldInfo.getLogicDeleteAutoFillFieldList().size() > 0) {
                for (Field field : autoFillFieldInfo.getLogicDeleteAutoFillFieldList()) {
                    params.put(field.getName(), autoFillHandler.getUpdateAutoFillPropertyValue().get(field.getName()));
                }
            }
        }
    }

    /**
     * id自动赋值
     *
     * @param field  Field
     * @param entity 实体
     */
    private void setIdAutoFill(Field field, Object entity) {
        field.setAccessible(true);
        try {
            if (null != field.get(entity)) {
                return;
            }
            TableId fieldAnnotation = field.getAnnotation(TableId.class);
            if (fieldAnnotation.type().equals(IdType.AUTO)) {
                Long id = longIdHandler.getId();
                if (ExtTypeUtil.isNumber(id)) {
                    field.set(entity, id);
                } else {
                    field.set(entity, String.valueOf(id));
                }
            } else if (fieldAnnotation.type().equals(IdType.UUID)) {
                field.set(entity, uuidIdHandler.getId());
            } else if (fieldAnnotation.type().equals(IdType.CUSTOM)) {
                // 如果是自定义ID生成器，判断是否存在单独指定的ID生成器，如果没有，则使用全局自定义的ID生成器
                if (fieldAnnotation.idHandler().equals(IdGeneratorNone.class)) {
                    field.set(entity, customIdHandler.getId());
                } else {
                    IdHandler<?> idHandler = customIdHandlerMap.get(fieldAnnotation.idHandler());
                    if (null == idHandler) {
                        idHandler = fieldAnnotation.idHandler().getDeclaredConstructor().newInstance();
                    }
                    field.set(entity, idHandler.getId());
                }

            } else if (fieldAnnotation.type().equals(IdType.DB_AUTO)) {
                // ID自增时，在Mapper接口里使用注解设置，不在此处做处理
            } else {
                throw new MybatisExtException("未找到对应的ID生成器");
            }


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 逻辑删除自动赋值
     *
     * @param field  Field
     * @param entity 实体
     */
    private void setLogicDeletedAutoFill(Field field, Object value, Object entity) {
        field.setAccessible(true);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增或修改时属性自动赋值
     *
     * @param insertAutoFillFieldList 新增时自动赋值属性集合
     * @param autoFillMap             自动填充属性值
     * @param entity                  实体
     */
    private void setAutoFill(List<Field> insertAutoFillFieldList, Map<String, Object> autoFillMap, Object entity) {
        if (null == autoFillMap || autoFillMap.size() == 0) {
            return;
        }
        // 自动填充的属性及属性值
        for (Field field : insertAutoFillFieldList) {
            field.setAccessible(true);
            Object value = autoFillMap.get(field.getName());
            if (value != null) {
                try {
                    field.set(entity, value);
                    /*if (null == field.get(entity)) {
                        field.set(entity, value);
                    }*/
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
