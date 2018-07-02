package com.example.dima.testtask

import android.content.Context
import com.google.android.gms.security.ProviderInstaller
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.*
import javax.security.cert.CertificateException
import javax.security.cert.X509Certificate


public interface GithubApi{

    @GET("search/users")
    fun searchUsers(@Query("q") query: String,
                    @Query("page") page: Int,
                    @Query("per_page") perPage: Int): Call<UsersList>



    companion object Factory{
        public fun create(context: Context): GithubApi {

            ProviderInstaller.installIfNeeded(context)
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl("https://api.github.com/")
                    .build()

            return retrofit.create<GithubApi>(GithubApi::class.java)
        }

    }

}