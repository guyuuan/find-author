package com.guyuuan.app.find_author.core.data.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

/**
 * @author: guyuuan
 * @createTime: 7/11/24 16:01
 * @description:
 **/

private val monthDateTimeFormater = LocalDateTime.Format {
    monthNumber(Padding.NONE)
    char('月')
    dayOfMonth(Padding.NONE)
    char('日')
}

private val yearDateTimeFormater = LocalDateTime.Format {
    year(Padding.NONE)
    char('年')
    monthNumber(Padding.NONE)
    char('月')
    dayOfMonth(Padding.NONE)
    char('日')
}

val Long.asLocalDateTime: LocalDateTime
    get() = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
val Long.formatedDateString: String
    get() {
        val dateTime = asLocalDateTime
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (now.year != dateTime.year) {
            return dateTime.format(yearDateTimeFormater)
        } else if (now.dayOfYear == dateTime.dayOfYear) {
            return "今天"
        } else if (now.dayOfYear - 1 == dateTime.dayOfYear) {
            return "昨天"
        }

        return dateTime.format(monthDateTimeFormater)
    }

val LocalDateTime.formatedDateString: String
    get() {
        val dateTime = this
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (now.year != dateTime.year) {
            return dateTime.format(yearDateTimeFormater)
        } else if (now.dayOfYear == dateTime.dayOfYear) {
            return "今天"
        } else if (now.dayOfYear - 1 == dateTime.dayOfYear) {
            return "昨天"
        }

        return dateTime.format(monthDateTimeFormater)
    }