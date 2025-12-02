package dev.ag6.mclauncher.util

enum class OperatingSystem {
    WINDOWS, MACOS, LINUX, UNKNOWN;

    companion object {
        val CURRENT_OS = determineOs()

        private fun determineOs(): OperatingSystem {
            val osName = System.getProperty("os.name").lowercase()
            return getOsFromString(osName)
        }

        fun getOsFromString(osName: String): OperatingSystem {
            return when {
                osName.contains("win") -> WINDOWS
                osName.contains("mac") || osName.contains("osx") -> MACOS
                osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> LINUX
                else -> UNKNOWN
            }
        }
    }
}