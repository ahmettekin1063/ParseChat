package com.ahmettekin.parsechat.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ahmettekin.parsechat.R
import com.parse.ParseObject
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_add_room.*

class AddRoomFragment() : DialogFragment(), View.OnClickListener {
    lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_room,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext= view.context
        btnCreateRoom.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.btnCreateRoom -> {
                val room = ParseObject("Rooms")
                room.put("name", etRoomName.text.toString())
                room.put("messageIdList", ArrayList<String>())
                room.put("adminUserId", ParseUser.getCurrentUser().objectId)
                val userIdList= ArrayList<String>()
                userIdList.add(ParseUser.getCurrentUser().objectId)
                room.put("userIdList", userIdList)
                room.saveInBackground {
                    if (it != null) {
                        println(it.localizedMessage)
                        Toast.makeText(mContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(mContext, "Room Created!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        dialog?.dismiss()
    }

}