package io.github.diskria.projektor.minecraft.versions

@JvmInline
value class InclusiveVersion(val value: String) : VersionBound {
    override fun toString(): String = value
}
