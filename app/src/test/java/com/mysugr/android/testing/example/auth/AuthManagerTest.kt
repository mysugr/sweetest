package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.net.BackendGatewaySteps
import com.mysugr.android.testing.example.state.SessionStoreSteps
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.sweetest.framework.base.*

import org.junit.Test

class AuthManagerTest : BaseJUnitTest(appModuleTestingConfiguration) {
    
    override fun configure() = super.configure()
            .requireReal<AuthManager>()

    private val user by steps<UserSteps>()
    private val sut by steps<AuthManagerSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewaySteps>()
    
    @Test
    fun `Login as existing user`() {
        sut.whenLoggingInOrRegistering()
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenLoggingIn()
        }
    }

    @Test(expected = AuthManager.WrongPasswordException::class)
    fun `Login as existing user with wrong password`() {
        user.correctPassword = false
        try {
            sut.whenLoggingInOrRegistering()
        } finally {
            sessionStore.thenSessionIsNotStarted()
            backendGateway {
                thenEmailIsChecked()
                thenLoggingIn()
            }
        }
    }

    @Test
    fun `Register new user`() {
        user.exists = false
        sut.whenLoggingInOrRegistering()
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenRegistered()
        }
    }

    @Test
    fun `Logging out`() {
        sut.whenLoggingOut()
        sessionStore.thenSessionIsEnded()
    }
    
}