package com.example.gallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_item.view.*
import java.util.ArrayList

class GalleryAdapter : ListAdapter<PhotoItem, MyViewHolder>(DIFFCALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        )
        holder.itemView.setOnClickListener {
            Bundle().apply {
                putParcelableArrayList("List", ArrayList(currentList))
                putInt("pos", holder.adapterPosition)
                holder.itemView.findNavController()
                    .navigate(R.id.action_galleryFragment_to_viewPager2Fragment, this)
            }

        }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val photoItem = getItem(position)
        holder.itemView.shimmerLayoutGallery.apply {
            setShimmerColor(0x75FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        holder.itemView.imageView.layoutParams.height = photoItem.imageHigh
        holder.photoFavorite.text = photoItem.favorites.toString()
        holder.photoLikes.text = photoItem.likes.toString()
        holder.photoUser.text = photoItem.user

        Glide.with(holder.itemView)
            .load(photoItem.previewURL)
            .placeholder(R.drawable.ic_photo_gray_24dp)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { holder.itemView.shimmerLayoutGallery?.stopShimmerAnimation() }
                }
            })
            .into(holder.itemView.imageView)
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

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val photoUser = itemView.findViewById<TextView>(R.id.textViewUser)
    val photoLikes = itemView.findViewById<TextView>(R.id.textViewLikes)
    val photoFavorite = itemView.findViewById<TextView>(R.id.textViewFavorites)
}