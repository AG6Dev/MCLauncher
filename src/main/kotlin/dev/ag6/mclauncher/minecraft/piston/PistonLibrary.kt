package dev.ag6.mclauncher.minecraft.piston

import com.google.gson.JsonObject
import dev.ag6.mclauncher.util.OperatingSystem

data class PistonLibrary(val name: String, val download: Download?, val rules: List<LibraryRule>?) {
    fun getJarPath(): String {
        return download?.path!!
    }

    fun isAllowedForSystem(): Boolean {
        if (rules == null) return true

        for (rule in rules) {
            if (rule.os == OperatingSystem.CURRENT_OS && rule.action == LibraryRule.Action.ALLOW) {
                return true
            }
        }
        return false
    }

    companion object {
        fun fromJson(json: JsonObject): PistonLibrary {
            val name = json.get("name").asString
            var download: Download? = null
            if (json.has("downloads")) {
                val downloadsJson = json.getAsJsonObject("downloads")
                if (downloadsJson.has("artifact")) {
                    download = Download.fromJson(downloadsJson.getAsJsonObject("artifact"))
                }
            }

            var rules: List<LibraryRule>? = null
            if (json.has("rules")) {
                val rulesJson = json.getAsJsonArray("rules")
                rules = rulesJson.map { LibraryRule.fromJson(it.asJsonObject) }
            }
            return PistonLibrary(name, download, rules)
        }
    }

    data class LibraryRule(val action: Action, val os: OperatingSystem?) {
        companion object {
            fun fromJson(json: JsonObject): LibraryRule {
                val action = when (json.get("action").asString) {
                    "allow" -> Action.ALLOW
                    "disallow" -> Action.DISALLOW
                    else -> throw IllegalArgumentException("Unknown action: ${json.get("action").asString}")
                }

                val os = if (json.has("os")) {
                    val osJson = json.getAsJsonObject("os")
                    OperatingSystem.getOsFromString(osJson.get("name").asString)
                } else {
                    null
                }

                return LibraryRule(action, os)
            }
        }

        enum class Action {
            ALLOW, DISALLOW
        }
    }
}
