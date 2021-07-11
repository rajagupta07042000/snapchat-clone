package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    var emailEditText:EditText? =null
    var passwordEditText:EditText? =null
    val mAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText=findViewById(R.id.emailEditText)
        passwordEditText=findViewById(R.id.passwordEditText)
        if(mAuth.currentUser!=null)
        {
            logIn()
        }
    }
    fun goClicked(view: View){
        //Check if we can log in the user

        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    logIn()
                } else {
                    //sign up the user
                    mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(),passwordEditText?.text.toString())
                        .addOnCompleteListener(this) {task ->
                            if(task.isSuccessful)
                            {
                                //add to database
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user!!.uid).child("email").setValue(emailEditText?.text.toString())
                                logIn()
                            }
                            else
                            {
                                Toast.makeText(this,"Login Failed..Try Again",Toast.LENGTH_SHORT).show()
                            }

                        }
                }
            }
    }

    fun logIn()
    {
        //Move to Next activity
        val intent=Intent(this,SnapsActivity::class.java)
        startActivity(intent)
    }
}