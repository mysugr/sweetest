package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration

class ConfigurationsTestContext {

    private val moduleConfigurations = mutableListOf<ModuleTestingConfiguration>()

    fun put(configuration: ModuleTestingConfiguration) {
        if (!moduleConfigurations.contains(configuration)) {
            moduleConfigurations.add(configuration)
        }
    }
}
