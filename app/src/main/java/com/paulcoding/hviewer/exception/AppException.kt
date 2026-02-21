package com.paulcoding.hviewer.exception

sealed class AppException(cause: Throwable? = null) : Exception(cause) {
    class InvalidRepositoryException(val url: String) : AppException()
    class FailedToSaveGzException(cause: Exception) : AppException(cause)
}