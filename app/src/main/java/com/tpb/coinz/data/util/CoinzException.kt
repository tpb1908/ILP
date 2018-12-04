package com.tpb.coinz.data.util

sealed class CoinzException : Exception() {
    class NotFoundException : CoinzException()
    class AlreadyExistsException : CoinzException()
    class InvalidArgumentException : CoinzException()
    class CancelledException : CoinzException()
    class AuthenticationException : CoinzException()
    class NetworkException : CoinzException()
    class UnknownException : CoinzException()
}