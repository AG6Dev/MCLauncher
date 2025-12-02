package dev.ag6.mclauncher.view.components

import io.github.palexdev.mfxresources.fonts.MFXFontIcon
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class TwoButtonToggle(
    leftIcon: String,
    rightIcon: String,
) : Pane() {
    private val toggle = ToggleGroup()

    private val _value: ObjectProperty<Selection> = SimpleObjectProperty(Selection.LEFT)
    var value: Selection
        get() = _value.get()
        set(newValue) = _value.set(newValue)

    private val _leftIcon: StringProperty = SimpleStringProperty(leftIcon)
    var leftIcon: String
        get() = _leftIcon.get()
        set(newValue) = _leftIcon.set(newValue)

    private val _rightIcon: StringProperty = SimpleStringProperty(rightIcon)
    var rightIcon: String
        get() = _rightIcon.get()
        set(newValue) = _rightIcon.set(newValue)

    fun valueProperty() = _value
    fun leftIconProperty() = _leftIcon
    fun rightIconProperty() = _rightIcon

    private val leftButton = createToggleButton(_leftIcon)
    private val rightButton = createToggleButton(_rightIcon)

    init {
        children += createContainer()
        leftButton.isSelected = true
        _value.bind(Bindings.createObjectBinding(this::updateValue, toggle.selectedToggleProperty()))
    }

    private fun createContainer(): VBox = VBox(10.0).apply {
        children += buttonContainer()
        isFillWidth = false
        alignment = Pos.CENTER
    }

    private fun buttonContainer(): HBox = HBox(10.0).apply {
        children += listOf(leftButton, rightButton)
        styleClass += "button-container"
    }

    private fun updateValue(): Selection {
        return when (toggle.selectedToggle) {
            leftButton -> Selection.LEFT
            rightButton -> Selection.RIGHT
            else -> Selection.LEFT
        }
    }

    private fun createToggleButton(iconName: StringProperty): ToggleButton = ToggleButton().apply {
        styleClass += "toggle-switch-button"
        toggle.toggles += this
        graphicProperty().bind(Bindings.createObjectBinding({ createIcon(iconName.value) }, iconName))
    }

    private fun createIcon(iconName: String) = MFXFontIcon(iconName, 18.0, Color.WHITE)

    enum class Selection {
        LEFT, RIGHT
    }
}