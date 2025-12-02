package dev.ag6.mclauncher.view.instance

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.instance.InstanceManager
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox

class InstanceList : StackPane() {
    private val instances: ObservableList<GameInstance> = FXCollections.observableArrayList<GameInstance>().apply {
        Bindings.bindContent(this, InstanceManager.instances)
    }

    private val gridView: TilePane = createGridView()
    private val gridViewContainer: ScrollPane = createGridViewContainer()
    private val listView: VBox = createListView()
    private val listViewContainer: ScrollPane = createListViewContainer()

    init {
        gridViewContainer.isVisible = false

        children += listViewContainer
        children += gridViewContainer

        refreshItems()

        instances.addListener(ListChangeListener { refreshItems() })
    }

    private fun createGridView(): TilePane = TilePane(15.0, 15.0).apply {
        padding = Insets(15.0)
    }

    private fun createGridViewContainer(): ScrollPane = ScrollPane(gridView).apply {
        alignment = Pos.TOP_LEFT
        isFitToWidth = true
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    }

    private fun createListView(): VBox = VBox(10.0)

    private fun createListViewContainer(): ScrollPane = ScrollPane(listView).apply {
        isFitToWidth = true
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    }

    fun toggleView() {
        gridViewContainer.isVisible = !gridViewContainer.isVisible
        listViewContainer.isVisible = !listViewContainer.isVisible
    }

    fun filterItems(predicate: (GameInstance) -> Boolean) {
        listView.children.clear()
        gridView.children.clear()
        for (instance in instances) {
            if (predicate(instance)) {
                listView.children.add(InstanceListItem(instance))
                gridView.children.add(InstanceGridItem(instance))
            }
        }
    }

    private fun refreshItems() {
        gridView.children.clear()
        listView.children.clear()
        for (instance in instances) {
            listView.children.add(InstanceListItem(instance))
            gridView.children.add(InstanceGridItem(instance))
        }
    }
}