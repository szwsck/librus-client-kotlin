package com.wabadaba.dziennik.api

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthTokens(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("refresh_token")
        val refreshToken: String
)
