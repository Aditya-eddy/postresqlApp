package com.agoda.travelcard.wise

/**
 * Named bundle of the three Wise clients wired up in DI. Mirrors the three
 * token / OAuth flows described in the service logs.
 */
data class WiseClients(
    val physical: WiseClient,
    val virtual: WiseClient,
    val subsidy: WiseClient,
)
