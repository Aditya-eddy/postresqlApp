package com.agoda.travelcard.wise

/**
 * Configuration for a single Wise API client. The service instantiates three
 * logical clients: physical-card issuance, virtual-card issuance, and subsidy.
 */
data class WiseClientConfig(
    val baseUrl: String,
    val clientId: String,
    val clientSecret: String,
    val program: String?, // null for the subsidy flow
    val label: String,
)
