package io.github.diskria.projektor.settings.properties

import io.github.diskria.projektor.settings.extensions.common.gradleError
import io.github.diskria.utils.kotlin.extensions.common.`dot․case`
import io.github.diskria.utils.kotlin.extensions.setCase
import io.github.diskria.utils.kotlin.extensions.toNullIfEmpty
import io.github.diskria.utils.kotlin.extensions.wrapWithSingleQuote
import io.github.diskria.utils.kotlin.properties.common.AbstractAutoNamedProperty
import io.github.diskria.utils.kotlin.words.CamelCase
import org.gradle.api.provider.ProviderFactory

class AutoNamedGradleProperty(val providerFactory: ProviderFactory) : AbstractAutoNamedProperty<String>() {

    override fun mapToValue(propertyName: String): String {
        val gradlePropertyName = propertyName.setCase(CamelCase, `dot․case`)
        return providerFactory.gradleProperty(gradlePropertyName).get().toNullIfEmpty()
            ?: gradleError("Missing ${gradlePropertyName.wrapWithSingleQuote()} property in gradle.properties")
    }
}

fun ProviderFactory.toAutoNamedGradleProperty(): AutoNamedGradleProperty =
    AutoNamedGradleProperty(this)
