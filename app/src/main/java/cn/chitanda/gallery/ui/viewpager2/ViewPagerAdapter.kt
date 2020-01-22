package cn.chitanda.gallery.ui.viewpager2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.chitanda.gallery.data.model.PhotoItem
import com.bumptech.glide.Glide
import com.chitanda.gallery.R
import kotlinx.android.synthetic.main.viewpager_itme.view.*

class ViewPagerAdapter : ListAdapter<PhotoItem, MViewHolder>(
    DIFFCALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.viewpager_itme, parent, false)
            .apply {
                return MViewHolder(this)
            }
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(getItem(position).fullURL)
            .placeholder(R.drawable.ic_photo_gray_24dp)
            .into(holder.itemView.pagerPhoto)
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

    }
}

class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
}