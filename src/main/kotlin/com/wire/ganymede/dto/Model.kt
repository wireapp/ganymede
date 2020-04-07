package com.wire.ganymede.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.HashMap
import java.util.UUID

///////////////// Sign Request ///////////////////////////
@JsonInclude(JsonInclude.Include.NON_NULL)
class RootSignRequest {
    @JsonProperty("SignRequest")
    var signRequest: SignRequest = SignRequest()

    init {
        signRequest.requestId = UUID.randomUUID()
    }
}

class SignRequest {
    @JsonProperty("@Profile")
    var profile = "http://ais.swisscom.ch/1.1"

    @JsonProperty("@RequestID")
    var requestId: UUID? = null

    @JsonProperty("InputDocuments")
    var inputDocuments = InputDocuments()

    @JsonProperty("OptionalInputs")
    var optionalInputs = OptionalInputs()
}

class InputDocuments {
    @JsonProperty("DocumentHash")
    var documentHash = DocumentHash()
}

class DocumentHash {
    @JsonProperty("@ID")
    var documentId: String? = null

    @JsonProperty("dsig.DigestValue")
    var hash: String? = null

    @JsonProperty("dsig.DigestMethod")
    var digestMethod = DigestMethod()
}

class DigestMethod {
    @JsonProperty("@Algorithm")
    var algorithm = "http://www.w3.org/2001/04/xmlenc#sha256"
}

class AddTimestamp {
    @JsonProperty("@Type")
    var type = "urn:ietf:rfc:3161"
}

class ClaimedIdentity {
    @JsonProperty("Name")
    var name = "ais-90days-trial-OTP:OnDemand-Advanced"
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class OptionalInputs {
    @JsonProperty("sc.CertificateRequest")
    var certificateRequest = CertificateRequest()

    @JsonProperty("sc.SignatureStandard")
    var signatureStandard = "PADES"

    @JsonProperty("sc.AddRevocationInformation")
    var addRevocationInformation = AddRevocationInformation()

    @JsonProperty("SignatureType")
    var SignatureType = "urn:ietf:rfc:3369"

    @JsonProperty("AdditionalProfile")
    var additionalProfile = arrayOf(
        "http://ais.swisscom.ch/1.0/profiles/batchprocessing",
        "urn:oasis:names:tc:dss:1.0:profiles:timestamping",
        "http://ais.swisscom.ch/1.0/profiles/ondemandcertificate",
        "urn:oasis:names:tc:dss:1.0:profiles:asynchronousprocessing",
        "http://ais.swisscom.ch/1.1/profiles/redirect"
    )

    @JsonProperty("AddTimestamp")
    var addTimestamp = AddTimestamp()

    @JsonProperty("ClaimedIdentity")
    var claimedIdentity = ClaimedIdentity()
}

class AddRevocationInformation {
    @JsonProperty("@Type")
    var type = "BOTH"
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class CertificateRequest {
    @JsonProperty("sc.StepUpAuthorisation")
    var stepUpAuthorisation = StepUpAuthorisation()

    @JsonProperty("sc.DistinguishedName")
    var distinguishedName: String? = null
}

class StepUpAuthorisation {
    @JsonProperty("sc.Phone")
    var phone = Phone()
}

class Phone {
    @JsonProperty("sc.Language")
    var language: String? = null

    @JsonProperty("sc.MSISDN")
    var phoneNumber: String? = null

    @JsonProperty("sc.Message")
    var message: String? = null

    @JsonProperty("sc.SerialNumber")
    var serialNumber: String? = null
}
///////////////// Sign Request ///////////////////////////


///////////////// Sign Request ///////////////////////////
///////////////// Sign Response ///////////////////////////
@JsonIgnoreProperties(ignoreUnknown = true)
class RootSignResponse {
    @JsonProperty("SignResponse")
    var signResponse: SignResponse? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class SignResponse {
    @JsonProperty("@Profile")
    var profile = "http://ais.swisscom.ch/1.1"

    @JsonProperty("@RequestID")
    var requestId: UUID? = null

    @JsonProperty("OptionalOutputs")
    var optionalOutputs: OptionalOutputs? = null

    @JsonProperty("SignatureObject")
    var signature: SignatureObject? = null

    @JsonProperty("Result")
    var result: Result? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class OptionalOutputs {
    @JsonProperty("async.ResponseID")
    var responseId: UUID? = null

    @JsonProperty("sc.StepUpAuthorisationInfo")
    var stepUpAuthorisationInfo: StepUpAuthorisationInfo? = null

    @JsonProperty("sc.RevocationInformation")
    var revocationInformation: RevocationInformation? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Result {
    @JsonProperty("ResultMajor")
    var major: String? = null

    @JsonProperty("ResultMinor")
    var minor: String? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class RevocationInformation {
    @JsonProperty("sc.CRLs")
    var CRLs: HashMap<String, String>? = null

    @JsonProperty("sc.OCSPs")
    var OCSPs: HashMap<String, String>? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class StepUpAuthorisationInfo {
    @JsonProperty("sc.Result")
    var result: _Result? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class _Result {
    @JsonProperty("sc.ConsentURL")
    var url: String? = null

    @JsonProperty("sc.SerialNumber")
    var serialNumber: String? = null

    @JsonProperty("SignatureObject")
    var signatureObject: SignatureObject? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class SignatureObject {
    @JsonProperty("Other")
    var other: Other? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Other {
    @JsonProperty("sc.SignatureObjects")
    var signatureObjects: SignatureObjects? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class SignatureObjects {
    @JsonProperty("sc.ExtendedSignatureObject")
    var extendedSignatureObject: ExtendedSignatureObject? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ExtendedSignatureObject {
    @JsonProperty("@WhichDocument")
    var documentId: String? = null

    @JsonProperty("Base64Signature")
    var base64Signature: Base64Signature? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Base64Signature {
    @JsonProperty("@Type")
    var type: String? = null

    @JsonProperty("$")
    var value: String? = null
}
///////////////// Sign Response ///////////////////////////

///////////////// Sign Response ///////////////////////////
///////////////// Pending Request ////////////////////////////
class RootPendingRequest(responseId: UUID?) {
    @JsonProperty("async.PendingRequest")
    var pendingRequest = PendingRequest()

    init {
        pendingRequest.optionalInputs.responseId = responseId
    }
}

class PendingRequest {
    @JsonProperty("@Profile")
    var profile = "http://ais.swisscom.ch/1.0"

    @JsonProperty("OptionalInputs")
    var optionalInputs = PendingOptionalInputs()
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class PendingOptionalInputs {
    @JsonProperty("ClaimedIdentity")
    var claimedIdentity = ClaimedIdentity()

    @JsonProperty("async.ResponseID")
    var responseId: UUID? = null
}
///////////////// Pending Request ////////////////////////////
