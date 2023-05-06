package ru.droidcat.login_sample.user_storage

sealed class Response

sealed class ResponseSuccess : Response()
data class UserResponse(val user: User) : ResponseSuccess()
object OperationSucceed : ResponseSuccess()

sealed class ResponseError : Response()
object ServerNotAvailable : ResponseError()
object UserNotExists : ResponseError()
object UserAlreadyRegistered : ResponseError()
object NoLoggedInUser : ResponseError()
