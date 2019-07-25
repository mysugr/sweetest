package com.mysugr.android.testing.example.moduleconfig

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.Ignore
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        plugin = ["pretty"],
        features = ["src/test/resources/features"],
        glue = ["com.mysugr.android.testing.example.moduleconfig"])
class FeatureRunner