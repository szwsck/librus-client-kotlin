package com.wabadaba.dziennik.api

sealed class HttpException : RuntimeException {

    class DeviceOffline(url: String) : HttpException(url, "Device Offline")
    class ServerOffline(url: String) : HttpException(url, "Server Offline")
    class NotActive(url: String) : HttpException(url, "Is not active")
    class Maintenance(url: String) : HttpException(url, "Maintenance")
    class Authorization(url: String) : HttpException(url, "Invalid username or password")
    class Unknown(url: String, code: Int, message: String) : HttpException(url, code, message)

    constructor(url: String, message: String) : super("Request to URL $url failed with message: $message")

    constructor(url: String, code: Int, message: String) : super("Request to URL $url failed with code $code and message: $message")
}
