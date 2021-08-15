package io.farewell12345.github.faqbot.DTO.model.dataclass

import java.util.*

data class Answer (
        val imgList : LinkedList<String>,
        val atList : LinkedList<Long>,
        val text : String,
        val xmlCardMsg:XmlCardMsg?
){
        data class XmlCardMsg(
                var id:Int,
                var content: String
        )
}