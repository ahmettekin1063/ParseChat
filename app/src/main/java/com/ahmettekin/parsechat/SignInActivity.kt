package com.ahmettekin.parsechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.parse.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        configureListener()
        /*FirebaseAuth.getInstance().signInWithEmailAndPassword(etLoginMail.text.toString(), etLoginPassword.text.toString())
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    progressBarLogin.visibility = View.INVISIBLE
                    task.result?.user?.let {

                        if (!it.isEmailVerified) {
                            FirebaseAuth.getInstance().signOut()
                        }
                    }
                } else {
                    progressBarLogin.visibility = View.INVISIBLE
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }*/

/*
        val parseObj = ParseObject("Fruits")
        val parseObj2 = ParseObject("Fruits")
        parseObj.put("name","apple")
        parseObj.put("calories",200)

        parseObj2.put("name","orange")
        parseObj2.put("calories",100)

        parseObj.saveInBackground {

            if (it!=null){
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"Successful1",Toast.LENGTH_SHORT).show()
            }
        }

        parseObj2.saveInBackground {

            if (it!=null){
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"Successful2",Toast.LENGTH_SHORT).show()
            }
        }*/
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