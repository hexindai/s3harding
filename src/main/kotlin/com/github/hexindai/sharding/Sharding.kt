package com.github.hexindai.sharding

interface Sharding {

    fun getShardingTableName(shardingKey: String): String

}