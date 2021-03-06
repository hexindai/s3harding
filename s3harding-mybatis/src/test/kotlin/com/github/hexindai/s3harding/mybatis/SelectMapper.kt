package com.github.hexindai.s3harding.mybatis

import com.github.hexindai.s3harding.core.annotation.S3harding
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

interface SelectMapper {

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id")
    @Select("select * from New_V_FundIO where from_user_id = 148407 limit 1")
    fun getOneNewVFundIO(): NewVFundIO?

    @Select("select * from New_V_FundIO_449 where from_user_id = 148407 limit 1")
    fun getOneNewVFundIOWithoutS3harding(): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id", paramName = "fromUserId")
    @Select("select * from New_V_FundIO where from_user_id = #{fromUserId} and num = #{num} limit 1")
    fun getOneNewVFundIOByData(data: SearchData): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id", paramName = "fromUserId")
    @Select("select * from New_V_FundIO where from_user_id = #{fromUserId} limit 1")
    fun getOneNewVFundIOByFromUserId(@Param("fromUserId") fromUserId: Int): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id", paramName = "param1")
    @Select("select * from New_V_FundIO where from_user_id = #{param1} limit 1")
    fun getOneNewVFundIOByFromUserIdWithParam(fromUserId: Int): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id", paramName = "fromUserId")
    @Select("select * from New_V_FundIO where \${columnName} = #{fromUserId} limit 1")
    fun getOneNewVFundIOByFromUserIdWithParamAndColumnName(
            @Param("fromUserId") fromUserId: Int,
            @Param("columnName") columnName: String = "from_user_id"
    ): NewVFundIO?

}