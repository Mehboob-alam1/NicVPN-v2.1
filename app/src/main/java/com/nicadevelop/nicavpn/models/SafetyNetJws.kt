package com.nicadevelop.nicavpn.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.io.Serializable
import java.lang.StringBuilder



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "nonce",
    "timestampMs",
    "apkPackageName",
    "apkDigestSha256",
    "ctsProfileMatch",
    "apkCertificateDigestSha256",
    "basicIntegrity",
    "advice",
    "evaluationType"
)
class SafetyNetJws : Serializable {
    @get:JsonProperty("nonce")
    @set:JsonProperty("nonce")
    @JsonProperty("nonce")
    var nonce: String? = null

    @get:JsonProperty("timestampMs")
    @set:JsonProperty("timestampMs")
    @JsonProperty("timestampMs")
    var timestampMs: Long? = null

    @get:JsonProperty("apkPackageName")
    @set:JsonProperty("apkPackageName")
    @JsonProperty("apkPackageName")
    var apkPackageName: String? = null

    @get:JsonProperty("apkDigestSha256")
    @set:JsonProperty("apkDigestSha256")
    @JsonProperty("apkDigestSha256")
    var apkDigestSha256: String? = null

    @get:JsonProperty("ctsProfileMatch")
    @set:JsonProperty("ctsProfileMatch")
    @JsonProperty("ctsProfileMatch")
    var ctsProfileMatch: Boolean? = null

    @get:JsonProperty("apkCertificateDigestSha256")
    @set:JsonProperty("apkCertificateDigestSha256")
    @JsonProperty("apkCertificateDigestSha256")
    var apkCertificateDigestSha256: List<String>? = null

    @get:JsonProperty("basicIntegrity")
    @set:JsonProperty("basicIntegrity")
    @JsonProperty("basicIntegrity")
    var basicIntegrity: Boolean? = null

    @get:JsonProperty("advice")
    @set:JsonProperty("advice")
    @JsonProperty("advice")
    var advice: String? = null

    @get:JsonProperty("evaluationType")
    @set:JsonProperty("evaluationType")
    @JsonProperty("evaluationType")
    var evaluationType: String? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param evaluationType
     * @param ctsProfileMatch
     * @param apkPackageName
     * @param advice
     * @param apkDigestSha256
     * @param nonce
     * @param timestampMs
     * @param basicIntegrity
     */
    constructor(
        ctsProfileMatch: Boolean?,
        basicIntegrity: Boolean?
    ) : super() {
        this.ctsProfileMatch = ctsProfileMatch
        this.basicIntegrity = basicIntegrity
    }

    /**
     *
     * @param evaluationType
     * @param ctsProfileMatch
     * @param apkPackageName
     * @param advice
     * @param apkDigestSha256
     * @param nonce
     * @param timestampMs
     * @param basicIntegrity
     */
    constructor(
        nonce: String?,
        timestampMs: Long?,
        apkPackageName: String?,
        apkDigestSha256: String?,
        ctsProfileMatch: Boolean?,
        basicIntegrity: Boolean?,
        advice: String?,
        evaluationType: String?
    ) : super() {
        this.nonce = nonce
        this.timestampMs = timestampMs
        this.apkPackageName = apkPackageName
        this.apkDigestSha256 = apkDigestSha256
        this.ctsProfileMatch = ctsProfileMatch
        this.basicIntegrity = basicIntegrity
        this.advice = advice
        this.evaluationType = evaluationType
    }

    override fun toString(): String {
        return StringBuilder().append("{nonce: ", nonce).append(", timestampMs: ", timestampMs)
            .append(", apkPackageName: ", apkPackageName)
            .append(", apkDigestSha256: ", apkDigestSha256)
            .append(", ctsProfileMatch: ", ctsProfileMatch)
            .append(", apkCertificateDigestSha256: ", apkCertificateDigestSha256)
            .append(", basicIntegrity: ", basicIntegrity).append(", advice: ", advice)
            .append(", evaluationType: ", evaluationType).append("}").toString()
    }

    companion object {
        private const val serialVersionUID = -8477890045051140777L
    }
}