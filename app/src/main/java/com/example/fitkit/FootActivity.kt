package com.example.fitkit

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.android.synthetic.main.activity_foot.*
import java.io.*
import java.util.*

class FootActivity : AppCompatActivity() {
    //Shoe size table
    val footToEUShoeSize = mapOf("23" to "39","24" to "39","25" to "41","26" to "42",
        "27" to "43-44","28" to "44-45","29" to "46-47","30" to "47-48",
        "31" to "48-49")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foot)

    }

    val getImgCam = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it?.data == null){
            return@registerForActivityResult
        }else {


            var bitmap = it?.data?.extras?.get("data") as Bitmap
            if (bitmap.height < bitmap.width) {
                //Image is horizontal
                var matrix = Matrix()
                matrix.postRotate(90F)
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            }
            foot_iv.setImageBitmap(bitmap)
            if (!Python.isStarted())
                Python.start(AndroidPlatform(this))
            var py = Python.getInstance()
            var pyObj = py.getModule("measure") //python Script name

            val fileAbsPath = getImageAbsPath(bitmap)
            var obj = pyObj.callAttr("main", fileAbsPath) //(method name, argument input (img_path))
            var footLength: String = obj.toString() //return value
            foot_length_tv.text = "Estimated Foot length: " + footLength + " cm"
            shoe_size_tv.text = "Recommended shoe size: " + footToEUShoeSize[footLength.substring(
                0,
                footLength.indexOf(".")
            )] //rounding the foot length down
        }
    }

    val getImgGal = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if(it == null){
                return@ActivityResultCallback
            }
            var inputStream = contentResolver.openInputStream(it)
            var bitmap = BitmapFactory.decodeStream(inputStream)

            if(bitmap.height < bitmap.width){
                //Image is horizontal
                var matrix = Matrix()
                matrix.postRotate(90F)
                bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)

            }

            foot_iv.setImageBitmap(bitmap)



            if(!Python.isStarted())
                Python.start(AndroidPlatform(this))
            var py = Python.getInstance()
            var pyObj  = py.getModule("measure") //python Script name

            val fileAbsPath = getImageAbsPath(bitmap)
            var obj = pyObj.callAttr("main",fileAbsPath) //(method name, argument input (img_path))
            var footLength : String = obj.toString() //return value
            foot_length_tv.text = "Estimated Foot length: "+footLength + " cm"
            shoe_size_tv.text = "Recommended shoe size: "+footToEUShoeSize[footLength.substring(0,footLength.indexOf("."))] //rounding the foot length down
        })

    fun getImageAbsPath(bitmap:Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
//        Toast.makeText(this, file.absolutePath, Toast.LENGTH_LONG).show()

        return file.absolutePath
    }


    fun pickImageCamera(view: View){

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null)
            getImgCam.launch(intent)

    }

    fun pickImageGallery(view: View){
        getImgGal.launch("image/*")
    }
}