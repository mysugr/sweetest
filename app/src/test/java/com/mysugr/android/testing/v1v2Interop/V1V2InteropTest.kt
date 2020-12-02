package com.mysugr.android.testing.v1v2Interop

import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import dev.sweetest.api.v2.Steps
import dev.sweetest.api.v2.framework.base.JUnit4Test
import org.junit.After
import org.junit.Before
import org.junit.Test

private class A

private class B

class V1V2InteropTest {

    class AStepsV1(testContext: TestContext) : BaseSteps(testContext) {
        override fun configure() = super.configure()
            .provide<A> { aInstance!! }

        fun doDomethingA() {}
    }

    class BStepsV2(testContext: TestContext) : Steps(testContext) {
        init {
            provide<B> { bInstance!! }
        }

        fun doDomethingB() {}
    }

    @Before
    fun setUp() {
        // Makes sure configuration bleeding over to next test doesn't cause false positives
        aInstance = A()
        bInstance = B()
    }

    @After
    fun tearDown() {
        aInstance = null
        bInstance = null
    }

    @Test
    fun withV1Test() {
        val testV1 = object : BaseJUnitTest() {

            override fun configure() = super.configure()
                .requireSteps<AStepsV1>()
                .requireSteps<BStepsV2>()

            val aSteps by steps<AStepsV1>()
            val bSteps by steps<BStepsV2>()
            val a by dependency<A>()
            val b by dependency<B>()
        }

        with(testV1) {
            junitBefore()

            // Steps objects from both versions can be retrieved
            aSteps.doDomethingA()
            bSteps.doDomethingB()

            // Dependency configurations from both versions apply
            assert(a === aInstance)
            assert(b === bInstance)

            junitAfter()
        }
    }

    @Test
    fun withV2Test() {
        val testV2 = object : JUnit4Test() {

            init {
                requireSteps<AStepsV1>()
                requireSteps<BStepsV2>()
            }

            val aSteps by steps<AStepsV1>()
            val bSteps by steps<BStepsV2>()
            val a by dependency<A>()
            val b by dependency<B>()
        }

        with(testV2) {
            junitBefore()

            // Steps objects from both versions can be retrieved
            aSteps.doDomethingA()
            bSteps.doDomethingB()

            // Dependency configurations from both versions apply
            assert(a === aInstance)
            assert(b === bInstance)

            junitAfter()
        }
    }

    companion object {
        private var aInstance: A? = null
        private var bInstance: B? = null
    }
}
