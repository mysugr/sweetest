package com.mysugr.sweetest.framework.environment

import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyConfigurationConsumer
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyInitializerContext
import com.mysugr.sweetest.framework.dependency.DependencyManager
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencySetup
import com.mysugr.sweetest.framework.dependency.DependencyStatesConsumer
import kotlin.reflect.KClass

object TestEnvironment {

    private val _dependencies = DependencyManager {
        DependencySetup.init(it)
    }

    private val dependenciesController = DependencyManager.Controller(_dependencies)

    val dependencies: DependencyAccessor = _dependencies

    fun reset() {
        dependenciesController.resetState()
    }
}

interface DependencySetupHandler {

    fun addConfiguration(configuration: DependencyConfiguration<*>)

    @Deprecated("Use addConfiguration(configuration) instead")
    // since it is deprecated, we need no additional codacy warnings on this
    @Suppress("LongParameterList")
    fun <T : Any> addConfiguration(
        clazz: KClass<T>,
        realInitializer: DependencyInitializer<T>? = null,
        mockInitializer: (DependencyInitializerContext.() -> T)? = null,
        dependencyMode: DependencyMode? = null,
        alias: KClass<*>? = null
    ): DependencyConfiguration<T>
}

interface DependencyAccessor {
    val configurations: DependencyConfigurationConsumer
    val states: DependencyStatesConsumer
}