package ru.droidcat.login_sample.login

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
import ru.droidcat.login_sample.user_storage.UserNotExists
import ru.droidcat.login_sample.user_storage.UserResponse
import ru.droidcat.login_sample.user_storage.UserStorage

private const val ALLOWED_REGEX = "^[a-zA-Z0-9]+"
private const val MIN_PASSWORD_LENGTH = 8

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state.asStateFlow()

    private val _loginSuccessful = MutableSharedFlow<Boolean>().apply { this.tryEmit(false) }
    val loginSuccessful = _loginSuccessful.asSharedFlow().distinctUntilChanged()

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

    fun onLogin() {
        _state.value = _state.value.copy(
            loginError = null,
            passwordError = null,
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
        _state.value = _state.value.copy(loading = true)
        viewModelScope.launch {
            signIn()
        }
    }

    fun onTogglePasswordVisible() {
        _state.value = _state.value.copy(
            passwordVisible = _state.value.passwordVisible.not()
        )
    }

    private suspend fun signIn() {
        UserStorage.login(
            login = _state.value.login,
            password = _state.value.password
        ).let {
            when (it) {
                is UserResponse -> _loginSuccessful.emit(true)
                is UserNotExists -> {
                    _state.value = _state.value.copy(
                        signError = ServerError.WrongCredentials,
                        loading = false
                    )
                    _loginSuccessful.emit(false)
                }
                is ServerNotAvailable -> {
                    _state.value = _state.value.copy(
                        signError = ServerError.ServerNotAvailable,
                        loading = false
                    )
                    _loginSuccessful.emit(false)
                }
                else -> {
                    _state.value = _state.value.copy(
                        signError = ServerError.UnknownError,
                        loading = false
                    )
                    _loginSuccessful.emit(false)
                }
            }
        }
    }

}

data class LoginScreenState(
    val login: String = "",
    val password: String = "",
    val loginError: LoginError? = null,
    val passwordError: PasswordError? = null,
    val signError: ServerError? = null,
    val loading: Boolean = false,
    val passwordVisible: Boolean = false,
)
