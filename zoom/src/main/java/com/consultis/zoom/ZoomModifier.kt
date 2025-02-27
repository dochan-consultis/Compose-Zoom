package com.consultis.zoom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import com.consultis.zoom.util.TappedPoint
import com.consultis.zoom.util.update
import com.smarttoolfactory.gesture.detectTransformGestures
import kotlinx.coroutines.launch

/**
 * Modifier that zooms in or out of Composable set to.
 * @param key is used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
fun Modifier.zoom(
    key: Any? = Unit,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null,
    onTap: ((TappedPoint) -> Unit)? = null
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()
        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = zoomState.limitPan && !zoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {

            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            zoomState.size = this.size

            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd?.invoke(zoomState.zoomData)
                },
                onGesture = { centroid, pan, zoom, rotation, _, changes ->
                    val newCentroid = getNewCentroid(changes, centroid)

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            centroid = newCentroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotation
                        )
                    }

                    onGesture?.invoke(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            zoomState.size = this.size
            detectTapGestures(
                onTap = { offset ->
                    val width = zoomState.size.width
                    val height = zoomState.size.height
                    val zoom = zoomState.zoom

                    val toCanvasX =
                        convertCanvasCoordinate(offset.x.toDouble() - zoomState.pan.x, (width * zoom) / 2)
                    val toCanvasY =
                        convertCanvasCoordinate(offset.y.toDouble() - zoomState.pan.y, (height * zoom) / 2)

                    val x = (toCanvasX - (width / 2)) / zoom
                    val y = (toCanvasY - (height / 2)) / zoom
                    onTap?.invoke(TappedPoint(x, y))
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(zoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(transformModifier)
                .then(tapModifier)
                .then(graphicsModifier)

        )
    },
    inspectorInfo = {
        name = "zoom"
        properties["key"] = key
        properties["clip"] = clip
        properties["consume"] = consume
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

/**
 * Modifier that zooms in or out of Composable set to.
 * [key1], [key2] are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
fun Modifier.zoom(
    key1: Any?,
    key2: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null,
    onTap: ((TappedPoint) -> Unit)? = null
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = zoomState.limitPan && !zoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key1, key2) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            zoomState.size = this.size
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd?.invoke(zoomState.zoomData)
                },
                onGesture = { centroid, pan, zoom, rotation, _, changes ->
                    val newCentroid = getNewCentroid(changes, centroid)

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            centroid = newCentroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotation
                        )
                    }

                    onGesture?.invoke(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key1, key2) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            zoomState.size = this.size
            detectTapGestures(
                onTap = { offset ->
                    val x = (offset.x.toDouble() - zoomState.pan.x) / zoomState.zoom
                    val y = (offset.y.toDouble() - zoomState.pan.y) / zoomState.zoom

                    onTap?.invoke(TappedPoint(x, y))
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(zoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(transformModifier)
                .then(tapModifier)
                .then(graphicsModifier)

        )
    },
    inspectorInfo = {
        name = "zoom"
        properties["key1"] = key1
        properties["key2"] = key2
        properties["clip"] = clip
        properties["consume"] = consume
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

/**
 * Modifier that zooms in or out of Composable set to.
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
fun Modifier.zoom(
    vararg keys: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null,
    onTap: ((TappedPoint) -> Unit)? = null
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = zoomState.limitPan && !zoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(*keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            zoomState.size = this.size
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd?.invoke(zoomState.zoomData)
                },
                onGesture = { centroid, pan, zoom, rotation, _, changes ->
                    val newCentroid = getNewCentroid(changes, centroid)

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            centroid = newCentroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotation
                        )
                    }

                    onGesture?.invoke(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            zoomState.size = this.size
            detectTapGestures(
                onTap = { offset ->
                    val x = (offset.x.toDouble() - zoomState.pan.x) / zoomState.zoom
                    val y = (offset.y.toDouble() - zoomState.pan.y) / zoomState.zoom

                    onTap?.invoke(TappedPoint(x, y))
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(zoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(transformModifier)
                .then(tapModifier)
                .then(graphicsModifier)

        )
    },
    inspectorInfo = {
        name = "zoom"
        properties["keys"] = keys
        properties["clip"] = clip
        properties["consume"] = consume
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

/**
 * Modifier that zooms in or out of Composable set to.
 * @param key is used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 */
fun Modifier.zoom(
    key: Any? = Unit,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    key = key,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = null,
    onGestureEnd = null,
    onGesture = null
)

/**
 * Modifier that zooms in or out of Composable set to.
 * [key1] and [key2] are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 */
fun Modifier.zoom(
    key1: Any?,
    key2: Any?,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    key1 = key1,
    key2 = key2,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = null,
    onGestureEnd = null,
    onGesture = null
)

/**
 * Modifier that zooms in or out of Composable set to.
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 */
fun Modifier.zoom(
    vararg keys: Any?,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    keys = keys,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = null,
    onGestureEnd = null,
    onGesture = null
)

internal val ZoomState.zoomData: ZoomData
    get() = ZoomData(
        zoom = zoom,
        pan = pan,
        rotation = rotation
    )


fun convertCanvasCoordinate(originalValue: Double, maxZoom: Float): Double {
    val rangeSpan = maxZoom * 2
    val shiftedValue = originalValue + maxZoom
    return shiftedValue / rangeSpan * rangeSpan
}

private fun getNewCentroid(
    changes: List<PointerInputChange>,
    centroid: Offset
) = if (changes.size == 2) {
    centroid
} else {
    Offset(0f,0f)
}