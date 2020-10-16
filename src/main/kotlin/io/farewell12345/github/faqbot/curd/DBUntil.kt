@file:Suppress(
    "EXPERIMENTAL_API_USAGE",
    "DEPRECATION_ERROR",
    "OverridingDeprecatedMember",
    "INVISIBLE_REFERENCE",
    "INVISIBLE_MEMBER"
)
package io.farewell12345.github.faqbot.curd

import io.farewell12345.github.faqbot.BotManager.Session
import io.farewell12345.github.faqbot.DB.DB
import io.farewell12345.github.faqbot.DTO.Answer
import io.farewell12345.github.faqbot.DTO.Question
import com.google.gson.Gson
import io.farewell12345.github.faqbot.DTO.Welcome
import me.liuwj.ktorm.dsl.*
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.OnlineGroupImage
import net.mamoe.mirai.message.data.PlainText
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.util.StackLocatorUtil
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.*
import kotlin.math.log

fun logger(): Logger {
    return org.apache.logging.log4j.LogManager.getLogger(StackLocatorUtil.getStackTraceElement(2).className)
}

fun searchWelcomeTalk(group:Group): String? {
    try{
        val query = DB.database
            .from(Welcome)
            .select()
            .where {
                (Welcome.group eq group.id)
            }
        query.forEach {
            return it[Welcome.talk]
        }
    }catch (e:Exception){
        logger().info(e)
    }
    return null
}

fun appendWelcomeTalk(group: Group,talk:Answer):Boolean{
    if (searchWelcomeTalk(group)==null){
        val gson = Gson()
        DB.database.insert(Welcome){
            set(Welcome.group,group.id)
            set(Welcome.talk,gson.toJson(talk))
        }
        return true
    }
    return false
}

fun upDateWelcomeTalk(group:Group,talk:Answer):Boolean{
    try {
        val gson = Gson()
        DB.database.update(Welcome) {
            set(Welcome.talk, gson.toJson(talk))
            where {
                it.group eq group.id
            }
        }
        return true
    }catch (e:Exception){
        logger().info(e)
    }
    return false
}

fun deleteQuestion(question: String,group: Group):Boolean{
    try {
        if (searchQuestion(question,group) !=null) {
            DB.database.delete(Question) {
                it.question eq question
            }
            return true
        }
    }catch (e:Exception){
        logger().info(e)
    }
    return false
}

fun changeWelcome(group:Group,messageChain: MessageChain){
    var imgList = LinkedList<String>()
    var atList = LinkedList<Long>()
    var text = ""
    messageChain.forEach {
        when(it){
            is OnlineGroupImage ->{
                downImg(it)
                imgList.add(it.imageId)
            }
            is At ->{
                atList.add(it.target)
            }
            is PlainText ->{
                text =it.content
            }
        }
    }
    upDateWelcomeTalk(group, Answer(imgList, atList, text))
}

fun searchQuestion(question:String,group: Group): QueryRowSet? {
    try {
        val query = DB.database
                .from(Question)
                .select()
                .where {
                    (Question.question eq question) and (Question.group eq group.id)
                }
        query.forEach {
            return it
        }
    }catch (e:Exception) {

    }
    return null
}

fun quickSearchQuestion(id:Int,group: Group): QueryRowSet? {
    val query= DB.database
            .from(Question)
            .select()
            .where {
                (Question.id eq id) and (Question.group eq group.id)
            }
    query.forEach {
        return it
    }
    return null
}


fun downImg(imgUrl:OnlineGroupImage){
    val file = File("img",imgUrl.imageId)
    if (!file.exists()){
        file.createNewFile()
    }
    val output = file.outputStream()
    URL(imgUrl.originUrl).openStream().use {
        it.copyTo(output)
    }
    output.close()
}



fun upDateQuestionAnswer(message: GroupMessageEvent, session: Session): Boolean {
    val imgList = LinkedList<String>()
    val atList = LinkedList<Long>()
    var text = ""
    message.message.forEach {
        when(it){
            is OnlineGroupImage ->{
                downImg(it)
                imgList.add(it.imageId)
            }
            is At ->{
                atList.add(it.target)
            }
            is PlainText ->{
                text =it.content
            }
        }
    }
    try {
        upDate(
            answer = Answer(imgList, atList, text) ,
            session = session
        )
        return true
    }catch (e: Exception){
        logger().info(e)
    }
    return false
}

fun upDate(answer: Answer, session: Session){
    val gson = Gson()
    val json = gson.toJson(answer)
    DB.database.update(Question) {
        it.answer to json
        it.lastEditUser to session.user
        where {
            (Question.question eq session.question) and (Question.group eq session.group)
        }
    }
}