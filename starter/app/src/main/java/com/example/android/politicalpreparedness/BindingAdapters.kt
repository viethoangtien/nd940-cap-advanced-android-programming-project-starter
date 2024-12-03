package com.example.android.politicalpreparedness

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        //TODO: Add Glide call to load image and circle crop - user ic_profile as a placeholder and for errors.
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}

@BindingAdapter("electionsDate")
fun setElectionsDate(textView: TextView, date: Date?) {
    date?.let {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss 'EDT' yyyy", Locale.getDefault())
        textView.text = dateFormat.format(date)
    }
}

@BindingAdapter("followElectionStatus")
fun setFollowElectionText(textView: TextView, isFollow: Boolean?) {
    val context = textView.context
    isFollow?.let {
        textView.text =
            context.getString(if (isFollow) R.string.unfollow_election else R.string.follow_election)
    }
}

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    val context = view.context
    url?.let {
        Glide.with(context).load(url).placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile).into(view)
    }
}

