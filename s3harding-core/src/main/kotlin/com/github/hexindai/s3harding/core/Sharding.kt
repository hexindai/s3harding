package com.github.hexindai.s3harding.core

interface Sharding {

    fun getShardingTableName(shardKey: String): String

}