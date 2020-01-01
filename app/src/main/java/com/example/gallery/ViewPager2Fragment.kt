package com.example.gallery


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_view_pager2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream

/**
 * A simple [Fragment] subclass.
 */
class ViewPager2Fragment : Fragment() {
    private var photoList: ArrayList<PhotoItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager2, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        photoList = arguments?.getParcelableArrayList<PhotoItem>("List")

        ViewPagerAdapter().apply {
            viewpager2.adapter = this
            submitList(photoList)
        }
        photoTag.text = "${1}/${photoList?.size}"
        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = "${position + 1}/${photoList?.size}"
            }
        })
        viewpager2.setCurrentItem(arguments?.getInt("pos") ?: 0, false)
    }


}
