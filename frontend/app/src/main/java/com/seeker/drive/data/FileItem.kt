package com.seeker.drive.data

data class FileItem(
    val name: String,
    val size: Long,
    val status: Int,
    val hash: String,
    val userId: Int,
    val fid: Int,
    val upload_time: Long
)