package dev.ag6.mclauncher.instance.component.minecraft

import dev.ag6.mclauncher.minecraft.asset.AssetIndex
import dev.ag6.mclauncher.minecraft.piston.PistonVersionMetadata
import java.nio.file.Path

data class MinecraftLaunchData(
    var pistonMetadata: PistonVersionMetadata,
    var assetIndex: AssetIndex,
    var libraries: List<Path>,
    var clientJar: Path
)
