package com.wire.ganymede.utils

/**
 * Creates URL from [this] as base and [path] as path
 */
infix fun String.appendPath(path: String) = "${dropLastWhile { it == '/' }}/${path.dropWhile { it == '/' }}"
