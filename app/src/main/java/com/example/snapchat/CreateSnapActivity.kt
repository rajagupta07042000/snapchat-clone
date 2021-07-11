package com.example.snapchat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.ByteArrayOutputStream
import java.util.*


@Suppress("DEPRECATION")
class CreateSnapActivity : AppCompatActivity() {

    var createSnapImageView:ImageView?=null
    var messageEditText:EditText?=null
    val imageName= UUID.randomUUID().toString()+".jpg"  //for unique name of image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        createSnapImageView=findViewById(R.id.createSnapImageView)
        messageEditText=findViewById(R.id.messageEditText)
    }

    fun getPhoto()
    {
        val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,1)
    }

    fun chooseImageClicked(view:View)
    {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else
        {
            getPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage=data!!.data
        if(requestCode==1&&resultCode==Activity.RESULT_OK&&data!=null)
        {
            try{
                val bitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)
            }catch(e:Exception)
            {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==1){
            if(grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getPhoto()
            }
        }
    }
    //uploading photo on firebase
    fun nextClicked(view:View)
    {
        // Get the data from an ImageView as bytes
        createSnapImageView?.isDrawingCacheEnabled
        createSnapImageView?.buildDrawingCache()
        val bitmap = createSnapImageView?.getDrawingCache()
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask=FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads

            Toast.makeText(this,"UploadFailed",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...

            val downloadURL = taskSnapshot.storage.child("images").child(imageName).downloadUrl




            Log.i("URL",downloadURL.toString())

            val intent=Intent(this,ChooseUserActivity::class.java)
            intent.putExtra("imageURL",downloadURL.toString())
            intent.putExtra("imageName",imageName)
            intent.putExtra("message",messageEditText?.text.toString())
            startActivity(intent)

        }
    }

}
