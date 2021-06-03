package com.files.task.presentation

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.files.task.App
import com.files.task.R
import com.files.task.data.remote.FilePojo
import com.files.task.data.remote.FileProgress
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), FilesAdapter.FileClickListener {
    @Inject
    lateinit var mainViewModel: MainViewModel
    private var adapter = FilesAdapter()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestStoragePermission()
        parsJsonAndDisplayList()

    }

    private fun readTextFile(inputStream: InputStream): String? {
        val outputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var len: Int
        try {
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
        }
        return outputStream.toString()
    }

    private fun parsJsonAndDisplayList() {
        val input = resources.openRawResource(R.raw.fake_response)
        val jsonString = readTextFile(input)
        val gson = Gson()
        val files =
            gson.fromJson<Array<FilePojo>>(jsonString, Array<FilePojo>::class.java).toMutableList()
        for (item in files){
            item.isExists=isFileExists(item?.name , item?.type.toLowerCase())
        }
        adapter.setfilesList(files)
        adapter.setContext(this)
        adapter.setPhotoClickListener(this)
        adapter.setClickListener(this)
        rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(this)
    }

    override fun onFileClickListener(position: Int) {
        var file = adapter.getFileList()?.get(position)

        var fileType = ""
        fileType = if (file?.type.equals("PDF"))
            "pdf"
        else
            "mp4"

        mainViewModel.downloadFile(file?.url!!, file.name, fileType,position.toString())
        mainViewModel.progess.observe(this, androidx.lifecycle.Observer {
            val args = Bundle()
            args.putInt("progress", (it as FileProgress).progress)
            adapter.notifyItemChanged(it.id.toInt(), args)

        })


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestStoragePermission(): Boolean {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            )
        }
        return false
    }

    private fun isFileExists(fileName:String , fileType:String):Boolean{
        val path= Environment.getExternalStorageDirectory()
            .toString() + File.separator + fileName + "." + fileType
        val fileDownloaded  = File(path)
        return fileDownloaded.isFile
    }




}