s3harding-mybatis
===

## Introduction

A simple key sharding interceptor for Mybatis. 

## Usage

1. Add this to pom.xml

```xml
<dependency>
  <groupId>com.github.hexindai.s3harding</groupId>
  <artifactId>s3harding-mybatis</artifactId>
  <version>${s3harding-mybatis.version}</version>
</dependency>
```

2. Register this interceptor

```xml
<!-- mybatis-config.xml -->
<plugins>
    <plugin interceptor="com.github.hexindai.s3harding.mybatis.ShardingInterceptor">
        <property name="shardingClass" value="com.github.hexindai.s3harding.core.MurmurHashSharding"/>
        <property name="tableNamePrefix" value="New_V_FundIO_"/>
        <property name="shardingCount" value="512"/>
        <property name="seed" value="12341234"/>
        <property name="numOfNodesPerTable" value="5"/>
    </plugin>
</plugins>
```

**Property `shardingClass` is a must, the others are optional which will be passed to "shardingClass" via `setProperties` method**

3. Add `@S3harding` to your mapper method, SQL will be modified automatically

```kotlin
interface Mapper {
    @S3harding(tableName = "New_V_FundIO", columnName = "id")
    @Select("select * from New_V_FundIO where id = 148407 limit 1")
    fun getOneNewVFundIO(): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "id", paramName = "id")
    @Select("select * from New_V_FundIO where id = #{id} limit 1")
    fun getOneNewVFundIOById(id: Int): NewVFundIO?
}
```