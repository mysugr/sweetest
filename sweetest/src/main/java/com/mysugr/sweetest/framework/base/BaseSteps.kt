package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.StepsBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext

interface Steps

abstract class BaseSteps(
    private val testContext: TestContext,
    private val moduleTestingConfiguration: ModuleTestingConfiguration
) : Steps, TestingAccessor {

    open fun configure() = StepsBuilder(this, testContext, moduleTestingConfiguration)

    override val accessor = configure().build()
    protected val dependencies = accessor.dependencies
    protected val delegates = accessor.delegates
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)