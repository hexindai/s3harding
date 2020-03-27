s3harding
===

**Work in progress. Do not use it in a production deployment. 😄**

## Introduction

A simple sharding strategy based on key sharding. 

Rewriting SQL for sharding. We can custom any sharding strategy based shard key which is used for generating sharded SQL.
Currently, shards are logical that all shards are in a database node(this is dumb). In future, it will support separate
database nodes.

### Features:

* Key based sharding (hash based sharding)
* Only one database node (dumb)

## Usage

1. Register this interceptor

```xml
<!-- mybatis-config.xml -->
<plugins>
    <plugin interceptor="com.github.hexindai.s3harding.mybatis.ShardingInterceptor"/>
</plugins>
```

2. Add `@S3harding` to your mapper method, SQL will be modified automatically

```kotlin
interface Mapper {
    @S3harding(tableName = "New_V_FundIO", columnName = "id")
    @Select("select * from New_V_FundIO where id = 148407 limit 1")
    fun getOneNewVFundIO(): NewVFundIO
}
```

3. Enjoy

## Run tests

```shell script
./gradlew test
```

## Contribution

If you find some bugs, please commit your PRs.