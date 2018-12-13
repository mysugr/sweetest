package com.mysugr.sweetest.framework.coroutine

/**
 * Verifies a specific call order. If the actual order is different from the expected one, an
 * [AssertionError] is thrown.
 * Successful example:
 * ```
 * verifyOrder {
 *   order(1) // is called first
 *
 *   launch {
 *     order(3) // this is called after yield()
 *   }
 *
 *   order(2) // this is called second
 *   yield()
 *   order(4) // this is called after the launch block
 * }
 * ```
 *
 * Failing example:
 * ```
 * verifyOrder {
 *   order(1)
 *   order(3) // this will throw an [AssertionError], because order(2) was expected here instead
 *   order(2)
 * }
 * ```
 */
suspend fun verifyOrder(block: suspend OrderVerifier.() -> Unit) {
    block(OrderVerifier())
}

/**
 * Helper class for [verifyOrder].
 */
class OrderVerifier {
    private var currentOrderIndex = 0

    fun order(orderIndex: Int) {
        currentOrderIndex++

        if (orderIndex != currentOrderIndex) {
            throw AssertionError(
                    "Expected order($currentOrderIndex), but reached order($orderIndex) instead"
            )
        }
    }
}