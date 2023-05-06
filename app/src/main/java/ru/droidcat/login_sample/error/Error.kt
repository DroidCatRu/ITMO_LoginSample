package ru.droidcat.login_sample.error

sealed class Error(val message: String)

sealed class LoginError(message: String) : Error(message) {
    object LoginEmpty : LoginError("Представься пожалуйста")
    object IncorrectSymbols : LoginError("Я таких знаков не знаю, вводи только латинские буквы и цифры")
}

sealed class PasswordError(message: String) : Error(message) {
    object PasswordEmpty : PasswordError("Без пароля никак")
    object IncorrectSymbols : PasswordError("Какие-то не те символы, вводи только латинские буквы и цифры")
    object LengthTooShort : PasswordError("Пароль должен быть не менее 8 символов")
    object PasswordWeak : PasswordError("Пароль какой-то слабый, введи буквы и цифры")
    object NotRepeatingPassword : PasswordError("Повтори пароль")
}

sealed class ServerError(message: String) : Error(message) {
    object WrongCredentials : ServerError("А мы точно знакомы?")
    object UserAlreadyExists : ServerError("Попробуй войти в аккаунт, я тебя уже знаю")
    object ServerNotAvailable : ServerError("Сервер наелся и спит, попробуй позже")
    object UnknownError : ServerError("Даже не знаю, что сказать")
}