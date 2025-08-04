package com.buoyancy.common.utils

import org.springframework.context.MessageSource
import java.util.Locale

fun MessageSource.get(code: String, vararg args: Any): String {
    return this.getMessage(code, if (args.isEmpty()) null else args, Locale.ENGLISH)
}