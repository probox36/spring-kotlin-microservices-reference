package com.buoyancy.common.utils

import org.springframework.context.MessageSource
import java.util.Locale

fun MessageSource.get(code: String, vararg args: Any? = emptyArray()): String {
    return this.getMessage(code, args, Locale.ENGLISH)
}