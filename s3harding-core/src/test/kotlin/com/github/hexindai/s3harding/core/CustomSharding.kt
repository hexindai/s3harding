package com.github.hexindai.s3harding.core

class CustomSharding: MurmurHashSharding() {

    private fun tableName(mappedIndex: String): String {
        val prefix = this.getProperty("tableNamePrefix") as String? ?: throw IllegalArgumentException("Bad settings")
        return "$prefix$mappedIndex"
    }

    override fun getShardingTableName(shardKey: String) = when(shardKey) {
        "0" ->  tableName("0")
        "20" -> tableName("20")
        else -> super.getShardingTableName(shardKey)
    }

}