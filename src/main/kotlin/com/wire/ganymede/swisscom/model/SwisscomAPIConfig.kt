package com.wire.ganymede.swisscom.model

data class SwisscomAPIConfig(
    val baseUrl: String = "https://ais.swisscom.com/AIS-Server/rs/v1.0",
    val signPath: String = "/sign",
    val pendingPath: String = "/pending"
)
