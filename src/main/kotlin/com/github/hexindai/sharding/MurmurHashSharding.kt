package com.github.hexindai.sharding

import org.apache.commons.codec.digest.MurmurHash2
import java.util.*

class MurmurHashSharding(
        private val tableNamePrefix: String,
        private val shardingCount: Int = 512,
        private val numOfNodes: Int = 10
) : Sharding {

    private val nodes: SortedMap<Long, String> = TreeMap()

    private val tableNames = mutableListOf<String>()

    init {
        assert(shardingCount > 0 && numOfNodes > 0) {
            "Parameter shardingCount and numOfNodes may not less then zero"
        }
        for (i in 0..shardingCount) {
            val tableName = "$tableNamePrefix$i"
            tableNames.add(tableName)
            for (n in 0..numOfNodes) {
                nodes[this.hash("${tableName.toLowerCase()}*$n")] = tableName
            }
        }
    }

    private fun hash(shardingKey: String): Long {
        val bytes = shardingKey.toByteArray(Charsets.UTF_8)
        return MurmurHash2.hash64(bytes, bytes.size, 0x1234ABCD)
    }

    override fun getShardingTableName(shardingKey: String): String {
        val hashShardingKey = this.hash(shardingKey)
        val sortedMap = this.nodes.tailMap(hashShardingKey)
        if (sortedMap.isEmpty()) {
            return nodes[nodes.firstKey()]!!
        }
        return nodes[sortedMap.firstKey()]!!
    }

}