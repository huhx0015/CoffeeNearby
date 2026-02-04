package com.huhx0015.coffeenearby.di.modules

import com.huhx0015.coffeenearby.BuildConfig
import com.huhx0015.coffeenearby.network.CoffeeShopApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

private const val HEADER_AUTHORIZATION = "Authorization"
private const val HEADER_BEARER = "Bearer"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

  @Provides
  @Singleton
  fun provideAuthInterceptor(): Interceptor {
    return Interceptor { chain ->
      val request = chain.request().newBuilder()
        .addHeader(HEADER_AUTHORIZATION, "$HEADER_BEARER ${BuildConfig.YELP_API_KEY}")
        .build()
      chain.proceed(request)
    }
  }

  @Provides
  @Singleton
  fun provideOkHttp(
    authInterceptor: Interceptor
  ): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(authInterceptor)
      .build()
  }

  @Provides
  @Singleton
  fun provideMoshi(): Moshi {
    return Moshi.Builder()
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(
    okHttpClient: OkHttpClient,
    moshi: Moshi
  ): Retrofit {
    return Retrofit.Builder()
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .baseUrl(BuildConfig.YELP_BASE_URL)
      .client(okHttpClient)
      .build()
  }

  @Provides
  @Singleton
  fun provideCoffeeShopApi(
    retrofit: Retrofit
  ): CoffeeShopApi {
    return retrofit.create(CoffeeShopApi::class.java)
  }
}