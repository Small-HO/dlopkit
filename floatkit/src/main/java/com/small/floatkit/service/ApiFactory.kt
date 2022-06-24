package com.small.floatkit.service

import com.google.gson.Gson
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by small-ho on 2022/06 17:20
 * title: Api工厂
 */
class ApiFactory private constructor() {

    companion object {
        val instance: ApiFactory by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ApiFactory()
        }
    }

    fun <T> getService(url: String, service: Class<T>, block: (OkHttpClient.Builder) -> Unit): T {
        val client = RetrofitUrlManager.getInstance()
            .with(OkHttpClient.Builder())
            .apply { block(this) }
            .build()
        val gson = Gson().newBuilder()
            .setLenient()
            .serializeNulls()
            .create()
        val retrofit = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit.create(service)
    }

}