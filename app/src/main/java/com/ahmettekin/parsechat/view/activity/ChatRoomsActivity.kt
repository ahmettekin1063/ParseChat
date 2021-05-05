package com.ahmettekin.parsechat.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.parsechat.R
import com.ahmettekin.parsechat.Refreshable
import com.ahmettekin.parsechat.model.Room
import com.ahmettekin.parsechat.view.adapter.ChatRoomsAdapter
import com.ahmettekin.parsechat.view.fragment.AddRoomFragment
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_chat_rooms.*
import java.util.concurrent.TimeUnit

class ChatRoomsActivity : AppCompatActivity() {
    lateinit var roomList: ArrayList<Room>
    lateinit var mAdapter: ChatRoomsAdapter
    val myHandler = Handler()
    val POLL_INTERVAL = TimeUnit.SECONDS.toMillis(1)

    private val mRefreshRoomsRunnable = object : Runnable{
        override fun run() {
            setupRoomList()
            myHandler.postDelayed(this, POLL_INTERVAL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_rooms)
        configureUI()
    }

    override fun onResume() {
        super.onResume()
        myHandler.postDelayed(mRefreshRoomsRunnable, POLL_INTERVAL)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.rooms_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.rooms_menu_log_out -> {
                ParseUser.logOut()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun configureUI() {
        roomList = ArrayList()
        mAdapter = ChatRoomsAdapter(roomList, this)
        rvChatRooms.apply {
            adapter= mAdapter
            layoutManager= LinearLayoutManager(this@ChatRoomsActivity)
        }
        setupRoomList()
    }

    private fun setupRoomList() {
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Rooms")
        query.orderByAscending("createdAt")
        query.findInBackground { rooms, e ->
            if(e == null && rooms != null){
                roomList.clear()
                for(tempRoom in rooms){
                    val room = Room(tempRoom.objectId,
                        tempRoom.getString("name"),
                        tempRoom.getString("adminUserId"),
                        tempRoom.getList<String>("userIdList") as ArrayList<String>,
                        tempRoom.getList<String>("messageIdList") as ArrayList<String>)
                    roomList.add(room)
                }
                mAdapter.notifyDataSetChanged()
            }else {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showAddRoomFragment(view: View?){
        AddRoomFragment(object: Refreshable{
            override fun refresh() {
                setupRoomList()
            }
        }).show(supportFragmentManager,"ADD_ROOM")
    }
}