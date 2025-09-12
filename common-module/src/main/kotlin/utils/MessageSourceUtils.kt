package com.buoyancy.common.utils

import org.springframework.context.MessageSource
import java.util.Locale

/*
    Changed this from "get" to "find" because of Unresolved reference exception.
    The problem is purely my ide + gradle combination, otherwise everything works fine

    Exception in question:

    Unresolved reference. None of the following candidates is applicable because of receiver type mismatch:
    public operator fun MatchGroupCollection.get(name: String): MatchGroup? defined in kotlin.text
*/

fun MessageSource.find(code: String, vararg args: Any): String {
    return this.getMessage(code, if (args.isEmpty()) null else args, Locale.ENGLISH)
}