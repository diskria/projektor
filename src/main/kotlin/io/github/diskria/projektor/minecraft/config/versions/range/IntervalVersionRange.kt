package io.github.diskria.projektor.minecraft.config.versions.range

import io.github.diskria.projektor.minecraft.config.versions.VersionBound
import io.github.diskria.utils.kotlin.BracketsType
import io.github.diskria.utils.kotlin.Constants
import io.github.diskria.utils.kotlin.extensions.wrapWithBrackets

object IntervalVersionRange : VersionRange() {

    private const val SEPARATOR: Char = Constants.Char.COMMA
    private val EXCLUSIVE_BRACKETS_TYPE: BracketsType = BracketsType.ROUND

    override val any: String = SEPARATOR.toString().wrapWithBrackets(EXCLUSIVE_BRACKETS_TYPE)

    override fun rangeInternal(minVersion: VersionBound?, maxVersion: VersionBound?): String {
        val min = when (minVersion) {
            null -> EXCLUSIVE_BRACKETS_TYPE.openingChar.toString()
            else -> buildString {
                val bracketsType = getBracketsType(minVersion.isInclusive())
                append(bracketsType.openingChar)
                append(minVersion.toString())
            }
        }
        val max = when (maxVersion) {
            null -> EXCLUSIVE_BRACKETS_TYPE.closingChar.toString()
            else -> buildString {
                val bracketsType = getBracketsType(maxVersion.isInclusive())
                append(maxVersion.toString())
                append(bracketsType.closingChar)
            }
        }
        return min + SEPARATOR + max
    }

    private fun getBracketsType(isInclusive: Boolean): BracketsType =
        if (isInclusive) BracketsType.SQUARE
        else EXCLUSIVE_BRACKETS_TYPE
}
