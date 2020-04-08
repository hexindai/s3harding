package com.github.hexindai.s3harding.core

import java.util.*

interface Sharding {

    fun getShardingTableName(shardKey: String): String

}