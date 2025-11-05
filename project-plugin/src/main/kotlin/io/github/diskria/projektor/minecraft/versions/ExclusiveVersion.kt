package io.github.diskria.projektor.minecraft.versions

@JvmInline
value class ExclusiveVersion(val value: String) : VersionBound {
    override fun toString(): String = value
}
