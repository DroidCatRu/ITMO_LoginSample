package ru.droidcat.login_sample.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.droidcat.login_sample.error.LoginError
import ru.droidcat.login_sample.error.PasswordError
import ru.droidcat.login_sample.error.ServerError
import ru.droidcat.login_sample.user_storage.ServerNotAvailable
import ru.droidcat.login_sample.user_storage.UserAlreadyRegistered
import ru.droidcat.login_sample.user_storage.UserResponse
import ru.droidcat.login_sample.user_storage.UserStorage

private const val ALLOWED_REGEX = "^[a-zA-Z0-9]+"
private const val MIN_PASSWORD_LENGTH = 8
private const val ONLY_DIGITS = "[0-9]+"
private const val ONLY_CHARS = "[a-zA-Z]+"

class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterScreenState())
    val state = _state.asStateFlow()

    private val _registerSuccessful = MutableSharedFlow<Boolean>().apply { this.tryEmit(false) }
    val registerSuccessful = _registerSuccessful.asSharedFlow().distinctUntilChanged()

    fun onLoginChange(newValue: String) {
        if (newValue.contains('\n') || newValue.contains('\r') || newValue.contains('\t')) {
            return
        }
        _state.value = _state.value.copy(login = newValue)
        if (newValue.isNotEmpty() && !newValue.matches(Regex(ALLOWED_REGEX))) {
            _state.value = _state.value.copy(loginError = LoginError.IncorrectSymbols)
            return
        }
        _state.value = _state.value.copy(loginError = null)
    }

    fun onPasswordChange(newValue: String) {
        if (newValue.contains('\n') || newValue.contains('\r') || newValue.contains('\t')) {
            return
        }
        _state.value = _state.value.copy(password = newValue)
        if (newValue.isNotEmpty() && !newValue.matches(Regex(ALLOWED_REGEX))) {
            _state.value = _state.value.copy(passwordError = PasswordError.IncorrectSymbols)
            return
        }
        _state.value = _state.value.copy(passwordError = null)
    }

    fun onRepeatPasswordChange(newValue: String) {
        if (newValue.contains('\n') || newValue.contains('\r') || newValue.contains('\t')) {
            return
        }
        _state.value = _state.value.copy(repeatPassword = newValue)
        if (newValue.isNotEmpty() && !newValue.matches(Regex(ALLOWED_REGEX))) {
            _state.value = _state.value.copy(repeatPasswordError = PasswordError.IncorrectSymbols)
            return
        }
        _state.value = _state.value.copy(repeatPasswordError = null)
    }

    fun onRegister() {
        _state.value = _state.value.copy(
            loginError = null,
            passwordError = null,
            repeatPasswordError = null,
            signError = null
        )
        if (_state.value.login.isBlank()) {
            _state.value = _state.value.copy(loginError = LoginError.LoginEmpty)
            return
        }
        if (!_state.value.login.matches(Regex(ALLOWED_REGEX))) {
            _state.value = _state.value.copy(loginError = LoginError.IncorrectSymbols)
            return
        }
        if (_state.value.password.isBlank()) {
            _state.value = _state.value.copy(passwordError = PasswordError.PasswordEmpty)
            return
        }
        if (!_state.value.password.matches(Regex(ALLOWED_REGEX))) {
            _state.value = _state.value.copy(passwordError = PasswordError.IncorrectSymbols)
            return
        }
        if (_state.value.password.trim().length < MIN_PASSWORD_LENGTH) {
            _state.value = _state.value.copy(passwordError = PasswordError.LengthTooShort)
            return
        }
        if (_state.value.password.trim().matches(Regex(ONLY_DIGITS)) ||
            _state.value.password.trim().matches(Regex(ONLY_CHARS))
        ) {
            _state.value = _state.value.copy(passwordError = PasswordError.PasswordWeak)
            return
        }
        if (_state.value.password != _state.value.repeatPassword) {
            _state.value = _state.value.copy(repeatPasswordError = PasswordError.NotRepeatingPassword)
            return
        }
        _state.value = _state.value.copy(loading = true)
        viewModelScope.launch {
            register()
        }
    }

    fun onTogglePasswordVisible() {
        _state.value = _state.value.copy(
            passwordVisible = _state.value.passwordVisible.not(),
            repeatPasswordVisible = false
        )
    }

    fun onToggleRepeatPasswordVisible() {
        _state.value = _state.value.copy(
            passwordVisible = false,
            repeatPasswordVisible = _state.value.repeatPasswordVisible.not()
        )
    }

    private suspend fun register() {
        UserStorage.registerUser(
            login = _state.value.login,
            password = _state.value.password
        ).let {
            when (it) {
                is UserResponse -> _registerSuccessful.emit(true)
                is UserAlreadyRegistered -> {
                    _state.value = _state.value.copy(
                        signError = ServerError.UserAlreadyExists,
                        loading = false
                    )
                    _registerSuccessful.emit(false)
                }
                is ServerNotAvailable -> {
                    _state.value = _state.value.copy(
                        signError = ServerError.ServerNotAvailable,
                        loading = false
                    )
                    _registerSuccessful.emit(false)
                }
                else -> {
                    _state.value = _state.value.copy(
                        signError = ServerError.UnknownError,
                        loading = false
                    )
                    _registerSuccessful.emit(false)
                }
            }
        }
    }

}

data class RegisterScreenState(
    val login: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val loginError: LoginError? = null,
    val passwordError: PasswordError? = null,
    val repeatPasswordError: PasswordError? = null,
    val signError: ServerError? = null,
    val loading: Boolean = false,
    val passwordVisible: Boolean = false,
    val repeatPasswordVisible: Boolean = false,
)
