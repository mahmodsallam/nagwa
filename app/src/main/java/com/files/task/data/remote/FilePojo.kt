package com.files.task.data.remote

data class FilePojo(
    val id: Int, // 1
    val name: String, // Video 1
    val type: String, // VIDEO
    val url: String ,// https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4
    var progress:Int ,
    var isExists:Boolean


)