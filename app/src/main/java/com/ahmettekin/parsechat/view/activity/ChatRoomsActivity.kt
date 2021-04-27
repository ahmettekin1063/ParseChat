package com.ahmettekin.parsechat.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.parsechat.R
import com.ahmettekin.parsechat.model.Room
import com.ahmettekin.parsechat.view.adapter.ChatRoomsAdapter
import com.ahmettekin.parsechat.view.fragment.AddRoomFragment
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.android.synthetic.main.activity_chat_rooms.*

class ChatRoomsActivity : AppCompatActivity() {
    lateinit var roomList: ArrayList<Room>
    lateinit var mAdapter: ChatRoomsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_rooms)
        configureUI()
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

    internal fun setupRoomList() {
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Rooms")
        query.orderByAscending("createdAt")
        query.findInBackground { rooms, e ->
            if(e == null){
                roomList.clear()
                for(tempRoom in rooms){
                    val room = Room(tempRoom.getString("name"))
                    roomList.add(room)
                }
                mAdapter.notifyDataSetChanged()
            }else {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showAddRoomFragment(view: View?){
        AddRoomFragment(this).show(supportFragmentManager,"ADD_ROOM")
    }
}