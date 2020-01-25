package cn.chitanda.gallery.ui.gallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.chitanda.gallery.data.model.PhotoItem
import cn.chitanda.gallery.data.model.Pixabay
import cn.chitanda.gallery.data.network.VolleySingleton
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlin.math.ceil

const val DATA_STATUS_CAN_LOAD_MORE = 0
const val DATA_STATUS_NO_MORE = 1
const val DATA_STATUS_NETWORK_ERROR = 2

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _dataStatusLive = MutableLiveData<Int>()
    val dataStatusLive: MutableLiveData<Int> get() = _dataStatusLive
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive
    var needToScrollToTop = true
    private val perPage = 20
    private var currentPage = 1
    private var totalPage = 1
    var currentKey = ""
    private var isNewQuery = true
    private var isLaoding = false

    init {
        resetQuery()
    }

    fun resetQuery() {
        currentPage = 1
        totalPage = 1
        currentKey = ""
        isNewQuery = true
        needToScrollToTop = true
        fetchData()
    }

    fun fetchData() {
        if (isLaoding) return
        if (currentPage > totalPage) {
            _dataStatusLive.value =
                DATA_STATUS_NO_MORE
            return
        }
        isLaoding = true
        val stringRequest = StringRequest(
            Request.Method.GET,
            getURL(),
            Response.Listener {
                with(Gson().fromJson(it, Pixabay::class.java)) {
                    totalPage = ceil(totalHits.toDouble() / perPage).toInt()
                    if (isNewQuery) {
                        _photoListLive.value = hits.toList()
                    } else {
                        _photoListLive.value =
                            arrayListOf(_photoListLive.value!!, hits.toList()).flatten()
                    }
                }
                isLaoding = false
                isNewQuery = false
                currentPage++
            },
            Response.ErrorListener {
                _dataStatusLive.value =
                    DATA_STATUS_NETWORK_ERROR
                isLaoding = false
            }
        )
        VolleySingleton.getINSTANCE(getApplication())
            .requestQueue.add(stringRequest)
        Log.d("cont", currentPage.toString())
    }

    private fun getURL(): String {
        return "https://pixabay.com/api/?key=14598379-6f1338e6d1b1cbb8269b3abae&per_page=${perPage}&q=${currentKey}&page=${currentPage}"
    }

}