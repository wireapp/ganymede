package com.wire.bots.polls.dto.roman

/**
 * Respond received from the proxy to every message from the bot.
 */
data class Response(
    /**
     * ID of the message bot sent.
     */
    val messageId: String
)
