package com.paulcoding.hviewer.network

import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.ui.model.SiteConfigs
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getSiteConfigs(@Url url: String): SiteConfigs

    @GET
    suspend fun downloadTar(@Url tarUrl: String): ResponseBody
}

object RetrofitInstance {
    private var api: ApiService? = null

    fun getInstance(): ApiService {
        if (api == null) {
            api = Retrofit.Builder()
                .baseUrl(Preferences.getRemote())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
        return api!!
    }
}
