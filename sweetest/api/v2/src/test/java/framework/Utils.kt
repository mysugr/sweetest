package framework

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.usecases.proceedWorkflow
import dev.sweetest.api.v2.BaseTest

fun BaseTest.startWorkflow() {
    proceedWorkflow(this.internalTestContext[WorkflowTestContext])
}

fun BaseTest.finishWorkflow() {
    com.mysugr.sweetest.usecases.finishWorkflow(this.internalTestContext[WorkflowTestContext])
}
