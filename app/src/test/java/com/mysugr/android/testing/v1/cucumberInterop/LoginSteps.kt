package com.mysugr.android.testing.v1.cucumberInterop

import com.mysugr.android.testing.example.app.R
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.android.testing.v1.example.auth.AuthManagerSteps
import com.mysugr.android.testing.v1.example.net.BackendFakeSteps
import com.mysugr.android.testing.v1.example.state.SessionStoreSteps
import com.mysugr.android.testing.v1.example.view.LoginViewModelSteps
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.TestContext
import cucumber.api.java.Before
import cucumber.api.java.en.Then
import cucumber.api.java.en.When

class LoginSteps(testContext: TestContext) : BaseSteps(testContext) {

    override fun configure() = super.configure()
        .requireSteps<AuthManagerSteps>()
        .onSetUp {
            loginViewModel.whenInitialized()
        }

    val backend by steps<BackendFakeSteps>()

    private val loginViewModel by steps<LoginViewModelSteps>()
    private val sessionStore by steps<SessionStoreSteps>()

    @Before("@login-integration")
    fun dummy() {
    }

    @When("^trying to login or register with email address \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun whenLoginOrRegister(email: String, password: String) {
        loginViewModel.whenLoggingIn(email, password)
    }

    @Then("^the user \"([^\"]*)\" is logged in as an existing user$")
    fun thenLoggedInExistingUser(email: String) {
        loginViewModel.thenLastStateIs(LoginViewModel.State.LoggedIn(isNewUser = false))
        loginViewModel.thenStateIsLoggedInAsExistingUser()
        sessionStore.thenSessionIsStarted(email)
    }

    @Then("^the user \"([^\"]*)\" is logged in as a new user$")
    fun thenLoggedInNewUser(email: String) {
        loginViewModel.thenLastStateIs(LoginViewModel.State.LoggedIn(isNewUser = true))
        loginViewModel.thenStateIsLoggedInAsNewUser()
        sessionStore.thenSessionIsStarted(email)
    }

    @Then("^the user can't enter the app$")
    fun thenLoginFailed() {
        loginViewModel.thenStateIsNotLoggedIn()
    }

    @Then("^a wrong email address is detected$")
    fun thenLoginFailedIncorrectEmail() {
        loginViewModel.thenLastStateIs(LoginViewModel.State.Error(emailError = R.string.error_invalid_email))
    }
}
