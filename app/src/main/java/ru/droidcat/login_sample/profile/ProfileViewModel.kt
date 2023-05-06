package ru.droidcat.login_sample.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.droidcat.login_sample.error.ServerError
import ru.droidcat.login_sample.user_storage.NoLoggedInUser
import ru.droidcat.login_sample.user_storage.OperationSucceed
import ru.droidcat.login_sample.user_storage.ServerNotAvailable
import ru.droidcat.login_sample.user_storage.UserResponse
import ru.droidcat.login_sample.user_storage.UserStorage

class ProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    private val _logOutSuccessful = MutableSharedFlow<Boolean>().apply { this.tryEmit(false) }
    val logOutSuccessful = _logOutSuccessful.asSharedFlow().distinctUntilChanged()

    fun getUserInfo() {
        _state.value = _state.value.copy(loading = true)
        viewModelScope.launch {
            UserStorage.getCurrentUser().let {
                when (it) {
                    is UserResponse -> {
                        _state.value = _state.value.copy(
                            username = it.user.userName,
                            getUserError = null,
                            loading = false,
                        )
                    }
                    is NoLoggedInUser -> _logOutSuccessful.emit(true)
                    is ServerNotAvailable -> {
                        _state.value = _state.value.copy(
                            loading = false,
                            getUserError = ServerError.ServerNotAvailable
                        )
                    }
                    else -> {
                        _state.value = _state.value.copy(
                            loading = false,
                            getUserError = ServerError.UnknownError
                        )
                    }
                }
            }
        }
    }

    fun onLogOut() {
        _state.value = _state.value.copy(loggingOut = true)
        viewModelScope.launch {
            UserStorage.logOut().let {
                when (it) {
                    is OperationSucceed -> _logOutSuccessful.emit(true)
                    is ServerNotAvailable -> {
                        _state.value = _state.value.copy(
                            logOutError = ServerError.ServerNotAvailable,
                            loggingOut = false
                        )
                        _logOutSuccessful.emit(false)
                    }
                    else -> {
                        _state.value = _state.value.copy(
                            logOutError = ServerError.UnknownError,
                            loggingOut = false
                        )
                        _logOutSuccessful.emit(false)
                    }
                }
            }
        }
    }

    fun cancelLogOut() {
        _state.value = _state.value.copy(
            logOutError = null
        )
    }
}

data class ProfileScreenState(
    val username: String = "",
    val loading: Boolean = false,
    val loggingOut: Boolean = false,
    val getUserError: ServerError? = null,
    val logOutError: ServerError? = null
)