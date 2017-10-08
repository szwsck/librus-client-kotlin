package com.wabadaba.dziennik.api

class APIClientFactory {
    fun create(authInfo: AuthInfo, httpClient: RxHttpClient): APIClient
            = APIClient(authInfo, httpClient)
}