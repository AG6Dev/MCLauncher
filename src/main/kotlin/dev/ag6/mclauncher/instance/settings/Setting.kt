package dev.ag6.mclauncher.instance.settings

import javafx.beans.property.*

//TODO: Allow for more complex settings, allow for onChange listeners
sealed class Setting<T>(
    val name: String,
    val description: String?,
    private val property: Property<T>,
    val category: SettingCategory = SettingCategory.OTHER,
    private val onChange: (oldValue: T, newValue: T) -> Unit = { _, _ -> }
) {
    var value: T
        get() = property.value
        set(newValue) {
            onChange.invoke(property.value, newValue)
            property.value = newValue
        }

    fun getProperty(): Property<T> = property
}

class NumberSetting(
    name: String,
    description: String?,
    defaultValue: Number = 0,
    property: Property<Number> = SimpleIntegerProperty(defaultValue.toInt()),
    category: SettingCategory = SettingCategory.OTHER,
    onChange: (oldValue: Number, newValue: Number) -> Unit = { _, _ -> }
) : Setting<Number>(name, description, property, category, onChange)

class StringSetting(
    name: String,
    description: String?,
    property: Property<String> = SimpleStringProperty(),
    category: SettingCategory = SettingCategory.OTHER,
    onChange: (oldValue: String, newValue: String) -> Unit = { _, _ -> }
) : Setting<String>(name, description, property, category, onChange)

class BooleanSetting(
    name: String,
    description: String?,
    property: Property<Boolean> = SimpleBooleanProperty(),
    category: SettingCategory = SettingCategory.OTHER,
    onChange: (oldValue: Boolean, newValue: Boolean) -> Unit = { _, _ -> }
) : Setting<Boolean>(name, description, property, category, onChange)

class ChoiceSetting<T>(
    name: String,
    description: String?,
    property: Property<T> = SimpleObjectProperty(),
    val choices: List<T>,
    category: SettingCategory = SettingCategory.OTHER,
    onChange: (oldValue: T, newValue: T) -> Unit = { _, _ -> }
) : Setting<T>(name, description, property, category, onChange)
