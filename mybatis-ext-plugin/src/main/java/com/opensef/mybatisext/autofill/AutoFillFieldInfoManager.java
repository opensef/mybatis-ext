package com.opensef.mybatisext.autofill;

import com.opensef.mybatisext.annotation.AutoFillLogicDelete;
import com.opensef.mybatisext.annotation.Deleted;
import com.opensef.mybatisext.annotation.TableId;
import com.opensef.mybatisext.mapper.EntityManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自动填充属性管理器
 */
public class AutoFillFieldInfoManager {

    private final AutoFillHandler autoFillHandler;

    public AutoFillFieldInfoManager(AutoFillHandler autoFillHandler) {
        this.autoFillHandler = autoFillHandler;
    }

    // 自动填充信息缓存
    private final Map<Class<?>, AutoFillFieldInfo> autoFillFieldInfoMap = new ConcurrentHashMap<>();

    public AutoFillFieldInfo getAutoFillFieldInfo(Class<?> entityClass) {
        AutoFillFieldInfo autoFillFieldInfo = autoFillFieldInfoMap.get(entityClass);
        if (null != autoFillFieldInfo) {
            return autoFillFieldInfo;
        }

        autoFillFieldInfo = new AutoFillFieldInfo();
        autoFillFieldInfo.setInsertAutoFillFieldList(new ArrayList<>());
        autoFillFieldInfo.setUpdateAutoFillFieldList(new ArrayList<>());
        autoFillFieldInfo.setLogicDeleteAutoFillFieldList(new ArrayList<>());

        // 自动填充的属性及属性值
        Map<String, Object> insertAutoFillMap = new HashMap<>();
        Map<String, Object> updateAutoFillMap = new HashMap<>();
        if (null != autoFillHandler) {
            insertAutoFillMap = autoFillHandler.getInsertAutoFillPropertyValue();
            updateAutoFillMap = autoFillHandler.getUpdateAutoFillPropertyValue();
        }


        Field[] fields = EntityManager.getFields(entityClass);
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableId.class)) {
                // id
                autoFillFieldInfo.setIdField(field);
            } else if (field.isAnnotationPresent(Deleted.class) && field.getDeclaredAnnotation(Deleted.class).logicDelete()) {
                // 逻辑删除属性
                autoFillFieldInfo.setLogicDeletedField(field);
            } else {
                // 自动填充属性
                if (insertAutoFillMap.containsKey(field.getName())) {
                    autoFillFieldInfo.getInsertAutoFillFieldList().add(field);
                }
                if (updateAutoFillMap.containsKey(field.getName())) {
                    autoFillFieldInfo.getUpdateAutoFillFieldList().add(field);
                }
                if (field.isAnnotationPresent(AutoFillLogicDelete.class)) {
                    autoFillFieldInfo.getLogicDeleteAutoFillFieldList().add(field);
                }
            }
        }

        autoFillFieldInfoMap.put(entityClass, autoFillFieldInfo);
        return autoFillFieldInfo;
    }

}
