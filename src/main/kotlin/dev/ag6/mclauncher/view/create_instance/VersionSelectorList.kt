package dev.ag6.mclauncher.view.create_instance

import dev.ag6.mclauncher.minecraft.MinecraftVersion
import dev.ag6.mclauncher.minecraft.MinecraftVersionHandler
import dev.ag6.mclauncher.minecraft.Type
import io.github.palexdev.materialfx.utils.FXCollectors
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.util.Callback

class VersionSelectorList : TabPane() {
    init {
        val releaseTab = VersionTab("Release", Type.RELEASE)
        val snapshotTab = VersionTab("Snapshot", Type.SNAPSHOT)
        val oldBetaTab = VersionTab("Old Beta", Type.OLD_BETA)
        val oldAlphaTab = VersionTab("Old Alpha", Type.OLD_ALPHA)

        tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
        this.tabs.addAll(releaseTab, snapshotTab, oldBetaTab, oldAlphaTab)
    }

    fun getSelectedVersion(): MinecraftVersion {
        val selectedTab = this.selectionModel.selectedItem as VersionTab
        return selectedTab.listView.selectionModel.selectedItem
    }

    class VersionTab(title: String, versionType: Type) : Tab(title) {
        val listView: ListView<MinecraftVersion> = createListView(versionType)

        init {
            content = listView
        }

        private fun createListView(versionType: Type): ListView<MinecraftVersion> = ListView<MinecraftVersion>().apply {
            cellFactory = Callback { createCell() }
            items = MinecraftVersionHandler.minecraftVersions.stream().filter { it.type == versionType }
                .collect(FXCollectors.toList())
            selectionModel.selectFirst()
        }

        private fun createCell(): ListCell<MinecraftVersion> = object : ListCell<MinecraftVersion>() {
            public override fun updateItem(item: MinecraftVersion?, empty: Boolean) {
                super.updateItem(item, empty)
                text = if (empty) null else item?.id ?: "null"

            }
        }
    }
}