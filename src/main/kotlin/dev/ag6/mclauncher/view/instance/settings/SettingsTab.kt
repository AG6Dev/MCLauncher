package dev.ag6.mclauncher.view.instance.settings

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.instance.settings.*
import javafx.beans.property.Property
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter

class SettingsTab(category: SettingCategory, private val settings: List<Setting<*>>) : Tab(category.categoryName) {
    init {
        isClosable = false

        content = ScrollPane(createSettingsPane()).apply {
            isFitToWidth = true
        }
    }

    private fun createSettingsPane(): VBox = VBox(10.0).apply {
        children += settings.map { createSetting(it) }
    }

    private fun createSetting(setting: Setting<*>): HBox = HBox(10.0).apply {
        val control = when (setting) {
            is StringSetting -> createStringSetting(setting)
            is NumberSetting -> createNumberSetting(setting)
            is BooleanSetting -> createBooleanSetting(setting)
            is ChoiceSetting<*> -> createChoiceSetting(setting)
        }
        setting.description?.let { Tooltip.install(control, Tooltip(it)) }

        children += control

        alignment = Pos.CENTER_LEFT
    }

    private fun createStringSetting(setting: StringSetting): VBox = VBox(5.0).apply {
        children += Label(setting.name)

        val textField = TextField()
        textField.textProperty().bindBidirectional(setting.getProperty())
        children += textField
    }

    private fun createNumberSetting(setting: NumberSetting): VBox = VBox(5.0).apply {
        children += Label(setting.name)

        val textField = TextField()
        textField.textProperty().bindBidirectional(setting.getProperty(), NumberStringConverter())
        children += textField
    }

    private fun createChoiceSetting(setting: ChoiceSetting<*>): VBox = VBox(5.0).apply {
        children += Label(setting.name)

        val comboBox = ComboBox<Any>()
        comboBox.items.addAll(setting.choices)
        comboBox.valueProperty().bindBidirectional(setting.getProperty() as Property<Any>)
        children += comboBox
    }

    private fun createBooleanSetting(setting: BooleanSetting): VBox = VBox(5.0).apply {
        val checkBox = CheckBox(setting.name)
        checkBox.selectedProperty().bindBidirectional(setting.getProperty())
        children += checkBox
    }

    companion object {
        fun generalTab(instance: GameInstance): SettingsTab {
            val generalSettings = instance.getInstanceCoreSettings()
            return SettingsTab(SettingCategory.GENERAL, generalSettings)
        }
    }

}