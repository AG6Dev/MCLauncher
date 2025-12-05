package dev.ag6.mclauncher.instance.component.settings

import javafx.beans.property.Property

sealed class Setting<T>(
    val name: String,
    val description: String?,
    val value: Property<T>,
    val category: SettingCategory = SettingCategory.OTHER
)

class NumberSetting(
    name: String,
    description: String?,
    value: Property<Number>,
    category: SettingCategory = SettingCategory.OTHER
) : Setting<Number>(name, description, value, category)

class StringSetting(
    name: String,
    description: String?,
    value: Property<String>,
    category: SettingCategory = SettingCategory.OTHER
) : Setting<String>(name, description, value, category)

class BooleanSetting(
    name: String,
    description: String?,
    value: Property<Boolean>,
    category: SettingCategory = SettingCategory.OTHER
) : Setting<Boolean>(name, description, value, category)

class ChoiceSetting<T>(
    name: String,
    description: String?,
    value: Property<T>,
    val choices: List<T>,
    category: SettingCategory = SettingCategory.OTHER
) : Setting<T>(name, description, value, category)
