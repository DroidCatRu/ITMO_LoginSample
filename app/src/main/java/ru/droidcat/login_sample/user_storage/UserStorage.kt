package ru.droidcat.login_sample.user_storage

import kotlinx.coroutines.delay
import kotlin.random.Random

private val DEFAULT_USER = User("admin123")
private val DEFAULT_USER_CREDENTIALS = Credentials("admin123", "admin123")

private const val MAX_REQUEST_TIME = 3000L
private const val MIN_REQUEST_TIME = 600L

private const val SERVER_NOT_AVAILABLE_ERROR_THRESHOLD = 0.7f

object UserStorage {

    private val users = HashMap<Credentials, User>().apply {
        put(DEFAULT_USER_CREDENTIALS, DEFAULT_USER)
    }

    private var currentUser: User? = null

    suspend fun registerUser(login: String, password: String): Response {
        delay(Random.nextLong(MIN_REQUEST_TIME, MAX_REQUEST_TIME))

        if (Random.nextFloat() >= SERVER_NOT_AVAILABLE_ERROR_THRESHOLD) {
            return ServerNotAvailable
        }

        val newUser = User(login)
        val credentials = Credentials(login, password)

        return if (users.containsValue(newUser)) {
            currentUser = null
            UserAlreadyRegistered
        } else {
            users[credentials] = newUser
            currentUser = newUser
            UserResponse(newUser)
        }
    }

    suspend fun login(login: String, password: String): Response {
        delay(Random.nextLong(MIN_REQUEST_TIME, MAX_REQUEST_TIME))

        if (Random.nextFloat() >= SERVER_NOT_AVAILABLE_ERROR_THRESHOLD) {
            return ServerNotAvailable
        }

        val credentials = Credentials(login, password)
        currentUser = users[credentials]

        return currentUser?.let { UserResponse(it) } ?: UserNotExists
    }

    suspend fun getCurrentUser(): Response {
        delay(Random.nextLong(MIN_REQUEST_TIME, MAX_REQUEST_TIME))

        if (Random.nextFloat() >= SERVER_NOT_AVAILABLE_ERROR_THRESHOLD) {
            return ServerNotAvailable
        }

        return currentUser?.let { UserResponse(it) } ?: NoLoggedInUser
    }

    suspend fun logOut(): Response {
        delay(Random.nextLong(MIN_REQUEST_TIME, MAX_REQUEST_TIME))

        if (Random.nextFloat() >= SERVER_NOT_AVAILABLE_ERROR_THRESHOLD) {
            return ServerNotAvailable
        }

        currentUser = null

        return OperationSucceed
    }

}

data class Credentials(
    val login: String,
    val password: String
)