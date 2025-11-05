package io.github.diskria.projektor.extensions

import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.assign

fun JavaToolchainSpec.configureAdoptium(javaVersion: Int) {
    languageVersion = JavaLanguageVersion.of(javaVersion)
    vendor = JvmVendorSpec.ADOPTIUM
    implementation = JvmImplementation.VENDOR_SPECIFIC
}
