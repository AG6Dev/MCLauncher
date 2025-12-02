package dev.ag6.mclauncher.util

data class SemanticVersion(val major: Short, val minor: Short, val patch: Short) {
    companion object {
        fun fromString(string: String): SemanticVersion {
            val parts = string.split(".")
            val major = parts[0].toShort()
            val minor = parts[1].toShort()
            val patch = parts[2].toShort()
            return SemanticVersion(major, minor, patch)
        }
    }
}