package com.buoyancy.common.utils

import org.springframework.context.MessageSource
import java.util.Locale

fun MessageSource.get(code: String, arg: Array<Any>? = null, locale: Locale = Locale.ENGLISH): String {
    return this.getMessage(code, arg, locale)
}