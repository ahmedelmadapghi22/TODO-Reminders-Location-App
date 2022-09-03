package com.udacity.project4.ui.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("title")
fun bindTitle(textView: TextView, title: String) {
    textView.text = title
}

@BindingAdapter("desc")
fun bindDesc(textView: TextView, desc: String?) {
    if (desc == null) {
        textView.visibility = View.GONE
    } else {
        textView.text = desc
    }


}

@BindingAdapter("place")
fun bindPlace(textView: TextView, place: String) {
    textView.text = place

}

@BindingAdapter("VisibleView")
fun setVisible(view: View, visible: Boolean = true) {
    view.visibility = if (visible) View.VISIBLE else View.GONE


}

