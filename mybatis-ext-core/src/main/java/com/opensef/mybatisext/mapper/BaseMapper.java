package com.opensef.mybatisext.mapper;

import com.opensef.mybatisext.Page;
import com.opensef.mybatisext.PageRequest;
import com.opensef.mybatisext.sqlbuilder.LambdaQuery;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;

/**
 * 自动生成单表简单增删改查
 */
public interface BaseMapper<T> {

    /**
     * 新增
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    @InsertProvider(type = SqlProvider.class, method = "insert")
    int insert(T entity);

    /**
     * 批量新增
     *
     * @param entityList 实体对象集合
     * @return 影响行数
     */
    @InsertProvider(type = SqlProvider.class, method = "insertBatch")
    int insertBatch(@Param(BaseMapperParamConstant.ENTITY_LIST) List<T> entityList);

    /**
     * 修改
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update(T entity);

    /**
     * 批量修改
     *
     * @param entityList 实体对象集合
     * @return 影响行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateBatch")
    int updateBatch(@Param(BaseMapperParamConstant.ENTITY_LIST) List<T> entityList);

    /**
     * 修改全部属性
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateAll")
    int updateAll(T entity);

    /**
     * 批量修改全部属性
     *
     * @param entityList 实体对象集合
     * @return 影响行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateAllBatch")
    int updateAllBatch(@Param(BaseMapperParamConstant.ENTITY_LIST) List<T> entityList);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    @DeleteProvider(type = SqlProvider.class, method = "delete")
    int delete(T entity);

    /**
     * 批量删除
     *
     * @param entityList 实体对象集合
     * @return 影响行数
     */
    @DeleteProvider(type = SqlProvider.class, method = "deleteBatch")
    int deleteBatch(@Param(BaseMapperParamConstant.ENTITY_LIST) List<T> entityList);

    /**
     * 根据ID删除
     *
     * @param id ID
     * @return 影响行数
     */
    @DeleteProvider(type = SqlProvider.class, method = "deleteById")
    int deleteById(@Param(BaseMapperParamConstant.ID) Serializable id);

    /**
     * 根据ID批量删除
     *
     * @param ids ID集合
     * @return 影响行数
     */
    @DeleteProvider(type = SqlProvider.class, method = "deleteBatchByIds")
    int deleteBatchByIds(@Param(BaseMapperParamConstant.ID_LIST) List<Serializable> ids);

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 实体对象
     */
    @SelectProvider(type = SqlProvider.class, method = "findById")
    T findById(@Param(BaseMapperParamConstant.ID) Serializable id);

    /**
     * 根据条件表达式查询一个对象
     *
     * @param lambdaQuery 条件表达式
     * @return 实体对象
     */
    @SelectProvider(type = SqlProvider.class, method = "findByLambdaQuery")
    T findOne(@Param(BaseMapperParamConstant.QUERY) LambdaQuery<T> lambdaQuery);

    /**
     * 根据条件表达式查询多个对象
     *
     * @param lambdaQuery 条件表达式
     * @return 实体对象集合
     */
    @SelectProvider(type = SqlProvider.class, method = "findByLambdaQuery")
    List<T> findList(@Param(BaseMapperParamConstant.QUERY) LambdaQuery<T> lambdaQuery);

    /**
     * 分页查询
     *
     * @param pageRequest 分页对象
     * @param lambdaQuery 条件表达式
     * @return 分页对象
     */
    @SelectProvider(type = SqlProvider.class, method = "findByLambdaQuery")
    Page<T> findPage(PageRequest pageRequest, @Param(BaseMapperParamConstant.QUERY) LambdaQuery<T> lambdaQuery);

    /**
     * 分页查询
     *
     * @param pageRequest 分页对象
     * @param lambdaQuery 条件表达式
     * @return 分页对象
     */
    @SelectProvider(type = SqlProvider.class, method = "findByLambdaQuery")
    List<T> findListPage(PageRequest pageRequest, @Param(BaseMapperParamConstant.QUERY) LambdaQuery<T> lambdaQuery);

    /**
     * 查询总数量
     *
     * @param lambdaQuery 条件表达式
     * @return 总数量
     */
    @SelectProvider(type = SqlProvider.class, method = "countByLambdaQuery")
    long count(@Param(BaseMapperParamConstant.QUERY) LambdaQuery<T> lambdaQuery);

}
