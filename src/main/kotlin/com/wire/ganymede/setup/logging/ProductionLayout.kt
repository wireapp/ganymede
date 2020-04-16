package com.wire.ganymede.setup.logging


import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.LayoutBase
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Layout that is same as on the production servers in Wire.
 */
class ProductionLayout : LayoutBase<ILoggingEvent>() {

    private companion object {
        val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC)
    }

    override fun doLayout(event: ILoggingEvent): String =
        with(StringBuffer(128)) {
            append("|")
            append(String.format("level=%.1s", event.level.levelStr))

            event.mdcPropertyMap[USER_ID]?.let {
                append(",user=${it}")
            }

            append("|")
            append(formatTime(event))
            append("|")
            append(event.loggerName)
            append("|")
            append(event.formattedMessage)
            append(CoreConstants.LINE_SEPARATOR)
            toString()
        }

    private fun formatTime(event: ILoggingEvent): String =
        dateTimeFormatter.format(Instant.ofEpochMilli(event.timeStamp))
}
