@file:JvmName("S3harding")

package com.github.hexindai.s3harding.core.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention
@MustBeDocumented
annotation class S3harding(val tableName: String, val shardingKey: String)