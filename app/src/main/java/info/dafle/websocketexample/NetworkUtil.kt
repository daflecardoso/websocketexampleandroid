package info.dafle.websocketexample

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class NetworkUtil {

    companion object {

        fun getDefaultClient(): OkHttpClient {

            return OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        }
    }

}