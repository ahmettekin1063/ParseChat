package com.ahmettekin.parsechat.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmettekin.parsechat.R
import com.ahmettekin.parsechat.model.Room
import com.ahmettekin.parsechat.view.activity.ChatActivity
import com.ahmettekin.parsechat.view.adapter.ChatRoomsAdapter.RoomViewHolder
import kotlinx.android.synthetic.main.row_chatrooms_layout.view.*

class ChatRoomsAdapter(private val roomList:List<Room>, private val mContext:Context) : RecyclerView.Adapter<RoomViewHolder>() {

    class RoomViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_chatrooms_layout,parent,false))
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.itemView.tvRoomName.text = roomList[position].name
        holder.itemView.setOnClickListener {
            goToChatActivity(roomList[position])
            //TODO: Create a new activity and go to activity that you created. Then configure adding user to room operations.
            //TODO: If current user hasn't joined yet, they shouldn't see the messages.
        }
    }

    private fun goToChatActivity(room: Room) {
        val intent= Intent(mContext,ChatActivity::class.java)
        intent.putExtra("room", room)
        mContext.startActivity(intent)
    }

    override fun getItemCount()= roomList.size
}


