package com.example.flickrbrowser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var rvAdapter: RVAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button

    private lateinit var btn20: Button
    private lateinit var btn30: Button
    private lateinit var btnAll: Button
    private lateinit var btnClear: Button

    private lateinit var displayImg: LinearLayout
    private lateinit var imgName: TextView
    private lateinit var btnBack: Button
    private lateinit var img: ImageView

    private var list = ArrayList<Data>()
    private var searchText = ""
    private var imagesNum = 5
    private var allPhotos = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)

        btn20 = findViewById(R.id.btn20)
        btn30 = findViewById(R.id.btn30)
        btnAll = findViewById(R.id.btnAll)
        btnClear = findViewById(R.id.btnClear)

        displayImg = findViewById(R.id.displayImg)
        imgName = findViewById(R.id.imgName)
        img = findViewById(R.id.img)
        btnBack = findViewById(R.id.btnBack)

        recyclerView = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(this, list)
        recyclerView.adapter = rvAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)


        btnBack.setOnClickListener {
            displayImg.isVisible = false
        }

        btn20.setOnClickListener {
            imagesNum = 20
            if (searchText.isNotEmpty()){
                //requestData()
                fetchDataByRetrofit()
            }
        }
        btn30.setOnClickListener {
            imagesNum = 30
            if (searchText.isNotEmpty()){
                //requestData()
                fetchDataByRetrofit()
            }
        }
        btnAll.setOnClickListener {
            imagesNum = allPhotos

            if (searchText.isNotEmpty()){
                //requestData()
                fetchDataByRetrofit()
            }
        }

        btnClear.setOnClickListener {
           list.clear()
            searchText = ""
            imagesNum = 5
            rvAdapter.notifyDataSetChanged()
        }

        btnSearch.setOnClickListener {
             searchText = etSearch.text.toString()

            if (searchText.isNotEmpty()){
                etSearch.clearFocus()
                etSearch.text.clear()
                //requestData()
                fetchDataByRetrofit()
            }
            else {
                Toast.makeText(this@MainActivity, "Please enter something", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun DisplayImg(image: String, name: String){
        displayImg.isVisible = true
        Glide.with(this).load(image).into(img)
        imgName.text = name
    }

    private fun fetchDataByRetrofit(){
        val url = "https://api.flickr.com/services/rest/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: API = retrofit.create(API::class.java)

        val urlId = "?method=flickr.photos.search&per_page=$imagesNum&api_key=bd375e289abac377bb759ec1f3db0204&tags=$searchText&format=json&nojsoncallback=1"
        val callApi = api.getData(urlId)

        callApi.enqueue(object : Callback<Photos> {
            override fun onResponse(call: Call<Photos>, response: Response<Photos>) {
                list.clear()

                allPhotos = response.body()!!.photos.pages

                for(item in response.body()!!.photos.photo){

                    val name = item.title
                    val server = item.server
                    val id = item.id
                    val secret = item.secret
                    val link = "https://live.staticflickr.com/$server/${id}_${secret}.jpg"
                    list.add(Data(link, name))
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<Photos>, t: Throwable) {
                Toast.makeText(this@MainActivity, "something wrong.", Toast.LENGTH_SHORT).show()
            }
        })
    }




        private fun requestData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = async { fetchData() }.await()
                    list.clear()
                    populateData(data)
                    Log.d("MAIN", data)
            }catch (e: Exception)
            {
                Log.d("Error","$e")
            }
        }
    }

    private fun fetchData():String {
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

        withContext(Main)
        {
            try {

                val jsonObject = JSONObject(result)
                Log.i("v","$jsonObject")

                val photos = jsonObject.getJSONObject("photos").getJSONArray("photo")
                allPhotos = jsonObject.getJSONObject("photos").getInt("total")
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