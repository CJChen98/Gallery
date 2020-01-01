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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.SavePic -> {
                if (checkpermission()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        var result: Boolean? = null
                        withContext(Dispatchers.IO) {
                            result = download(requireContext()) ?: false
                        }
                        withContext(Dispatchers.Main) {
                            if (result!!) {
                                Toast.makeText(
                                    requireContext(),
                                    "Save Successed",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                Toast.makeText(requireContext(), "Save Failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InlinedApi")
    private fun download(context: Context): Boolean {
        var result: Boolean? = null
        val futureTarget = Glide.with(requireContext())
            .asBitmap()
            .load(photoList?.get(viewpager2.currentItem)?.fullURL)
            .submit()
        val file = futureTarget.get()
        val saveFileName = System.currentTimeMillis().toString() + ".jpg"
        val extenal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val values = ContentValues().also {
            it.put(MediaStore.Images.Media.DESCRIPTION, "This is an image")
            it.put(MediaStore.Images.Media.DISPLAY_NAME, saveFileName)
            it.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            it.put(MediaStore.Images.Media.TITLE, "Image.jpg")
            it.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Gallery")
        }
        val resolver = context.contentResolver
        val insertUri = resolver.insert(extenal, values)
        var ops: OutputStream? = null
        try {
            if (insertUri != null) {
                ops = resolver.openOutputStream(insertUri)
            }
            if (ops != null) {
                result = file.compress(Bitmap.CompressFormat.JPEG, 100, ops)
                ops.flush()
            }
        } catch (e: IOException) {
        } finally {
            ops?.close()
        }
        return result!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        photoList = arguments?.getParcelableArrayList<PhotoItem>("List")

        ViewPagerAdapter().apply {
            viewpager2.adapter = this
            submitList(photoList)
        }
        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = "${position + 1}/${photoList?.size}"
            }
        })
        viewpager2.setCurrentItem(arguments?.getInt("pos") ?: 0, false)
    }

    private fun checkpermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            val alert: AlertDialog? = context.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setTitle("权限申请")
                    setMessage("为了使应用功能正常使用，请允许以下权限")
                    setNegativeButton(
                        "不",
                        DialogInterface.OnClickListener { dialog, which -> onDestroy() })
                    setPositiveButton(
                        "好",
                        DialogInterface.OnClickListener { dialog, which ->
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1
                            )
                        })
                }
                builder.create()
            }
            alert?.show()
            return false
        } else {
            return true
        }
    }

}
