package com.agoda.travelcard.account

import com.agoda.travelcard.common.database.TransactionRecord
import com.agoda.travelcard.common.database.TransactionRepository
import com.agoda.travelcard.common.errors.BadRequestException

class AccountTransactionService(
    private val repository: TransactionRepository,
) {
    fun list(
        accountId: String,
        whitelabel: String,
        limit: Int,
        offset: Long,
    ): List<TransactionRecord> {
        if (accountId.isBlank()) throw BadRequestException("accountId is required")
        if (limit <= 0 || limit > 200) throw BadRequestException("limit must be in 1..200")
        if (offset < 0) throw BadRequestException("offset must be >= 0")
        return repository.listByAccount(accountId, whitelabel, limit, offset)
    }
}
