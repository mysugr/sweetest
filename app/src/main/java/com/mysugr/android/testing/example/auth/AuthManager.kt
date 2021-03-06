package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.net.AuthToken
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.android.testing.example.net.UsernameOrPasswordWrongException
import com.mysugr.android.testing.example.state.SessionStore

open class AuthManager(
    private val backendGateway: BackendGateway,
    private val sessionStore: SessionStore
) {

    open fun loginOrRegister(email: String, password: String): LoginOrRegisterResult {
        val exists = backendGateway.checkEmail(email)
        val authToken: AuthToken
        val result: LoginOrRegisterResult
        if (exists) {
            try {
                authToken = backendGateway.login(email, password)
            } catch (exception: UsernameOrPasswordWrongException) {
                throw WrongPasswordException()
            }
            result = LoginOrRegisterResult.LOGGED_IN
        } else {
            authToken = backendGateway.register(email, password)
            result = LoginOrRegisterResult.REGISTERED
        }
        val user = backendGateway.getUserData(authToken)
        sessionStore.beginSession(authToken, user)
        return result
    }

    fun logout() {
        sessionStore.endSession()
    }

    enum class LoginOrRegisterResult {
        LOGGED_IN,
        REGISTERED
    }

    class WrongPasswordException : Exception()
}
