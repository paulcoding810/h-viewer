package com.paulcoding.hviewer.repository

import com.paulcoding.hviewer.model.HRelease
import java.io.File

interface UpdateAppRepository {
    suspend fun getLatestAppRelease(): Result<HRelease>
    suspend fun downloadApk(downloadUrl: String, destination: File): Result<File>
}