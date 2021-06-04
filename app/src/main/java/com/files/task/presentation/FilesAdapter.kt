package com.files.task.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.files.task.R
import com.files.task.data.remote.FilePojo
import com.files.task.databinding.ItemFileBinding
import kotlinx.android.synthetic.main.item_file.view.*

class FilesAdapter() :
    RecyclerView.Adapter<FilesAdapter.FilesViewHolder>() {
    private var filesList: MutableList<FilePojo>? = null
    private lateinit var binding: ItemFileBinding
    private var context: Context? = null
    private var photoClickListener: FileClickListener? = null
    private var fileClickListener:FileClickListener? = null
    fun setPhotoClickListener(photoClickListener: FileClickListener) {
        this.photoClickListener = photoClickListener
    }

    fun setfilesList(filesList: MutableList<FilePojo>) {
        this.filesList = filesList
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun setClickListener(fileClickListener: FileClickListener){
        this.fileClickListener=fileClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.itemView.setOnClickListener { fileClickListener }
        return FilesViewHolder(binding , fileClickListener!!)
    }

    override fun getItemCount(): Int {
        return filesList?.size ?: 0
    }

    override fun onBindViewHolder(
        holder: FilesViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()){
            var args  = payloads[0] as Bundle
            var progress = args.getInt("progress")
            if (progress==100)
                filesList?.get(position)?.isExists=true
            holder.partialBinding(progress)

        }
        else {
            super.onBindViewHolder(holder, position, payloads)

        }

    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.bindData(filesList?.get(position)!!)
    }

    fun getFileList(): MutableList<FilePojo>? {
        return filesList
    }
    class FilesViewHolder(itemView: ItemFileBinding  , fileClickListener: FileClickListener) : RecyclerView.ViewHolder(itemView.root) ,
        View.OnClickListener {
        private var fileClickListener:FileClickListener?=null
        private var filePojo:FilePojo?=null
        init {
            this.fileClickListener=fileClickListener
            itemView.root.setOnClickListener(this)
        }
        fun bindData(file: FilePojo) {
            this.filePojo=filePojo
            itemView.tvFileName.text = file.name
            itemView.pbDownload.progress=file.progress
            if (file.type == "PDF")
                itemView.ivFile.setImageResource(R.drawable.pdf)
            else
                itemView.ivFile.setImageResource(R.drawable.video)

            if (file.isExists)
                itemView.iv_file_exists.visibility=View.VISIBLE
            else {
                itemView.iv_file_exists.visibility=View.GONE

            }
        }

        fun partialBinding(progress:Int?){
            itemView.pbDownload.progress=progress?:0
            itemView.pbDownload.visibility=View.VISIBLE
            if (progress==100){
                itemView.pbDownload.visibility=View.INVISIBLE
                itemView.iv_file_exists.visibility=View.VISIBLE
            }

        }

        override fun onClick(p0: View?) {
           fileClickListener?.onFileClickListener(adapterPosition)
        }
    }

    interface FileClickListener {
        fun onFileClickListener(position:Int)
    }
}