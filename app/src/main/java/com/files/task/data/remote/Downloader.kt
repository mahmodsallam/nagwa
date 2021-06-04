package com.files.task.data.remote

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import okhttp3.*
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class Downloader(
    private val url: String ,
    private val fileType:String ,
    private val id:String ,
    private val fileName:String
) {

    fun download(func: (percent: Int) -> Unit) {
        val okHttpClient = OkHttpClient.Builder()
        var client = okHttpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalResponse: Response = chain.proceed(chain.request())
                return originalResponse.newBuilder()
                    .body(originalResponse.body?.let {
                        ProgressResponseBody(it, object : OnAttachmentDownloadListener {
                            override fun onAttachmentDownloadedSuccess() {
                            }

                            override fun onAttachmentDownloadedError() {

                            }

                            override fun onAttachmentDownloadedFinished() {
                            }

                            override fun onAttachmentDownloadUpdate(percent: Int) {
                                Log.d("TAG", "$percent")
                                if (percent >= 0) {
                                    func(percent)
                                }
                            }
                        })
                    })
                    .build()
            }
        })
            .build()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
            }
            override fun onResponse(call: Call, response: Response) {
                writeResponseBodyToDisk(response.body,  fileName, fileType)
            }
        })

    }


    @Suppress("DEPRECATION")
    private fun writeResponseBodyToDisk(
        body: ResponseBody?,
        fileName: String,
        fileType: String
    ): Boolean {
        return try {
            val file =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + File.separator + fileName + "." + fileType
                )
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096*2)
                val fileSize = body?.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body?.byteStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    var read: Int = inputStream!!.read(fileReader)
                    var percent =
                        (fileSizeDownloaded.toFloat() / body!!.contentLength().toFloat()) * 100
                    if (percent >= 0)
                        if (read == -1) {
                            break
                        }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()

                }

                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }


}