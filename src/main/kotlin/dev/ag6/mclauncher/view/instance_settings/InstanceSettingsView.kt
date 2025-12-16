package dev.ag6.mclauncher.view.instance_settings

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.instance.InstanceManager
import dev.ag6.mclauncher.instance.settings.Setting
import dev.ag6.mclauncher.instance.settings.SettingCategory
import dev.ag6.mclauncher.util.getImageOrDefault
import dev.ag6.mclauncher.view.ContentManager
import dev.ag6.mclauncher.view.View
import dev.ag6.mclauncher.view.components.ContentBackButton
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.text.Font
import java.util.*

class InstanceSettingsView(private val original: GameInstance) : View {
    private val editedInstance: GameInstance = original.clone()

    private val organisedSettings: EnumMap<SettingCategory, List<Setting<*>>> = EnumMap(SettingCategory::class.java)

    init {
        for (setting in editedInstance.configurableComponents.flatMap { it.getSettings() }) {
            val category = setting.category
            val settingsInCategory = organisedSettings.getOrDefault(category, emptyList())
            organisedSettings[category] = settingsInCategory + setting
        }
    }

    override fun build(): Region {
        return createContainer()
    }

    private fun createContainer(): BorderPane = BorderPane().apply {
        top = createHeaderBox()
        center = createOptions()
        bottom = createNavigationButtons()
    }

    private fun createOptions(): TabPane = TabPane().apply {
        tabs += SettingsTab.generalTab(editedInstance)
        for ((category, settings) in organisedSettings) {
            tabs += SettingsTab(category, settings)
        }
    }

    private fun createNavigationButtons(): HBox = HBox(10.0).apply {
        alignment = Pos.CENTER
        padding = Insets(10.0)
        children += ContentBackButton("Cancel")
        children += createApplyButton()
    }

    private fun createApplyButton(): Button = Button("Apply").apply {
        onAction = EventHandler {
            InstanceManager.updateInstance(original, editedInstance)
            ContentManager.goBack()
        }
    }

    private fun createHeaderBox(): HBox = HBox(10.0).apply {
        alignment = Pos.CENTER_LEFT
        padding = Insets(10.0)
        children += ImageView(getImageOrDefault(editedInstance.icon, "default_icons/grass.png")).apply {
            fitWidth = 64.0
            fitHeight = 64.0
        }
        children += Label("Settings for ${original.name}").apply {
            font = Font.font(32.0)
        }
    }
}