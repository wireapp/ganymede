package com.wire.ganymede.swisscom.errors

import com.fasterxml.jackson.databind.JsonNode
import pw.forst.tools.katlib.parseJson

/**
 * Try to guess what does the received payload mean.
 */
fun guessError(readData: String): GuessedError {
    val root = parseJson<JsonNode>(readData)

    val response: ResponseNode? = root?.get("SignResponse") ?: root?.get("Response")
    val requestId: String? = response?.get("@RequestID")?.asText()

    val result: ResultNode? = response?.get("Result")
    val guessedResponse = result?.result(response.get("OptionalOutputs"))
        ?: UnknownResponse(
            swisscomMessage = result?.resultMessage() ?: result?.obtainMinorType() ?: "No idea what happened."
        )
    return GuessedError(
        receivedJson = readData,
        requestId = requestId,
        response = guessedResponse
    )
}

private fun ResultNode.obtainMinorType(): String? =
    get("ResultMinor")?.asText()?.trim()?.takeLastWhile { it != '/' }

private fun ResultNode?.resultMessage(): String? = this
    ?.get("ResultMessage")?.get("\$")
    ?.asText()?.trim()

private fun ResultNode.result(optionalOutputs: OptionalOutputsNode?): SwisscomResponse? =
    get("ResultMajor")?.let { resultMajor(it, optionalOutputs) }

private fun ResultNode.resultMajor(major: JsonNode, optionalOutputs: OptionalOutputsNode?): SwisscomResponse? =
    when (major.asText()) {
        "urn:oasis:names:tc:dss:1.0:profiles:asynchronousprocessing:resultmajor:Pending" -> optionalOutputs.pendingResult()
        "urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError" -> requesterError()
        "urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError" -> responderError()
        "http://ais.swisscom.ch/1.0/resultmajor/SubsystemError" -> subsystemError()
        else -> null
    }

private fun ResultNode.subsystemError(): SwisscomResponse? = resultMessage()?.let { message ->
    when {
        message.startsWith("SerialNumber mismatch", true) ->
            SignWithWrongSerialNumber(message)
        else -> null
    }
}

private fun ResultNode.responderError(): SwisscomResponse? = resultMessage()?.let { message ->
    when {
        message.startsWith("Either ClaimedIdentity", true) ->
            SignWithExpiredCertificate(message)
        else -> null
    }
}

private fun ResultNode.requesterError(): SwisscomResponse? =
    resultMessage()?.let { message ->
        when {
            message.startsWith("Distinguished name could not be parsed", true) ->
                SignWithMalformedMail(message)
            message.startsWith("Unknown ResponseID", true) ->
                InvalidRequestIdUsed(message)
            else -> null
        }
    }

private fun OptionalOutputsNode?.pendingResult(): SwisscomResponse? =
    this?.let { optional ->
        val consentUrl = optional.get("sc.StepUpAuthorisationInfo")?.get("sc.Result")?.get("sc.ConsentURL")
        val responseId: JsonNode? = optional.get("async.ResponseID")
        when {
            consentUrl != null -> ResourceStillInPendingState(responseId?.asText())
            responseId != null -> SignWithMalformedMail(responseId.asText())
            else -> null
        }
    }

// type aliases to distinguish particular message nodes
private typealias ResultNode = JsonNode
private typealias ResponseNode = JsonNode
private typealias OptionalOutputsNode = JsonNode
