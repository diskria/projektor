package io.github.diskria.projektor.core.model

import kotlinx.serialization.Serializable as JsonSerializable
import java.io.Serializable as PropertySerializable

@JsonSerializable
class DistributableProjektModel(
    val homepageUrl: String?,
    val readmeShieldMarkdowns: List<String>,
) : PropertySerializable
