package io.farewell12345.github.faqbot.DTO.model


import io.farewell12345.github.faqbot.FuckOkhttp.FuckOkhttp
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

data class Pic(
    @SerializedName("code")
    val code: Int,
    @SerializedName("height")
    val height: String,
    @SerializedName("acgurl")
    val acgurl: String,
    @SerializedName("width")
    val width: String,
    @SerializedName("size")
    val size: String
)

