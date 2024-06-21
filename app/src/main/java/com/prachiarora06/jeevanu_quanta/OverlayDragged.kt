package com.prachiarora06.jeevanu_quanta

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

enum class OverlayDraggedAt {
    TOPLEFT,
    TOPRIGHT,
    BOTTOMLEFT,
    BOTTOMRIGHT,
    INSIDE
}

fun detectDraggedAt(offset: Offset, rect: Rect, vertexSize: Float): OverlayDraggedAt? {
    return when {
        Rect(rect.topLeft, vertexSize).contains(offset) -> OverlayDraggedAt.TOPLEFT
        Rect(rect.topRight, vertexSize).contains(offset) -> OverlayDraggedAt.TOPRIGHT
        Rect(rect.bottomLeft, vertexSize).contains(offset) -> OverlayDraggedAt.BOTTOMLEFT
        Rect(rect.bottomRight, vertexSize).contains(offset) -> OverlayDraggedAt.BOTTOMRIGHT
        rect.contains(offset) -> OverlayDraggedAt.INSIDE
        else -> null
    }
}

fun transformOverlay(offset: Offset, rect: Rect, canvasRect: Rect, draggedAt: OverlayDraggedAt): Rect {
    var newRect = when (draggedAt) {
        OverlayDraggedAt.TOPLEFT -> Rect(
            rect.left + offset.x,
            rect.top + offset.y,
            rect.right,
            rect.bottom
        )

        OverlayDraggedAt.TOPRIGHT -> Rect(
            rect.left,
            rect.top + offset.y,
            rect.right + offset.x,
            rect.bottom
        )

        OverlayDraggedAt.BOTTOMLEFT -> Rect(
            rect.left + offset.x,
            rect.top,
            rect.right,
            rect.bottom + offset.y
        )

        OverlayDraggedAt.BOTTOMRIGHT -> Rect(
            rect.left,
            rect.top,
            rect.right + offset.x,
            rect.bottom + offset.y
        )

        OverlayDraggedAt.INSIDE -> Rect(
            rect.left + offset.x,
            rect.top + offset.y,
            rect.right + offset.x,
            rect.bottom + offset.y
        )
    }

    newRect = when {
        newRect.left < canvasRect.left -> newRect.translate(
            canvasRect.left - newRect.left,
            0f
        )
        newRect.right > canvasRect.right -> newRect.translate(
            canvasRect.right - newRect.right,
            0f
        )
        newRect.top < canvasRect.top -> newRect.translate(
            0f,
            canvasRect.top - newRect.top
        )
        newRect.bottom > canvasRect.bottom -> newRect.translate(
            0f,
            canvasRect.bottom - newRect.bottom
        )
        else -> newRect
    }

    return newRect
}
