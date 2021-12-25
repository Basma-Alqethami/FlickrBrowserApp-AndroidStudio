package com.example.flickrbrowserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var rvAdapter: RVAdapter
    lateinit var recyclerView : RecyclerView
    lateinit var etSearch: EditText
    lateinit var btnSearch: Button

    lateinit var btn20: Button
    lateinit var btn30: Button
    lateinit var btnAll: Button
    lateinit var btnClear: Button
    lateinit var tvSearch: TextView

    var list = ArrayList<Data>()
    var searchText = ""
    var imagesNum = 5
    var number = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)

        btn20 = findViewById(R.id.btn20)
        btn30 = findViewById(R.id.btn30)
        btnAll = findViewById(R.id.btnAll)
        btnClear = findViewById(R.id.btnClear)
        tvSearch = findViewById(R.id.tvSearch)

        btn20.setOnClickListener {
            imagesNum = 20
            list.clear()
            if (searchText.isNotEmpty()){
            requestData()}
        }
        btn30.setOnClickListener {
            imagesNum = 30
            list.clear()
            if (searchText.isNotEmpty()){
                requestData()}
        }
        btnAll.setOnClickListener {
            imagesNum = number
            list.clear()
            if (searchText.isNotEmpty()){
                requestData()}
        }

        btnClear.setOnClickListener {
           list.clear()
            tvSearch.setText("Search: ")
            searchText = ""
            imagesNum = 5
            rvAdapter.notifyDataSetChanged()
        }

        recyclerView = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(list)
        recyclerView.adapter = rvAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnSearch.setOnClickListener {
            if (searchText.isNotEmpty()){
            searchText = "$searchText,${etSearch.text}"}
            else{searchText = "${etSearch.text}"}

            if (searchText.isNotEmpty()){
                tvSearch.setText("Search: $searchText")
                etSearch.clearFocus()
                etSearch.text.clear()
                requestData()
                //fetchDataBy_Retrofit ()
            }
            else {
                Toast.makeText(this@MainActivity, "Please enter something", Toast.LENGTH_SHORT).show()
            }
        }
    }


        private fun requestData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = async { fetchData() }.await()
                if (data.isNotEmpty()) {
                    list.clear()
                    populateData(data)
                    Log.d("MAIN", "$data")
                } else {
                    withContext(Main) {
                        Toast.makeText(this@MainActivity, "wrong id or does not exist.", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e: Exception)
            {
                Log.d("Error","$e")
            }
        }
    }

    fun fetchData():String {
        var response = ""
        try {
            response = URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&per_page=$imagesNum&api_key=bd375e289abac377bb759ec1f3db0204&tags=$searchText&format=json&nojsoncallback=1").readText()
        }catch (e: Exception)
        {
            Log.d("Error","$e")
        }
        return response
    }

    private suspend fun populateData(result: String) {

        withContext(Dispatchers.Main)
        {
            try {

                val jsonObject = JSONObject(result)
                Log.i("v","$jsonObject")

                val photos = jsonObject.getJSONObject("photos").getJSONArray("photo")
                number = jsonObject.getJSONObject("photos").getInt("total")
                for(i in 0 until photos.length()){

                    val name = photos.getJSONObject(i).getString("title")
                    val server = photos.getJSONObject(i).getString("server")
                    val id = photos.getJSONObject(i).getString("id")
                    val secret = photos.getJSONObject(i).getString("secret")

                    val link = "https://live.staticflickr.com/$server/${id}_${secret}.jpg"
                    list.add(Data(link, name))
                }
                rvAdapter.notifyDataSetChanged()
            }catch (e: Exception){
                Log.d("Error","$e")
            }
        }
    }
}