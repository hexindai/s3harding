package com.github.hexindai.s3harding.mybatis

import com.github.hexindai.s3harding.core.annotation.S3harding
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

interface Mapper {
    @S3harding(tableName = "New_V_FundIO", columnName = "id")
    @Select("select * from New_V_FundIO where id = 148407 limit 1")
    fun getOneNewVFundIO(): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "id")
    @Select("select * from New_V_FundIO where id = #{id} limit 1")
    fun getOneNewFundIOById(id: Int): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "id")
    @Select("select * from New_V_FundIO where id = #{id} and num = #{num} limit 1")
    fun getOneNewFundIOByData(data: SearchData): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id")
    @Select("select * from New_V_FundIO where from_user_id = #{from_user_id} limit 1")
    fun getOneNewFundIOByData2(data: SearchData): NewVFundIO?

    @S3harding(tableName = "New_V_FundIO", columnName = "from_user_id")
    @Select("select * from New_V_FundIO where from_user_id = #{from_user_id} limit 1")
    fun getOneNewFundIOByFromUserId(@Param("from_user_id") fromUserId: Int): NewVFundIO?
}