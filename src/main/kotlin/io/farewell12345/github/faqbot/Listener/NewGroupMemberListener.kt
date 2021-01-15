package io.farewell12345.github.faqbot.Listener
import com.google.gson.Gson
import io.farewell12345.github.faqbot.BotManager.BotsManager
import io.farewell12345.github.faqbot.BotManager.CommandGroupList
import io.farewell12345.github.faqbot.DTO.model.dataclass.Answer
import io.farewell12345.github.faqbot.DTO.model.searchWelcomeTalk
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.message.data.*

class NewGroupMemberListener : BaseListeners() {
    @EventHandler
    suspend fun MemberJoinEvent.onEvent() {
        val user = this.member
        val groupId = this.group.id
        if (groupId in CommandGroupList.welcomeGroupList) {
            val gson = Gson()
            val talk = gson.fromJson(searchWelcomeTalk(group), Answer::class.java)
            val messageChain = buildMessageChain {
                +At(user)
                talk.atList.forEach {
                    append(At(Bot.instances[0].getGroup(group.id)?.get(it)!!))
                }
                talk.imgList.forEach {
                    +Image(it)
                }
                +PlainText(talk.text)
            }

            BotsManager.oneBot?.getGroup(groupId)!!.sendMessage(messageChain)
        }
    }
}