package dev.ag6.mclauncher.instance.component.settings

import javafx.beans.property.*

//TODO: Allow for more complex settings, allow for onChange listeners
sealed class Setting<T>(
    val name: String,
    val description: String?,
    private val property: Property<T>,
    val category: SettingCategory = SettingCategory.OTHER
) {
    var value: T
        get() = property.value
        set(newValue) {
            property.value = newValue
        }

    fun getProperty(): Property<T> = property
}

class NumberSetting(
    name: String,
    description: String?,
    property: Property<Number> = SimpleIntegerProperty(),
    category: SettingCategory = SettingCategory.OTHER
) : Setting<Number>(name, description, property, category)

class StringSetting(
    name: String,
    description: String?,
    property: Property<String> = SimpleStringProperty(),
    category: SettingCategory = SettingCategory.OTHER
) : Setting<String>(name, description, property, category)

class BooleanSetting(
    name: String,
    description: String?,
    property: Property<Boolean> = SimpleBooleanProperty(),
    category: SettingCategory = SettingCategory.OTHER
) : Setting<Boolean>(name, description, property, category)

class ChoiceSetting<T>(
    name: String,
    description: String?,
    property: Property<T> = SimpleObjectProperty(),
    val choices: List<T>,
    category: SettingCategory = SettingCategory.OTHER
) : Setting<T>(name, description, property, category)
