package com.udacity

//Clickable was originally "Clicked"
sealed class ButtonState {
    object Clickable : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}