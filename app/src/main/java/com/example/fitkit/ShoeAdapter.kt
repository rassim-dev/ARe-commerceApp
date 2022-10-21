package com.example.fitkit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.shoe_item.view.*

class ShoeAdapter(private val shoeList: ArrayList<ShoeModel>,
                  private val listener: onShoeItemClickListener,
                  private val context : Context
): RecyclerView.Adapter<ShoeAdapter.ShoeViewHolder>() {

    inner class ShoeViewHolder(itemView : View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener{
        val shoeImgView : ImageView = itemView.shoe_img_small
        val shoeNameTV : TextView = itemView.shoe_name
        val shoePriceTV : TextView = itemView.shoe_price


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            listener.onShoeClick(position)
        }

    }

    interface onShoeItemClickListener{
        fun onShoeClick(shoePosition: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.shoe_item,
            parent,false)
        return ShoeViewHolder(itemView)
    }

    override fun onBindViewHolder(currholder: ShoeViewHolder, currPosition: Int) {
        val currShoe = shoeList.get(currPosition)
        Glide.with(context).load(currShoe.shoeIconImg_URL).placeholder(R.drawable.placeholder).into(currholder.shoeImgView)
        currholder.shoeNameTV.text = currShoe.name
        currholder.shoePriceTV.text = currShoe.price

//        currholder.imgView.setBackgroundColor(currShoe.BD)


    }

    override fun getItemCount() = shoeList.size

}