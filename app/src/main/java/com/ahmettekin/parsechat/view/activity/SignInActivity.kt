package com.ahmettekin.parsechat.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ahmettekin.parsechat.R
import com.parse.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        configureListener()
    }

    override fun onResume() {
        super.onResume()
        loginControl()
    }

    private fun loginControl() {
        ParseUser.getCurrentUser()?.let {
            if(it.isAuthenticated){
                startActivity(Intent(applicationContext, ChatRoomsActivity::class.java))
                finish()
            }
        }
    }

    private fun configureListener() {
        btnLogin.setOnClickListener {
            if (etLoginMail.text.isNotEmpty() && etLoginPassword.text.isNotEmpty()) {
                progressBarLogin.visibility = View.VISIBLE
                val userName=etLoginMail.text.toString().substring(0,etLoginMail.text.toString().indexOf("@"))
                ParseUser.logInInBackground(userName,etLoginPassword.text.toString()
                ) { user, e ->
                    if (e!= null) {
                        progressBarLogin.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_LONG).show()
                    }else{
                        progressBarLogin.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext,"Welcome " + user.username.toString(),Toast.LENGTH_LONG).show()
                        startActivity(Intent(applicationContext, ChatRoomsActivity::class.java))
                        finish()
                    }
                }
            }
            else {
                Toast.makeText(this, "Boş Alanları Doldurunuz", Toast.LENGTH_SHORT).show()
            }
        }
        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

}