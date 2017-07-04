package com.wabadaba.dziennik.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.LocalDateTime

data class AuthInfo(
        @JsonProperty("access_token") var accessToken: String,
        @JsonProperty("refresh_token") var refreshToken: String,
        @JsonProperty("expires_in") var expiresIn: Int,
        var validFrom: LocalDateTime = LocalDateTime.now()) {
    private val buffer: Int = 3600

    fun needsRefresh(): Boolean =
            validFrom.plusSeconds(expiresIn + buffer)
                    .isAfter(LocalDateTime.now())
}