package com.files.task.presentation

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.files.task.data.remote.Downloader
import com.files.task.data.remote.FilePojo
import com.files.task.data.remote.FileProgress
import com.files.task.data.repository.FilesRepository
import okhttp3.ResponseBody
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


class MainViewModel @Inject constructor(private val filesRepository: FilesRepository) :
    ViewModel() {
    val progess = MutableLiveData<FileProgress>()

    fun downloadFile(url: String, fileName: String, fileType: String , fileID:String) {
        Downloader(url, fileType, fileID, fileName).download {
            publishProgress( it , fileID)
        }
    }


    private fun publishProgress(progress: Int , fileID:String) {
        progess.postValue(FileProgress(fileID,progress))

    }

}