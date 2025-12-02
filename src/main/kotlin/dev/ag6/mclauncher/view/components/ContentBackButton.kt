package dev.ag6.mclauncher.view.components

import dev.ag6.mclauncher.view.ContentManager
import javafx.scene.Node
import javafx.scene.control.Button

class ContentBackButton : Button {
    constructor(text: String) : super(text)

    constructor(text: String, graphic: Node) : super(text, graphic)

    constructor() : super("Back")

    init {
        prefWidth = 100.0
        setOnAction { ContentManager.goBack() }
    }
}