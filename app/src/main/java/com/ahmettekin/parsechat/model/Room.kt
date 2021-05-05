package com.ahmettekin.parsechat.model

import java.io.Serializable

class Room(val objectId:String?, val name: String?, val adminUserId: String?, var userIdList: ArrayList<String>?,var messageIdList: ArrayList<String>?): Serializable
