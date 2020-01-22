package cn.chitanda.gallery.ui.viewpager2


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cn.chitanda.gallery.data.model.PhotoItem
import com.chitanda.gallery.R
import kotlinx.android.synthetic.main.fragment_view_pager2.*
import kotlinx.android.synthetic.main.viewpager_itme.view.*
import kotlinx.coroutines.*

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
        saveButton.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                viewLifecycleOwner.lifecycleScope.launch { savePhoto() }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewLifecycleOwner.lifecycleScope.launch { savePhoto() }
                } else {
                    requireActivity().finish()
                }
            }
        }
    }

    @SuppressLint("InlinedApi")
    private suspend fun savePhoto() {
        withContext(Dispatchers.IO) {
            val holder =
                (viewpager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewpager2.currentItem)
                        as MViewHolder
            val photoItem = photoList?.get(viewpager2.currentItem)
            val bitmap = holder.itemView.pagerPhoto.drawable.toBitmap(
                photoItem?.imageWidth!!,
                photoItem.imageHigh
            )
            val values = ContentValues().also {
                it.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, "Pictures/" + "Gallery")
                //   it.put(MediaStore.Images.ImageColumns.DISPLAY_NAME,System.currentTimeMillis().toString()+".png")
            }
            val saveUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            ) ?: kotlin.run {
                MainScope().launch {
                    Toast.makeText(requireContext(), "Save Failed", Toast.LENGTH_SHORT).show()
                }
                return@withContext
            }
            requireContext().contentResolver.openOutputStream(saveUri).use {
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) {
                    MainScope().launch {
                        Toast.makeText(requireContext(), "Save Succeeded", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    MainScope().launch {
                        Toast.makeText(requireContext(), "Save Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

}
