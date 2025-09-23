package io.github.diskria.projektor.properties

import io.github.diskria.projektor.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.wrapWithSingleQuote
import io.github.diskria.kotlin.utils.properties.common.AbstractAutoNamedProperty
import io.github.diskria.kotlin.utils.words.CamelCase
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
