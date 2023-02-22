package com.nicadevelop.nicavpn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class IntroActivity : AppCompatActivity() {

    private var btnContinue:Button ?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        btnContinue=findViewById(R.id.btnContinueToApp);

       btnContinue?.setOnClickListener(View.OnClickListener {
           val intent = Intent(this@IntroActivity, MainActivity::class.java)
           startActivity(intent)
        })
    }
}