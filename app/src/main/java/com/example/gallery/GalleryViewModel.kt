package com.example.gallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive

    fun fetchData() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            getURL(),
            Response.Listener {
                _photoListLive.value = Gson().fromJson(it, Pixabay::class.java).hits.toList()
            },
            Response.ErrorListener {
                Log.d("ERR",it.toString())
            }
        )
        VolleySingleton.getINSTANCE(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getURL(): String {
        return "https://pixabay.com/api/?key=14598379-6f1338e6d1b1cbb8269b3abae&per_page=100"
    }
    private fun getURL(string: String): String {
        return "https://pixabay.com/api/?key=14598379-6f1338e6d1b1cbb8269b3abae&per_page=100&q=${string}"
    }
    fun fetchData(string: String){
        val stringRequest = StringRequest(
            Request.Method.GET,
            getURL(string),
            Response.Listener {
                _photoListLive.value = Gson().fromJson(it, Pixabay::class.java).hits.toList()
            },
            Response.ErrorListener {
                Log.d("ERR",it.toString())
            }
        )
        VolleySingleton.getINSTANCE(getApplication()).requestQueue.add(stringRequest)
    }
}