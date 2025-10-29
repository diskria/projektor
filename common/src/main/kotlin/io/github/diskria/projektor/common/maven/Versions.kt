package io.github.diskria.projektor.common.maven

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

@Serializable
data class Versions(
    @XmlElement(true)
    val version: List<String>,
)
