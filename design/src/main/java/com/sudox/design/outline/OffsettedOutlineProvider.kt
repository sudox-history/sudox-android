package com.sudox.design.outline

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class OffsettedOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        view.background.getOutline(outline)

        val radius = outline.getBorderRadius()
        val bounds = view.background.bounds
        val castedView = view as OffsettedOutlineView

        outline.setRoundRect(
                bounds.left + castedView.getLeftOutlineOffset(),
                bounds.top + castedView.getTopOutlineOffset(),
                bounds.right + castedView.getRightOutlineOffset(),
                bounds.bottom + castedView.getBottomOutlineOffset(),
                radius)
    }
}