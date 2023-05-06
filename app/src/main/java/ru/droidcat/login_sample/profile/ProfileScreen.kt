package ru.droidcat.login_sample.profile

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import ru.droidcat.login_sample.ui.theme.LoginSampleTheme

@Composable
fun ProfileScreen(
    onLogout: (() -> Unit)? = null
) {

    val vm: ProfileViewModel = viewModel(ProfileViewModel::class.java)
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.getUserInfo()
    }

    LaunchedEffect(Unit) {
        vm.logOutSuccessful.collectLatest { loggedOut ->
            loggedOut.takeIf { it }?.let {
                onLogout?.invoke()
            }
        }
    }

    AnimatedVisibility(
        visible = state.loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Загружаем информацию пользователя...")
            }
        }
    }

    AnimatedVisibility(
        visible = state.loading.not() && state.getUserError != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.getUserError?.let { Text(it.message) }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { vm.getUserInfo() }
                ) {
                    Text("Попробовать заново")
                }
            }
        }
    }

    AnimatedVisibility(
        visible = state.loggingOut,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Выходим из аккаунта...")
            }
        }
    }

    AnimatedVisibility(
        visible = state.loggingOut.not() && state.logOutError != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.logOutError?.let { Text(it.message) }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { vm.onLogOut() }
                ) {
                    Text("Попробовать заново")
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { vm.cancelLogOut() }
                ) {
                    Text("Отменить")
                }
            }
        }
    }

    AnimatedVisibility(
        visible = state.loggingOut.not() &&
                state.loading.not() &&
                state.logOutError == null &&
                state.getUserError == null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            Text("Привет, ${state.username}")

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                onClick = { vm.onLogOut() }
            ) {
                Text("Выйти из аккаунта")
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
fun PreviewProfileScreen() {
    LoginSampleTheme {
        ProfileScreen()
    }
}