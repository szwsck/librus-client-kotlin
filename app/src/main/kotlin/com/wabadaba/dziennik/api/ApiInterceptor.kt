package com.wabadaba.dziennik.api

import okhttp3.Interceptor
import okhttp3.Response


class ApiInterceptor(val authInfo: AuthInfo) : Interceptor {
    //TODO: handle refresh token header
    override fun intercept(chain: Interceptor.Chain?): Response {
        val request = chain!!.request()
        val builder = request.newBuilder()
        builder.addHeader("Authorization", "Bearer " + authInfo.accessToken)
        val newRequest = builder.build()
        val response = chain.proceed(newRequest)
        return response
    }
}