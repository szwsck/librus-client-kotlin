package com.wabadaba.dziennik.api

class ParseException : RuntimeException {

    constructor(input: String, cause: Throwable) : super("input: " + input, cause)

}
