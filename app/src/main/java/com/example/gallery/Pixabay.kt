package com.example.gallery

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.FlowPreview

data class Pixabay(
    val totalHits: String,
    val hits: Array<PhotoItem>,
    val total: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pixabay

        if (totalHits != other.totalHits) return false
        if (!hits.contentEquals(other.hits)) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = totalHits.hashCode()
        result = 31 * result + hits.contentHashCode()
        result = 31 * result + total
        return result
    }
}

@Parcelize
data class PhotoItem(
    @SerializedName("previewURL") val previewURL: String,
    @SerializedName("id") val photoId: Int,
    @SerializedName("largeImageURL") val fullURL: String,
    @SerializedName("user") val user: String,
    @SerializedName("webformatWidth") val imageWidth: Int,
    @SerializedName("webformatHeight") val imageHigh: Int,
    @SerializedName("likes") val likes: Int,
    @SerializedName("favorites") val favorites: Int
) : Parcelable