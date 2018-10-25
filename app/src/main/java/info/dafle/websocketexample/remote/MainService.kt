package info.dafle.websocketexample.remote

import android.provider.SyncStateContract
import com.google.gson.GsonBuilder
import info.dafle.websocketexample.NetworkUtil
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MainService {

    @Multipart
    @POST("/image")
    fun uploadPicture(@Part filePart: MultipartBody.Part): Observable<ResponseBody>

    object Creator {
        fun newMainService(): MainService {
            val gson = GsonBuilder()
                .setDateFormat(DATE_AND_TIME_PATTERN)
                .create()
            val retrofit = Retrofit.Builder()
                .baseUrl(MainService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(NetworkUtil.getDefaultClient())
                .build()
            return retrofit.create(MainService::class.java)
        }
    }

    companion object {
        val ENDPOINT = "http://172.18.2.21:3000/"
        val DATE_AND_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
}