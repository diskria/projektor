package builder.envs_generator.models

import java.io.Serializable

sealed interface Env : Serializable {
    val name: String
    val value: String
}

class ContextEnv(override val name: String, override val value: String) : Env

class SecretEnv(override val name: String) : Env {
    val secretName: String = if (name.startsWith("GITHUB_")) "GH_${name.removePrefix("GITHUB_")}" else name
    override val value: String get() = "secrets.$secretName"
}
