package info.dafle.websocketexample

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_photo.view.*

class ImageAdapter(val context: Context, val list: List<String>): RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val l = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return ViewHolder(l)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(private val v : View): RecyclerView.ViewHolder(v) {

        fun bind(s: String) {

            Glide.with(context)
                .load(s)
                .into(v.imageView)
        }
    }
}