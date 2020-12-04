@file:Suppress("DEPRECATION")

package dev.sweetest.v1

import dev.sweetest.internal.InternalBaseSteps
import dev.sweetest.internal.TestContext
import dev.sweetest.internal.environment.getCurrentTestContext
import dev.sweetest.v1.internal.ApiTestElement
import dev.sweetest.v1.internal.builder.StepsBuilder
import dev.sweetest.v1.internal.coroutines.CoroutinesTestContext
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

private const val COROUTINE_SCOPE_DEPRECATION_MESSAGE =
    "Please don't use steps classes as CoroutineScope! " +
        "This feature is deprecated and will be removed in the next API version!"

private const val TEST_CONTEXT_DEPRECATION_MESSAGE =
    "of version 2.0.0 TestContext isn't needed and is in fact IGNORED."

private const val REPLACE_WITH = "BaseSteps()"

@Deprecated(BASE_CLASS_DEPRECATION_MESSAGE)
abstract class BaseSteps
@Deprecated("$MODULE_CONFIG_DEPRECATION_MESSAGE.", ReplaceWith("BaseSteps(testContext)"))
constructor(private val moduleTestingConfiguration: ModuleTestingConfiguration? = null) :
    ApiTestElement(), InternalBaseSteps, CoroutineScope {

    // Use this constructor!
    constructor() : this(null)

    @Deprecated(
        "$MODULE_CONFIG_DEPRECATION_MESSAGE, and as $TEST_CONTEXT_DEPRECATION_MESSAGE",
        ReplaceWith(REPLACE_WITH)
    )
    constructor(
        @Suppress("UNUSED_PARAMETER")
        testContext: TestContext, // not used anymore, just part of legacy constructor signature
        moduleTestingConfiguration: ModuleTestingConfiguration? = null
    ) : this(moduleTestingConfiguration)

    @Deprecated(
        "As $TEST_CONTEXT_DEPRECATION_MESSAGE",
        ReplaceWith(REPLACE_WITH)
    )
    constructor(
        @Suppress("UNUSED_PARAMETER")
        testContext: TestContext // not used anymore, just part of legacy constructor signature
    ) : this(getCurrentTestContext(), null)

    override val testContext = getCurrentTestContext()

    open fun configure() =
        StepsBuilder(this, testContext, moduleTestingConfiguration)

    init {
        @Suppress("LeakingThis")
        configure().freeze()
    }

    @Deprecated(COROUTINE_SCOPE_DEPRECATION_MESSAGE)
    override val coroutineContext: CoroutineContext
        get() {
            // Unfortunately there is no way to mark the use of BaseSteps as CoroutineScope as deprecated in the IDE so we need to print the message
            println(
                "\u001B[31mWARNING:\u001B[0m $COROUTINE_SCOPE_DEPRECATION_MESSAGE"
            )
            return testContext[CoroutinesTestContext].coroutineContext
        }
}

operator fun <T : InternalBaseSteps> T.invoke(run: T.() -> Unit) = run(this)
