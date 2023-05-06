package ru.droidcat.login_sample.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import ru.droidcat.login_sample.ui.theme.LoginSampleTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    onRegister: (() -> Unit)? = null
) {

    val vm: RegisterViewModel = viewModel(RegisterViewModel::class.java)
    val state by vm.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        vm.registerSuccessful.collectLatest { registered ->
            registered.takeIf { it }?.let {
                onRegister?.invoke()
            }
        }
    }

    AnimatedVisibility(
        visible = state.loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Регистрируем вас...")
            }
        }
    }

    AnimatedVisibility(
        visible = !state.loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column {

                    Text(
                        style = MaterialTheme.typography.headlineMedium,
                        text = "Регистрация нового аккаунта"
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onKeyEvent {
                                when (it.key) {
                                    Key.Tab,
                                    Key.Enter -> {
                                        focusManager.moveFocus(FocusDirection.Down)
                                        true
                                    }
                                    else -> false
                                }
                            },
                        label = { Text("Логин") },
                        isError = state.loginError != null,
                        supportingText = {
                            state.loginError?.let {
                                Text(it.message)
                            }
                        },
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        value = state.login,
                        onValueChange = { vm.onLoginChange(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onKeyEvent {
                                when (it.key) {
                                    Key.Tab,
                                    Key.Enter -> {
                                        focusManager.moveFocus(FocusDirection.Down)
                                        true
                                    }
                                    else -> false
                                }
                            },
                        label = { Text("Пароль") },
                        isError = state.passwordError != null,
                        supportingText = {
                            state.passwordError?.let {
                                Text(it.message)
                            }
                        },
                        visualTransformation = when (state.passwordVisible) {
                            true -> VisualTransformation.None
                            else -> PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val image = when (state.passwordVisible) {
                                true -> Icons.Filled.Visibility
                                else -> Icons.Filled.VisibilityOff
                            }

                            val description = when (state.passwordVisible) {
                                true -> "Показать пароль"
                                else -> "Скрыть пароль"
                            }

                            IconButton(
                                onClick = { vm.onTogglePasswordVisible() }
                            ) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = description
                                )
                            }
                        },
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        value = state.password,
                        onValueChange = { vm.onPasswordChange(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onKeyEvent {
                                when (it.key) {
                                    Key.Enter -> {
                                        vm.onRegister()
                                        true
                                    }
                                    else -> false
                                }
                            },
                        label = { Text("Повтор пароля") },
                        isError = state.repeatPasswordError != null,
                        supportingText = {
                            state.repeatPasswordError?.let {
                                Text(it.message)
                            }
                        },
                        visualTransformation = when (state.repeatPasswordVisible) {
                            true -> VisualTransformation.None
                            else -> PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val image = when (state.repeatPasswordVisible) {
                                true -> Icons.Filled.Visibility
                                else -> Icons.Filled.VisibilityOff
                            }

                            val description = when (state.repeatPasswordVisible) {
                                true -> "Показать повтор пароля"
                                else -> "Скрыть повтор пароля"
                            }

                            IconButton(
                                onClick = { vm.onToggleRepeatPasswordVisible() }
                            ) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = description
                                )
                            }
                        },
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = { vm.onRegister() }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        value = state.repeatPassword,
                        onValueChange = { vm.onRepeatPasswordChange(it) }
                    )

                    state.signError?.let {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = it.message,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { vm.onRegister() }
            ) {
                Text("Зарегистрироваться")
            }

        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_4
)
@Composable
fun RegisterScreenPreview() {
    LoginSampleTheme {
        RegisterScreen()
    }
}