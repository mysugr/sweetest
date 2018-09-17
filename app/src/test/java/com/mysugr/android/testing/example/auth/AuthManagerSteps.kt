package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.util.isMock
import cucumber.api.java.Before
import org.junit.Assert.*
import org.mockito.Mockito.*

class AuthManagerSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
            .onSetUp(this::setUp)

    private val instance by dependency<AuthManager>()
    private val user by steps<UserSteps>()

    var loginOrRegisterThrowable: Throwable? = null

    private fun setUp() {
        if (instance.isMock) {
            `when`(instance.loginOrRegister(anyString(), anyString())).then {
                val password = it.arguments[1] as String
                if (user.isPasswordCorrect(password)) {
                    val email = it.arguments[0] as String
                    if (user.isUserExisting(email)) {
                        AuthManager.LoginOrRegisterResult.LOGGED_IN
                    } else {
                        AuthManager.LoginOrRegisterResult.REGISTERED
                    }
                } else {
                    throw AuthManager.WrongPasswordException()
                }
            }
        }
    }

    fun whenLoggingInOrRegistering(email: String? = null, password: String? = null) {
        instance.loginOrRegister(email ?: user.email, password ?: user.password)
    }

    fun whenLoggingOut() {
        instance.logout()
    }

    fun thenLoginOrRegisterIsCalled() {
        verify(instance).loginOrRegister(user.email, user.password)
    }

    fun thenLoginOrRegisterThrows() {
        assertNotNull(loginOrRegisterThrowable)
    }

}
