package com.github.hexindai.s3harding.mybatis

import com.github.hexindai.s3harding.core.annotation.S3harding
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Options

interface InsertMapper {

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id", paramName = "fromUserId")
    @Insert("insert into New_V_FundIO(num, from_user_id) values (#{num}, #{fromUserId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun insertAndGetNewVFundIO(insertData: InsertData): Int

}