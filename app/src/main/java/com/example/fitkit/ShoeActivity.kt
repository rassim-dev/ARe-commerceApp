package com.example.fitkit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_shoe.*
import kotlin.random.Random

class ShoeActivity : AppCompatActivity(), ShoeAdapter.onShoeItemClickListener {
    val list = ArrayList<ShoeModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoe)
//        supportActionBar?.hide()

//        kivi_WebView.webViewClient = WebViewClient()
//        kivi_WebView.settings.javaScriptEnabled = true

        //Shoe Fetch Data from FireStore/////////////////////////////////////
        fetchShoeData()
//        shoes_RV.adapter?.notifyDataSetChanged()






//        shoes_RV.adapter = ShoeAdapter(list,this)
//        shoes_RV.layoutManager = GridLayoutManager(this,2)
////        shoes_RV.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
//
//        shoes_RV.setHasFixedSize(true) //for optimization purposes

    }

    override fun onShoeClick(shoePosition: Int) {
//        kivi_WebView.loadUrl(list.get(shoePosition).kiviLink)
//        val intent = Intent(this,ShoeWebActivity::class.java)
//        intent.putExtra("shoe try-on",list.get(shoePosition).kivi_Link)
//        startActivity(intent)

        val intent = Intent(this,ShoePageActivity::class.java)
        intent.putExtra("shoe img",list.get(shoePosition).img_URL)
        intent.putExtra("shoe name",list.get(shoePosition).name)
        intent.putExtra("shoe colors",list.get(shoePosition).color)
        intent.putExtra("shoe price",list.get(shoePosition).price)
        intent.putExtra("shoe desc",list.get(shoePosition).desc)
        intent.putExtra("kivi link",list.get(shoePosition).kivi_Link)

        startActivity(intent)


    }

    fun toFootMeasurement(view: View){
        val intent = Intent(this,FootActivity::class.java)
        startActivity(intent)

    }

    fun fetchShoeData(){
        var counter = 0
        val db = Firebase.firestore
        db.collection("sport_shoes").get().addOnSuccessListener { result ->
            for(doc in result){
//                Log.d("doc", "${doc.id} => ${doc.data}")
                val shoeItem = doc.toObject<ShoeModel>()
                //Fetch Storage for images
                val storage = Firebase.storage
                val storageRef = storage.reference.child("Catalog/Shoe/${doc.id}/").listAll()
                    .addOnSuccessListener { it ->
                        it.items.forEach{item ->
                            item.downloadUrl.addOnSuccessListener { url ->
                                Log.d("item",item.name)

                                //for filtering ONLY shoe icon images
                                var prefix = item.name.substring(0,item.name.indexOf(" "))
//                                Log.d("item",prefix)
                                if(prefix.equals("icon")){
                                    shoeItem.shoeIconImg_URL = url.toString()
                                    counter++

                                }else{
                                    shoeItem.img_URL = url.toString()
                                    counter++
                                }
                                //////////////////////////////////////



//                                Log.d("url",url.toString())
                                if(counter == 2){
                                    list.add(shoeItem)
                                    counter = 0

                                }


                                ///////////////////For Testing Purposes Only////////////////////////
                                if(list.size == 2){
                                    var i=0
                                    while(i < 10){
                                        list.add(list.get(i % 2))
                                        i++
                                    }
//                                    Log.d("list",list.size.toString())
                                    shoes_RV.adapter = ShoeAdapter(list,this,this)
                                    shoes_RV.layoutManager = GridLayoutManager(this,2)
                                    shoes_RV.adapter?.notifyDataSetChanged()
                                }
                                ////////////////////////////////////////////////////////////////////

//                                shoes_RV.adapter = ShoeAdapter(list,this,this)
//                                shoes_RV.layoutManager = GridLayoutManager(this,2)
//                                //shoes_RV.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
//
////                                shoes_RV.setHasFixedSize(true) //for optimization purposes


                            }
                        }
                    }
                    .addOnFailureListener { exce ->
                        Log.w("Storage", "Error in fetching the shoe images: "+exce.message,exce)
                    }




            }
        }
            .addOnFailureListener{ exc ->
                Log.w("Firestore", "Error in getting the documents: "+exc.message,exc)
            }

    }






}