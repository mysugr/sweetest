package com.mysugr.sweetest.internal

/**
 * Intermediary solution until global state ([TestEnvironment]) is removed and [DependencyInitializerArgument] is not
 * late-initialized anymore.
 */
typealias DependencyInitializerArgumentProvider = () -> DependencyInitializerArgument