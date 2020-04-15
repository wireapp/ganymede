package com.wire.ganymede.setup.logging


import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.LayoutBase

/**
 * Layout that is same as on the production servers in Wire.
 */
class ProductionLayout : LayoutBase<ILoggingEvent>() {

    override fun doLayout(event: ILoggingEvent): String =
        with(StringBuffer(128)) {
            append("|")
            append(String.format("level=%.1s", event.level.levelStr))

            event.mdcPropertyMap[USER_ID]?.let {
                append(",user=${it}")
            }

            append("|")
            append(event.loggerName)
            append(" - ")
            append(event.formattedMessage)
            append(CoreConstants.LINE_SEPARATOR)
            toString()
        }
}
