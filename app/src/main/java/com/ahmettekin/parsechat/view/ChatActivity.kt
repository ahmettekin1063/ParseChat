package com.ahmettekin.parsechat.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.parsechat.ChatAdapter
import com.ahmettekin.parsechat.model.Message
import com.ahmettekin.parsechat.R
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
            myHandler.postDelayed(this,POLL_INTERVAL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setupMessagePosting()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_log_out -> {
                ParseUser.logOut()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupMessagePosting() {
        mMessages = ArrayList()
        val userId = ParseUser.getCurrentUser().objectId
        mAdapter = ChatAdapter(this@ChatActivity, userId, mMessages)
        rvChat.adapter= mAdapter
        rvChat.layoutManager = LinearLayoutManager(this@ChatActivity)

        btnSend.setOnClickListener {
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
        }
    }

    private fun refreshMessages() {
        val query: ParseQuery<Message> = ParseQuery.getQuery(
            Message::class.java)
        query.limit = 50
        query.orderByAscending("createdAt")
        query.findInBackground { objects, e ->
            if (e == null) {
                mMessages.clear()
                mMessages.addAll(objects)
                mAdapter.notifyDataSetChanged()
                if (mFirstLoad) {
                    rvChat.scrollToPosition(mMessages.size-1)
                    mFirstLoad = false
                }
            } else {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        myHandler.postDelayed(mRefreshMessagesRunnable,POLL_INTERVAL)
    }

    override fun onPause() {
        myHandler.removeCallbacksAndMessages(null)
        super.onPause()
    }

}