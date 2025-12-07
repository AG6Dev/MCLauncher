package dev.ag6.mclauncher.view.instance

import dev.ag6.mclauncher.view.ContentManager
import dev.ag6.mclauncher.view.View
import dev.ag6.mclauncher.view.components.TwoButtonToggle
import dev.ag6.mclauncher.view.create_instance.CreateInstanceView
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region

class InstancesView : View {
    private val instanceList: InstanceList = InstanceList()

    override fun build(): Region {
        BorderPane.setMargin(instanceList, Insets(10.0, 0.0, 0.0, 0.0))
        return createContainer()
    }

    private fun createContainer(): BorderPane = BorderPane().apply {
        top = createHeaderBox()
        center = instanceList
    }

    private fun createHeaderBox(): HBox = HBox(10.0).apply {
        val searchField = createSearchField()
        HBox.setHgrow(searchField, Priority.ALWAYS)
        children += searchField
        children += createViewToggle()
        children += createSortByField()
        children += createInstanceButton()
    }

    private fun createSearchField(): TextField = TextField().apply {
        promptText = "Search instances..."
        onKeyTyped = EventHandler {
            val query = text.lowercase()
            instanceList.filterItems { instance ->
                instance.name.lowercase().contains(query)
            }
        }
    }

    private fun createViewToggle(): TwoButtonToggle = TwoButtonToggle("fas-list", "fas-grip").apply {
        valueProperty().addListener { _, oldValue, newValue ->
            instanceList.toggleView()
        }
    }

    private fun createSortByField(): ChoiceBox<String> = ChoiceBox<String>().apply {
        items += listOf("Name", "Last Played")
        selectionModel.selectFirst()
        selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            when (newValue) {
                "Name" -> instanceList.instances.sortBy { it.name.lowercase() }
                "Last Played" -> instanceList.instances.sortBy { it.lastPlayed }
            }
        }
    }



    private fun createInstanceButton(): Button = Button().apply {
        text = "Create Instance"
        prefWidth = 150.0
        onAction = EventHandler {
            ContentManager.changeView(CreateInstanceView())
        }
    }
}