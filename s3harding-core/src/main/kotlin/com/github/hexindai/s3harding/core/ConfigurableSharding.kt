package com.github.hexindai.s3harding.core

import java.util.*

interface ConfigurableSharding : Sharding {

    val numberOfTable: Int

    val numberOfNode: Int

    fun setProperties(properties: Properties?)

    fun getProperties(): Properties

    fun getProperty(key: Any): Any?

}