package com.mysugr.sweetest.framework.configuration

import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencySetup
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.framework.factory.FactoryRunner
import com.mysugr.sweetest.framework.factory.FactoryRunner0
import com.mysugr.sweetest.framework.factory.FactoryRunner1
import com.mysugr.sweetest.framework.factory.FactoryRunner2
import com.mysugr.sweetest.framework.factory.FactoryRunner3
import kotlin.reflect.KClass

private const val dependencyModeDeprecationMessage = "Dependency mode constraints " +
    "(\"realOnly\", \"mockOnly\") are obsolete. Use \"any\" instead and add a `provide` " +
    "configuration in a test or steps class instead."

private const val dependencyInitializationDeprecationMessage = "Dependency initialization in the " +
    "module configuration level is obsolete. Add a `provide` configuration on a test or steps " +
    "class level instead."

fun moduleTestingConfiguration(
    vararg baseModuleTestingConfigurations: ModuleTestingConfiguration,
    run: (Dsl.MainScope.() -> Unit)? = null
): ModuleTestingConfiguration {

    // Force initialization before everything else
    TestEnvironment

    val scope = Dsl.MainScope()
    run?.invoke(scope)

    return ModuleTestingConfiguration(baseModuleTestingConfigurations.toList(), scope.factories.toList())
}

data class ModuleTestingConfiguration internal constructor(
    internal val baseModuleTestingConfigurations: List<ModuleTestingConfiguration>,
    internal val factories: List<FactoryRunner<*>>
)

class Dsl {

    class MainScope {

        @PublishedApi
        internal val factories = mutableListOf<FactoryRunner<*>>()

        @PublishedApi
        internal val dependencies = mutableListOf<DependencyConfiguration<*>>()

        @PublishedApi
        internal fun addDependency(configuration: DependencyConfiguration<*>) {
            dependencies.add(configuration)
            DependencySetup.addConfiguration(configuration)
        }

        // Supplier configuration

        inline fun <reified R : Any> factory(noinline createObject: () -> R) {
            factories.add(FactoryRunner0(R::class.java, createObject))
        }

        inline fun <reified T : Steps, reified R : Any> factory(noinline createObject: (T) -> R) {
            factories.add(FactoryRunner1(R::class.java, T::class.java, createObject))
        }

        inline fun <reified T1 : Steps, reified T2 : Steps, reified R : Any> factory(
            noinline createObject: (T1, T2) -> R
        ) {
            factories.add(FactoryRunner2(R::class.java, T1::class.java, T2::class.java, createObject))
        }

        inline fun <reified T1 : Steps, reified T2 : Steps, reified T3 : Steps, reified R : Any> factory(
            noinline createObject: (T1, T2, T3) -> R
        ) {
            factories.add(
                FactoryRunner3(
                    R::class.java, T1::class.java, T2::class.java, T3::class.java,
                    createObject
                )
            )
        }

        // Dependency configuration

        class LeftOperand

        data class RightOperand(
            @PublishedApi internal val addFunction: (dependencyMode: DependencyMode?, only: Boolean) -> Unit
        )

        data class AutoInitializerRightOperand(
            @PublishedApi internal val addFunction: () -> Unit
        )

        val dependency = LeftOperand()

        inline infix fun <reified T : Any> LeftOperand.any(clazz: KClass<T>) {
            anyInternal(this, clazz)
        }

        infix fun LeftOperand.any(rightOperand: RightOperand) {
            anyInternal(this, rightOperand)
        }

        @Deprecated(dependencyModeDeprecationMessage)
        infix fun LeftOperand.mockOnly(rightOperand: RightOperand) {
            mockOnlyInternal(this, rightOperand)
        }

        @Deprecated(dependencyModeDeprecationMessage)
        infix fun LeftOperand.realOnly(rightOperand: RightOperand) {
            realOnlyInternal(this, rightOperand)
        }

        @Deprecated(dependencyInitializationDeprecationMessage)
        inline fun <reified T : Any> initializer(noinline initializer: DependencyInitializer<T>): RightOperand =
            initializerInternal(T::class, initializer)

        inline fun <reified T : Any> of(): RightOperand =
            ofInternal(T::class)

        @Deprecated(dependencyInitializationDeprecationMessage)
        inline fun <reified T : Any> instance(instance: T) = instanceInternal(T::class, instance)

        // Events

        fun onInitialization(run: () -> Unit) = run()

        // Internal API

        @PublishedApi
        internal fun <T : Any> anyInternal(leftOperand: LeftOperand, type: KClass<T>) {
            dependencies.add(DependencyConfiguration(type, null, null))
        }

        @PublishedApi
        internal fun anyInternal(leftOperand: LeftOperand, rightOperand: RightOperand) {
            rightOperand.addFunction(null, false)
        }

        @PublishedApi
        internal fun mockOnlyInternal(leftOperand: LeftOperand, rightOperand: RightOperand) {
            rightOperand.addFunction(DependencyMode.MOCK, true)
        }

        @PublishedApi
        internal fun realOnlyInternal(leftOperand: LeftOperand, rightOperand: RightOperand) {
            rightOperand.addFunction(DependencyMode.REAL, true)
        }

        @PublishedApi
        internal fun <T : Any> initializerInternal(type: KClass<T>, initializer: DependencyInitializer<T>) =
            RightOperand { dependencyMode, only ->
                val finalDependencyMode = if (only) dependencyMode else null
                if (dependencyMode == DependencyMode.MOCK) {
                    addDependency(DependencyConfiguration(type, null, initializer, finalDependencyMode))
                } else {
                    addDependency(DependencyConfiguration(type, initializer, null, finalDependencyMode))
                }
            }

        @PublishedApi
        internal fun <T : Any> ofInternal(type: KClass<T>) =
            RightOperand { dependencyMode, only ->
                val finalDependencyMode = if (only) dependencyMode else null
                addDependency(DependencyConfiguration(type, null, null, finalDependencyMode))
            }

        @PublishedApi
        internal fun <T : Any> instanceInternal(type: KClass<T>, instance: T) =
            RightOperand { dependencyMode, only ->
                val finalDependencyMode = if (only) dependencyMode else null
                when (dependencyMode) {
                    DependencyMode.MOCK ->
                        addDependency(DependencyConfiguration(type, null, { instance }, finalDependencyMode))
                    else ->
                        addDependency(DependencyConfiguration(type, { instance }, null, finalDependencyMode))
                }
            }
    }
}
