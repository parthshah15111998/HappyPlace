package com.example.happyplace.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplace.activitys.AddHappyPlaceActivity
import com.example.happyplace.activitys.MainActivity
import com.example.happyplace.database.DatabaseHandler
import com.example.happyplace.databinding.ItemHappyPlaceBinding
import com.example.happyplace.models.HappyPlaceModel


open class HappyPlacesAdapter(
    private val context: Context, private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnClickListener? = null
    private var EXTRA_PLACE_DETAIL="extra_place_detail"

     class MyViewHolder(val binding: ItemHappyPlaceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {

            holder.binding.ivPlaceImage.setImageURI(Uri.parse(model.image))
            holder.binding.tvTitle.text = model.title
            holder.binding.tvDescription.text = model.description

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    fun notifyEditItem(activity:Activity,position: Int,requestCode:Int){
        val intent=Intent(context,AddHappyPlaceActivity::class.java)
        intent.putExtra(EXTRA_PLACE_DETAIL,list[position])
        activity.startActivityForResult(intent,requestCode)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeAt(position: Int) {
        val dbHandler = DatabaseHandler(context)
        val isDeleted = dbHandler.deleteHappyPlace(list[position])
        if (isDeleted > 0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int,model:HappyPlaceModel)
    }
}
