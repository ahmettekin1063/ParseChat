package com.ahmettekin.parsechat.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.parsechat.R
import com.ahmettekin.parsechat.model.Message
import com.ahmettekin.parsechat.view.adapter.ChatAdapter
import com.parse.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.concurrent.TimeUnit

class ChatActivity : AppCompatActivity() {
    lateinit var mMessages: ArrayList<Message>
    lateinit var mAdapter: ChatAdapter
    var mFirstLoad:Boolean = true
    val POLL_INTERVAL = TimeUnit.SECONDS.toMillis(1)
    val myHandler = Handler()

    private val mRefreshMessagesRunnable = object : Runnable{
        override fun run() {
            refreshMessages()
            myHandler.postDelayed(this, POLL_INTERVAL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setupMessagePosting()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_log_out -> {
                ParseUser.logOut()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
                true
            }else-> super.onOptionsItemSelected(item)
        }
    }

    private fun setupMessagePosting() {
        mMessages = ArrayList()
        val userId = ParseUser.getCurrentUser().objectId
        mAdapter = ChatAdapter(this@ChatActivity, userId, mMessages)

        rvChat.apply {
            adapter=mAdapter
            layoutManager= LinearLayoutManager(this@ChatActivity)
        }

        /*btnSend.setOnClickListener {
            val data = etMessage.text.toString()
            val message = Message()
            message.userId = ParseUser.getCurrentUser().objectId
            message.body = data
            message.saveInBackground {
                if(it!=null){
                    Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Success",Toast.LENGTH_SHORT).show()
                    refreshMessages()
                }
            }
            etMessage.text=null
        }*/

        btnSend.setOnClickListener {
            val message = ParseObject("Message")
            message.put("userId", ParseUser.getCurrentUser().objectId)
            message.put("body", etMessage.text.toString())
            message.saveInBackground {
                if (it != null) {
                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                    refreshMessages()
                } else {
                    Toast.makeText(applicationContext, "Message Sent!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun refreshMessages() {
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Message")
        query.limit = 50
        query.orderByAscending("createdAt")
        query.findInBackground { messages, e ->
            if (e == null) {
                mMessages.clear()
                for (tempMessage in messages){
                    val message = Message(tempMessage.getString("userId"), tempMessage.getString("body"))
                    mMessages.add(message)
                }
                mAdapter.notifyDataSetChanged()
                if (mFirstLoad) {
                    rvChat.scrollToPosition(mMessages.size - 1)
                    mFirstLoad = false
                }
            } else {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL)
    }

    override fun onPause() {
        myHandler.removeCallbacksAndMessages(null)
        super.onPause()
    }

}