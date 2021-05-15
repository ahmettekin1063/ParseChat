package com.ahmettekin.parsechat.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.parsechat.R
import com.ahmettekin.parsechat.model.Room
import com.ahmettekin.parsechat.view.adapter.ChatRoomsAdapter
import com.ahmettekin.parsechat.view.fragment.AddRoomFragment
import com.parse.*
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import kotlinx.android.synthetic.main.activity_chat_rooms.*

class ChatRoomsActivity : AppCompatActivity() {
    lateinit var roomList: ArrayList<Room>
    lateinit var mAdapter: ChatRoomsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_rooms)
        configureUI()
    }

    override fun onResume() {
        super.onResume()
        setupRoomList()
        changeRoomLive()
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
        AddRoomFragment().show(supportFragmentManager,"ADD_ROOM")
    }

    private fun changeRoomLive(){
        val parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
        val parseQuery: ParseQuery<ParseObject> = ParseQuery.getQuery("Rooms")
        parseQuery.orderByAscending("createdAt")
        val subscriptionHandling: SubscriptionHandling<ParseObject> = parseLiveQueryClient.subscribe(parseQuery)
        subscriptionHandling.handleEvents(object : SubscriptionHandling.HandleEventsCallback<ParseObject>{
            override fun onEvents(query: ParseQuery<ParseObject>?, event: SubscriptionHandling.Event?, `object`: ParseObject?) {
                val handler = Handler(Looper.getMainLooper())
                handler.post(object : Runnable{
                    override fun run() {
                        println("tetiklendi")
                        query?.findInBackground(object : FindCallback<ParseObject>{
                            override fun done(rooms: MutableList<ParseObject>?, e: ParseException?) {
                                if (e== null){
                                    rooms?.let {
                                        roomList.clear()
                                        for(tempRoom in it){
                                            val room = Room(tempRoom.objectId,
                                                tempRoom.getString("name"),
                                                tempRoom.getString("adminUserId"),
                                                tempRoom.getList<String>("userIdList") as ArrayList<String>,
                                                tempRoom.getList<String>("messageIdList") as ArrayList<String>)
                                            roomList.add(room)
                                        }
                                        runOnUiThread {
                                            mAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }else{
                                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                            }

                        })
                    }

                })
            }

        })

    }

}