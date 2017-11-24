package protokot.example.com.protokot.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Abstract class handling service creation
 */
abstract class AbstractService<T>(baseUrl: String, service: Class<T>) {

    var serviceInstance: T

    init {
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        clientBuilder.readTimeout(20, TimeUnit.SECONDS)
        clientBuilder.connectTimeout(20, TimeUnit.SECONDS)
        clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(baseUrl)
                .client(clientBuilder.build())

        serviceInstance = retrofitBuilder.build().create(service)
    }
}