package com.wabadaba.dziennik.api

class ParseException(input: String, cause: Throwable) : RuntimeException("input: " + input, cause)
