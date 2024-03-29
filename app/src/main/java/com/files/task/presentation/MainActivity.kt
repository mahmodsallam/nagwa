package com.files.task.presentation

import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.files.task.App
import com.files.task.BuildConfig
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
        for (item in files) {
            var fileType = if (item.type == "PDF")
                "pdf"
            else
                "mp4"
            item.isExists = isFileExists(item.name, fileType)
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

        mainViewModel.progess.observe(this, androidx.lifecycle.Observer {
            val args = Bundle()
            args.putInt("progress", (it as FileProgress).progress)
            adapter.notifyItemChanged(it.id.toInt(), args)

        })

        if (file?.isExists!!) {
            openFile(getFileUri(file?.name, fileType))
        } else {
            mainViewModel.downloadFile(file?.url!!, file.name, fileType, position.toString())
        }


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

    private fun isFileExists(fileName: String, fileType: String): Boolean {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator + fileName + "." + fileType
        val fileDownloaded = File(path)
        return fileDownloaded.isFile
    }

    private fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data=pickerInitialUri
        intent.flags = FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
        intent.type="*/*"
        val chooser = Intent.createChooser(intent , "open with")
        startActivity(chooser)
    }

    private fun getFileUri(fileName: String, fileType: String): Uri {
        val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+ File.separator + fileName + "." + fileType)
        return getUriForFile(
            Objects.requireNonNull(applicationContext),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
    }

}