package io.github.diskria.projektor.common.minecraft.sync.maven

import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@EncodeDefaults
data class MavenMetadata(
    @XmlElement(true)
    val groupId: String,

    @XmlElement(true)
    val artifactId: String,

    @XmlElement(true)
    @XmlSerialName("versioning")
    val versioning: Versioning,
)
