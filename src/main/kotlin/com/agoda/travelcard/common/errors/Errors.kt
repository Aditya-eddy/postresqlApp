package com.agoda.travelcard.common.errors

class BadRequestException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : RuntimeException(message)

class WiseApiException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class DatabaseNotInitializedException(message: String = "Postgres pool failed to initialize") :
    RuntimeException(message)
