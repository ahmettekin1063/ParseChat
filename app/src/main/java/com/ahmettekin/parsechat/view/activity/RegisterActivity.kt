package com.ahmettekin.parsechat.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ahmettekin.parsechat.R
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        configureListener()
    }

    private fun configureListener() {
        btnRegister.setOnClickListener {
            if (etMail.text.isNotEmpty() && etSifre.text.isNotEmpty() && etSifreTekrar.text.isNotEmpty()) {
                if (etSifre.text.toString() == etSifreTekrar.text.toString()){
                    progressBarRegister.visibility= View.VISIBLE
                    newUserAccount(etMail.text.toString(),etSifre.text.toString())
                }else{
                    progressBarRegister.visibility= View.INVISIBLE
                    Toast.makeText(this,"Şifreler Aynı Değil", Toast.LENGTH_SHORT).show()
                }
            }else{
                progressBarRegister.visibility= View.INVISIBLE
                Toast.makeText(this,"Boş Alanları Doldurunuz", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun newUserAccount(email: String, password: String) {
        val user = ParseUser()
        user.username=email.substring(0,email.indexOf("@"))
        user.setPassword(password)
        user.email=email

        user.signUpInBackground {
            if (it != null) {
                progressBarRegister.visibility= View.INVISIBLE
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                progressBarRegister.visibility= View.INVISIBLE
                Toast.makeText(applicationContext,"Your Account Created\nPlease Verify your E-mail",Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}