# mybatis-ext

## 简介

mybatis-ext 是一个mybatis的扩展框架，提供了通用的增删改查方法，可以更方便操作数据库、分页等。

## 特性

- 通用CRUD
- 支持通过Lambda方式构建查询
- 自定义扩展插件
- 逻辑删除
- 自动填充
- 数据权限
- 多租户

## 快速开始

> 1.添加maven依赖

```xml

<dependency>
    <groupId>com.opensefcom.opensef</groupId>
    <artifactId>mybatis-ext-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

> 2.继承BaseMapper

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser findByUsername(@Param("username") String username);

    Page<SysUser> findListPage(PageRequest pageRequest);

}
```

## 开发文档

详见[开发文档](./Document.md)

## License

[MIT © mybatis-ext](./LICENSE)
