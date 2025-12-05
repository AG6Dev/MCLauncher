package dev.ag6.mclauncher.instance.component.settings

enum class SettingCategory(val categoryName: String, val description: String? = null) {
    GENERAL("General"),
    JAVA("Java", "Settings related to the Java Runtime Environment"),
    OTHER("Other")
}