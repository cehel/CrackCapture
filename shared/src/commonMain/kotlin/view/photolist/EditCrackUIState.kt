package view.photolist

import androidx.compose.foundation.gestures.Orientation
import model.CRACKLENGTHS
import model.CRACKWIDHTS

/**
 * UiState for the Edit Crack screen
 */
data class EditCrackUIState(
    val showCamera: Boolean = false,
    val description: String = "",
    val orientation: String = "",
    val orientationChoices: List<String> = Orientation.values().map { it.name },
    val width: String = "",
    val widthChoices: List<String> = CRACKWIDHTS,
    val length: String = "",
    val lengthChoices: List<String> = CRACKLENGTHS
)

