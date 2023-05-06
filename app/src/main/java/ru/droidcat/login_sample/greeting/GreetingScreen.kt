package ru.droidcat.login_sample.greeting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private val greetings = listOf(
    "неизвестный пользователь",
    "давай знакомиться?",
    "а ты кто?",
    "мы знакомы?",
    "я уже где-то тебя видел",
    "друг",
)


@Composable
fun GreetingScreen(
    onRegisterClick: (() -> Unit)? = null,
    onLoginClick: (() -> Unit)? = null
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        val greeting = remember { greetings[Random.nextInt(0, greetings.lastIndex)] }

        Column {

            Text(
                style = MaterialTheme.typography.headlineMedium,
                text = "Привет, $greeting"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onLoginClick?.invoke() }
            ) {
                Text("Войти")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onRegisterClick?.invoke() }
            ) {
                Text("Зарегистрироваться")
            }
        }

    }

}