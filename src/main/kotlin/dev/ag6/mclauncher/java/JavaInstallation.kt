package dev.ag6.mclauncher.java

import dev.ag6.mclauncher.util.SemanticVersion
import java.nio.file.Path

data class JavaInstallation(
    private val javaVersion: SemanticVersion,
    private val vendor: String,
    private val location: Path
) {
}
