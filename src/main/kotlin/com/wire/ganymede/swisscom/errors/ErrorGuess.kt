package com.wire.ganymede.swisscom.errors

import ai.blindspot.ktoolz.extensions.parseJson
import com.fasterxml.jackson.databind.JsonNode

/**
 * Try to guess what does the received payload mean.
 */
fun guessError(readData: String): GuessedError {
    val root = parseJson<JsonNode>(readData)

    val response: JsonNode? = root?.get("SignResponse") ?: root?.get("Response")
    val requestId: String? = response?.get("@RequestID")?.asText()

    val result: JsonNode? = response?.get("Result")
    val guessedResponse = result?.let { response.result(it) }
        ?: UnknownResponse(
            swisscomMessage = result?.resultMessage() ?: result?.obtainMinorType() ?: "No idea what happened."
        )
    return GuessedError(
        receivedJson = readData,
        requestId = requestId,
        response = guessedResponse
    )
}

private fun JsonNode.obtainMinorType(): String? =
    get("ResultMinor")?.asText()?.trim()?.takeLastWhile { it != '/' }

private fun JsonNode?.resultMessage(): String? = this
    ?.get("ResultMessage")?.get("\$")
    ?.asText()?.trim()

private fun JsonNode.result(result: JsonNode): SwisscomResponse? =
    result.get("ResultMajor")?.let { resultMajor(it) }

private fun JsonNode.resultMajor(major: JsonNode): SwisscomResponse? =
    when (major.asText()) {
        "urn:oasis:names:tc:dss:1.0:profiles:asynchronousprocessing:resultmajor:Pending" -> pendingResult()
        "urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError" -> requesterError()
        "urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError" -> responderError()
        "http://ais.swisscom.ch/1.0/resultmajor/SubsystemError" -> subsystemError()
        else -> null
    }

private fun JsonNode.subsystemError(): SwisscomResponse? = resultMessage()?.let { message ->
    when {
        message.startsWith("SerialNumber mismatch", true) ->
            SignWithWrongSerialNumber(message)
        else -> null
    }
}

private fun JsonNode.responderError(): SwisscomResponse? = resultMessage()?.let { message ->
    when {
        message.startsWith("Either ClaimedIdentity", true) ->
            SignWithExpiredCertificate(message)
        else -> null
    }
}

private fun JsonNode.requesterError(): SwisscomResponse? =
    resultMessage()?.let { message ->
        when {
            message.startsWith("Distinguished name could not be parsed", true) ->
                SignWithMalformedMail(message)
            message.startsWith("Unknown ResponseID", true) ->
                ExpiredRequestIdUsed(message)
            else -> null
        }
    }

private fun JsonNode.pendingResult(): SwisscomResponse? =
    get("OptionalOutputs")?.let { optional ->
        val consentUrl = optional.get("sc.StepUpAuthorisationInfo")?.get("sc.Result")?.get("sc.ConsentURL")
        val responseId: JsonNode? = optional.get("async.ResponseID")
        when {
            consentUrl != null -> ResourceStillInPendingState(responseId?.asText())
            responseId != null -> SignWithMalformedMail(responseId.asText())
            else -> null
        }
    }
