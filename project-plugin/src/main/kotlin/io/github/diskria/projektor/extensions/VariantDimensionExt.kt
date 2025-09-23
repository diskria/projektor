package io.github.diskria.projektor.extensions

import com.android.build.api.dsl.VariantDimension
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.common.failWithDetails
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.common.primitiveTypeNameOrNull
import io.github.diskria.kotlin.utils.extensions.wrapWithDoubleQuote
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty

fun VariantDimension.putBuildConfigs(configs: Map<String, Any>) {
    configs.forEach { (name, value) ->
        putBuildConfig(name, value)
    }
}

fun <T> VariantDimension.putBuildConfig(name: String, value: T) {
    val isString = value is String
    val type = value?.primitiveTypeNameOrNull() ?: run {
        if (isString) String::class.className()
        else failWithDetails {
            val name by name.toAutoNamedProperty()
            val value by value?.javaClass?.name.toAutoNamedProperty()
            listOf(name, value)
        }
    }
    buildConfigField(type, name, value.toString().modifyIf(isString) { it.wrapWithDoubleQuote() })
}
