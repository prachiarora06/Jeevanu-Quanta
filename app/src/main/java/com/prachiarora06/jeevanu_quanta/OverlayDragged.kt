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

fun transformOverlay(
    offset: Offset,
    rect: Rect,
    canvasRect: Rect,
    draggedAt: OverlayDraggedAt,
    minimumSize: Float,
): Rect {
    fun Float.adjustLeft(minimumVertexDistance: Float) =
        coerceAtLeast(canvasRect.left).coerceAtMost(rect.right - minimumVertexDistance)

    fun Float.adjustTop(minimumVertexDistance: Float) =
        coerceAtLeast(canvasRect.top).coerceAtMost(rect.bottom - minimumVertexDistance)

    fun Float.adjustRight(minimumVertexDistance: Float) =
        coerceAtMost(canvasRect.right).coerceAtLeast(rect.left + minimumVertexDistance)

    fun Float.adjustBottom(minimumVertexDistance: Float) =
        coerceAtMost(canvasRect.bottom).coerceAtLeast(rect.top + minimumVertexDistance)

    var newRect = when (draggedAt) {
        OverlayDraggedAt.TOPLEFT -> Rect(
            (rect.left + offset.x).adjustLeft(minimumSize),
            (rect.top + offset.y).adjustTop(minimumSize),
            rect.right,
            rect.bottom
        )

        OverlayDraggedAt.TOPRIGHT -> Rect(
            rect.left,
            (rect.top + offset.y).adjustTop(minimumSize),
            (rect.right + offset.x).adjustRight(minimumSize),
            rect.bottom
        )

        OverlayDraggedAt.BOTTOMLEFT -> Rect(
            (rect.left + offset.x).adjustLeft(minimumSize),
            rect.top,
            rect.right,
            (rect.bottom + offset.y).adjustBottom(minimumSize)
        )

        OverlayDraggedAt.BOTTOMRIGHT -> Rect(
            rect.left,
            rect.top,
            (rect.right + offset.x).adjustRight(minimumSize),
            (rect.bottom + offset.y).adjustBottom(minimumSize)
        )

        OverlayDraggedAt.INSIDE -> Rect(
            rect.left + offset.x,
            rect.top + offset.y,
            rect.right + offset.x,
            rect.bottom + offset.y
        )
    }

    if (newRect.left < canvasRect.left) {
        newRect = newRect.translate(
            canvasRect.left - newRect.left,
            0f
        )
    }
    if(newRect.right > canvasRect.right) {
        newRect = newRect.translate(
            canvasRect.right - newRect.right,
            0f
        )
    }
    if(newRect.top < canvasRect.top) {
        newRect = newRect.translate(
            0f,
            canvasRect.top - newRect.top
        )
    }
    if(newRect.bottom > canvasRect.bottom) {
        newRect = newRect.translate(
            0f,
            canvasRect.bottom - newRect.bottom
        )
    }

    return newRect
}
