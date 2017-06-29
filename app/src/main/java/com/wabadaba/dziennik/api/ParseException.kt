package com.wabadaba.dziennik.api

class ParseException : RuntimeException {

    constructor(input: String, cause: Throwable) : super("input: " + input, cause) {}

    constructor(input: String, message: String) : super(String.format("input: %s, message: %s", input, message)) {}
}
