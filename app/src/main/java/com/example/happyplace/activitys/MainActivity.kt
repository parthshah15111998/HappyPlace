package com.example.happyplace.activitys

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplace.adapter.HappyPlacesAdapter
import com.example.happyplace.database.DatabaseHandler
import com.example.happyplace.databinding.ActivityMainBinding
import com.example.happyplace.models.HappyPlaceModel
import com.example.happyplace.utils.SwipeToDeleteCallBack
import com.example.happyplace.utils.SwipeToEditCallBack

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    companion object{
        const val  ADD_ACTIVITY_REQUEST_CODE=1
        const val EXTRA_PLACE_DETAIL="extra_place_detail"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fBtnAddHappyPlace.setOnClickListener {
            startActivityForResult(Intent(this, AddHappyPlaceActivity::class.java),ADD_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlaceListFromLocalDB()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                getHappyPlaceListFromLocalDB()
            }else{
                Log.e("Activity","Cancel or Back Press")
            }
        }
    }

    private fun setupHappyPlaceRecyclerView(happyPlaceList:ArrayList<HappyPlaceModel>){

        binding.rvHappyPlaceList.layoutManager=LinearLayoutManager(this)
        binding.rvHappyPlaceList.setHasFixedSize(true)
         val placeAdapter=HappyPlacesAdapter(this,happyPlaceList)
        binding.rvHappyPlaceList.adapter=placeAdapter

        placeAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent=(Intent(this@MainActivity,HappyPlaceDetailActivity::class.java))
                intent.putExtra(EXTRA_PLACE_DETAIL,model)
                startActivity(intent)
            }
        })
        
        val editSwipeHandler = object : SwipeToEditCallBack(this    ) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvHappyPlaceList.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition,ADD_ACTIVITY_REQUEST_CODE)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvHappyPlaceList)

        val deleteSwipeHandler = object : SwipeToDeleteCallBack(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvHappyPlaceList.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getHappyPlaceListFromLocalDB()

            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvHappyPlaceList)

    }

    private fun getHappyPlaceListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList:ArrayList<HappyPlaceModel> = dbHandler.getHappyPlaceList()

        if (getHappyPlaceList.size > 0) {
             binding.rvHappyPlaceList.visibility= View.VISIBLE
           binding.tvNoRecord.visibility= View.GONE
            setupHappyPlaceRecyclerView(getHappyPlaceList)
        }
        else
        {
            binding.rvHappyPlaceList.visibility= View.GONE
            binding.tvNoRecord.visibility= View.VISIBLE
        }

    }
}