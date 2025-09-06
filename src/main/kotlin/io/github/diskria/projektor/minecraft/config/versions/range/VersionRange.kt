package io.github.diskria.projektor.minecraft.config.versions.range

import io.github.diskria.projektor.minecraft.config.versions.VersionBound
import io.github.diskria.utils.kotlin.extensions.common.failWithWrongUsage

sealed class VersionRange {

    abstract val any: String

    protected abstract fun rangeInternal(minVersion: VersionBound?, maxVersion: VersionBound?): String

    fun min(version: VersionBound): String =
        rangeInternal(version, null)

    fun max(version: VersionBound): String =
        rangeInternal(null, version)

    fun range(minVersion: VersionBound? = null, maxVersion: VersionBound? = null): String {
        if (minVersion == null && maxVersion == null) {
            useAnyInsteadEmptyRange()
        }
        return rangeInternal(minVersion, maxVersion)
    }

    protected fun useAnyInsteadEmptyRange(): Nothing =
        failWithWrongUsage(useInsteadThis = ::any.name)
}
