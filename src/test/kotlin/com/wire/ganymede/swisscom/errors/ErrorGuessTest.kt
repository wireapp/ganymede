package com.wire.ganymede.swisscom.errors

import mu.KLogging
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.test.assertEquals


class ErrorGuessTest {

    companion object : KLogging() {
        @Suppress("unused") // not true, used as generator
        @JvmStatic
        fun testingData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("ExpiredCertificate.json", SignWithExpiredCertificate::class),
                Arguments.of("MalformedMail.json", SignWithMalformedMail::class),
                Arguments.of("PendingMalformedMail.json", SignWithMalformedMail::class),
                Arguments.of("PendingResponse.json", ResourceStillInPendingState::class),
                Arguments.of("PendingWithExpiredRequestId.json", InvalidRequestIdUsed::class),
                Arguments.of("WrongSerialNumber.json", SignWithWrongSerialNumber::class)
            )
        }
    }

    private fun readJson(name: String): String = this::class.java.getResource(name).readText()

    @ParameterizedTest
    @MethodSource("testingData")
    fun `test data`(json: String, resultClass: KClass<out SwisscomResponse>) {
        logger.info { "Guessing for $json and ${resultClass.qualifiedName}" }
        val guess = guessError(readJson(json))
        logger.info { guess }

        assertEquals(resultClass, guess.response::class)
    }
}
