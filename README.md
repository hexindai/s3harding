s3harding
===

**Work in progress. Do not use it in a production deployment. 😄**

## Introduction

A sharding strategy based on MurmurHash algorithm which is used for generating sharding table names

## Usage

1. Register this interceptor

```
<!-- mybatis-config.xml -->
<plugins>
    <plugin interceptor="com.github.hexindai.s3harding.mybatis.ShardingInterceptor"/>
</plugins>
```

2. Add `@S3harding` to your mapper method, SQL will be modified automatically

```kotlin
interface Mapper {
    @S3harding(tableName = "New_V_FundIO", shardingKey = "148407")
    @Select("select * from New_V_FundIO limit 1")
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