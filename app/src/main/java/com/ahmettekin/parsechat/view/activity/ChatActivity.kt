package com.ahmettekin.parsechat.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.parsechat.R
import com.ahmettekin.parsechat.model.Message
import com.ahmettekin.parsechat.model.Room
import com.ahmettekin.parsechat.view.adapter.ChatAdapter
import com.parse.*
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var mMessages: ArrayList<Message>
    lateinit var mAdapter: ChatAdapter
    private lateinit var currentRoom: Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        currentRoom = intent.getSerializableExtra("room") as Room
        configureUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_log_out -> {
                ParseUser.logOut()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
                true
            }
            R.id.menu_join_room -> {
                val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Rooms")
                query.getInBackground(currentRoom.objectId) { room, e ->
                    if (e == null) {
                        room.add("userIdList", ParseUser.getCurrentUser().objectId)
                        room.saveInBackground {
                            if (it == null) {
                                println("success")
                                joinControl()
                            } else {
                                println("error1:" + it.localizedMessage)
                            }
                        }
                    } else {
                        println("error2: " + e.localizedMessage)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun configureUI() {
        mMessages = ArrayList()
        val userId = ParseUser.getCurrentUser().objectId
        mAdapter = ChatAdapter(this@ChatActivity, userId, mMessages)
        rvChat.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }
    }

    fun sendMessage(view: View?) {
        val message = ParseObject("Message")
        message.put("userId", ParseUser.getCurrentUser().objectId)
        message.put("body", etMessage.text.toString())
        message.saveInBackground {
            if (it != null) {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Message Sent!", Toast.LENGTH_LONG).show()
                saveMessageIdToCurrentRoom(message.objectId)
            }
        }
    }

    private fun saveMessageIdToCurrentRoom(messageId:String){
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Rooms")
        query.getInBackground(currentRoom.objectId) { room, e ->
            room.add("messageIdList",messageId)
            room.saveInBackground()
        }
    }

    private fun setupMessages() {
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Message")
        query.limit = 50
        query.orderByAscending("createdAt")
        query.findInBackground { messages, e ->
            if (e == null) {
                getMessagesInCurrentRoom(messages)
               } else {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getMessagesInCurrentRoom(messages: MutableList<ParseObject>) {
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Rooms")
        query.getInBackground(currentRoom.objectId) { room, e ->
            mMessages.clear()
            for (tempMessage in messages) {
                if (room.getList<String>("messageIdList")?.contains(tempMessage.objectId) == true) {
                    val message = Message(tempMessage.getString("userId"), tempMessage.getString("body"))
                    mMessages.add(message)
                }
            }
            runOnUiThread {
                mAdapter.notifyDataSetChanged()
                rvChat.scrollToPosition(mMessages.size - 1)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        joinControl()
        setupMessages()
        changeChatLive()
    }

    private fun joinControl() {
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Rooms")
        query.getInBackground(currentRoom.objectId) { room, e ->

            if (e == null) {
                val userIdList = room.getList<String>("userIdList")
                currentRoom.userIdList = userIdList as ArrayList<String>?
                if (currentRoom.userIdList?.contains(ParseUser.getCurrentUser().objectId) == false) {
                    btnSend.visibility = View.INVISIBLE
                    etMessage.visibility = View.INVISIBLE
                } else {
                    btnSend.visibility = View.VISIBLE
                    etMessage.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun changeChatLive() {
        val parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
        val parseQuery: ParseQuery<ParseObject> = ParseQuery.getQuery("Message")
        parseQuery.limit = 50
        parseQuery.orderByAscending("createdAt")
        val subscriptionHandling: SubscriptionHandling<ParseObject> = parseLiveQueryClient.subscribe(parseQuery)
        subscriptionHandling.handleEvents(object : SubscriptionHandling.HandleEventsCallback<ParseObject> {
            override fun onEvents(query: ParseQuery<ParseObject>?, event: SubscriptionHandling.Event?, `object`: ParseObject?) {
                val handler = Handler(Looper.getMainLooper())
                handler.post(object : Runnable{
                    override fun run() {
                        println("tetiklendi")
                        query?.findInBackground { messages, e ->
                            if (e == null) {
                                getMessagesInCurrentRoom(messages)
                            } else {
                                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            }
        })
    }

}