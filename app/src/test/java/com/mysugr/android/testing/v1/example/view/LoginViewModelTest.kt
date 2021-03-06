package com.mysugr.android.testing.v1.example.view

import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.android.testing.v1.example.auth.AuthManagerMockSteps
import com.mysugr.android.testing.v1.example.net.BackendFakeUser
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import org.junit.Test

class LoginViewModelTest : BaseJUnitTest() {

    private val sut by steps<LoginViewModelSteps>()
    private val authManager by steps<AuthManagerMockSteps>()

    private val user = BackendFakeUser("test@test.com", "supersecure")

    override fun configure() = super.configure()
        .onSetUp { sut.whenInitialized() }

    @Test
    fun `Login with existing email and correct password`() {
        authManager.givenExistingUser()
        sut {
            whenLoggingIn(user.email, user.password)
            thenLastStateIs(
                LoginViewModel.State.LoggedIn(
                    isNewUser = false
                )
            )
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedIn()
    }

    @Test
    fun `Login with non-existent email`() {
        sut {
            authManager.givenNewUser()
            whenLoggingIn(user.email, user.password)
            thenLastStateIs(
                LoginViewModel.State.LoggedIn(
                    isNewUser = true
                )
            )
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedInAsNewUser()
    }

    @Test
    fun `Login with wrong password`() {
        authManager.givenWrongPassword()
        sut {
            whenLoggingIn(user.email, user.password)
            thenLastStateIs(LoginViewModel.State.Error::class)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsPasswordErrorWrongPassword()
    }
}
