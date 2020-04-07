package com.wire.ganymede.swisscom.model

import com.wire.ganymede.dto.User

/**
 * Set document information.
 */
fun RootSignRequest.hashDocument(hash: String, documentId: String) {
    val inputDocuments = signRequest.inputDocuments
    inputDocuments.documentHash.hash = hash
    inputDocuments.documentHash.documentId = documentId
}

/**
 * This request is for this signer.
 */
fun RootSignRequest.createSignRequestForName(signer: User, name: String) {
    val certificateRequest = signRequest.optionalInputs.certificateRequest

    certificateRequest.distinguishedName =
        "CN=TEST ${signer.firstname} ${signer.lastname}, " +
                "givenname=${signer.firstname}, surname=${signer.lastname}, " +
                "C=${signer.country}, emailAddress=${signer.email}"

    certificateRequest.stepUpAuthorisation.phone.setFor(signer, name)
}

/**
 * Set phone metadata.
 */
fun Phone.setFor(signer: User, name: String) {
    language = signer.locale
    phoneNumber = signer.phone?.replace("+", "")?.replace(" ", "")
    message = "Please confirm the signing of the document: $name"
    serialNumber = signer.id.toString()
}
