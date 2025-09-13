package io.github.diskria.projektor.minecraft.config.versions.range

import io.github.diskria.projektor.minecraft.config.versions.VersionBound
import io.github.diskria.utils.kotlin.Constants

object InequalityVersionRange : VersionRange() {

    override val any: String = Constants.Char.ASTERISK.toString()

    override fun rangeInternal(minVersion: VersionBound?, maxVersion: VersionBound?): String {
        val min = minVersion?.let {
            buildString {
                append(Constants.Char.CLOSING_ANGLE_BRACKET)
                if (minVersion.isInclusive()) {
                    append(Constants.Char.EQUAL_SIGN)
                }
                append(minVersion.toString())
            }
        }
        val max = maxVersion?.let {
            buildString {
                append(Constants.Char.OPENING_ANGLE_BRACKET)
                if (maxVersion.isInclusive()) {
                    append(Constants.Char.EQUAL_SIGN)
                }
                append(maxVersion.toString())
            }
        }
        return when {
            min != null && max != null -> min + Constants.Char.SPACE + max
            min != null -> min
            max != null -> max
            else -> useAnyInsteadEmptyRange()
        }
    }
}
