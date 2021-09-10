package com.example.assignmenttask.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmenttask.R
import com.example.assignmenttask.data.Movie

class AttachmentAdapter(val list: List<Movie>, val click: (Movie, Int) -> Unit) :
  RecyclerView.Adapter<AttachmentAdapter.SingleRowAttachment>() {

  inner class SingleRowAttachment(biniding: View) : RecyclerView.ViewHolder(biniding) {
    val progress: ProgressBar = biniding.findViewById(R.id.progressBar)
    val nameFile: TextView = biniding.findViewById(R.id.nameFile)
    val downloadPrecetntage: TextView = biniding.findViewById(R.id.txtProgressPercent)
    val contentDownload: LinearLayout = biniding.findViewById(R.id.contentProgress)
    val imageViewDownload: ImageView = biniding.findViewById(R.id.download)
    fun bid(position: Int) {
      val item = list[position]
      imageViewDownload.setOnClickListener {
        if (!item.isCompleted)
          click(item, position)
      }
      if (item.startDownload) {
        if (item.totalFileSize!=-1L){
          val progressValue =
            ((item.currentDownload.toDouble() / item.totalFileSize.toDouble()) * 100).toInt()
          progress.setProgress(progressValue)
        }
        downloadPrecetntage.setText("${String.format("%.2f",(item.currentDownload.toDouble()/(1024*1024))) }MB /${ if(item.totalFileSize==-1L) 0 else String.format("%.2f",(item.totalFileSize.toDouble()/(1024*1024))) }MB")
        contentDownload.visibility = View.VISIBLE
        imageViewDownload.visibility = View.INVISIBLE
      } else if (item.isCompleted) {
        contentDownload.visibility = View.GONE
        imageViewDownload.visibility = View.VISIBLE
        imageViewDownload.setImageResource(R.drawable.ic_done_download)
      } else {
        contentDownload.visibility = View.GONE
      }
      nameFile.text = item.name
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleRowAttachment {
    return SingleRowAttachment(
      LayoutInflater.from(parent.context).inflate(R.layout.item_files_attacjment, parent, false)
    )
  }

  override fun onBindViewHolder(holder: SingleRowAttachment, position: Int) {
    holder.bid(position)
  }

  override fun getItemCount(): Int {
    return list.size
  }
}