package com.consultis.composezoom.demo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.consultis.composezoom.R
import com.consultis.composezoom.widget.ContentScaleSelectionMenu
import com.consultis.composezoom.widget.TitleMedium
import com.consultis.zoom.ZoomableImage
import com.consultis.zoom.rememberZoomState
import com.consultis.zoom.zoom
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ZoomDemo() {

    println("🍎 ZoomDemo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xffECEFF1))
    ) {

        var contentScale by remember { mutableStateOf(ContentScale.Fit) }

        val imageBitmap = ImageBitmap.imageResource(
            LocalContext.current.resources,
            R.drawable.landscape4
        )

        Spacer(modifier = Modifier.height(40.dp))
        ContentScaleSelectionMenu(contentScale = contentScale) {
            contentScale = it
        }

        ZoomableImageDemo(contentScale, imageBitmap)
        ZoomModifierDemo(imageBitmap)
        ZoomGestureCallbackDemo(imageBitmap)
    }
}

@Composable
private fun ZoomableImageDemo(contentScale: ContentScale, imageBitmap: ImageBitmap) {

    Text(
        text = "ZoomableImage",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    TitleMedium(text = "clipTransformToContentScale false")

    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        clipTransformToContentScale = false
    )

    Spacer(modifier = Modifier.height(40.dp))

    TitleMedium(text = "clipTransformToContentScale = true")
    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        clipTransformToContentScale = true
    )

    Spacer(modifier = Modifier.height(40.dp))
    TitleMedium(
        text = "clip = false\n" +
                "limitPan = false"
    )

    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        clip = false,
        limitPan = false

    )

    Spacer(modifier = Modifier.height(40.dp))
    TitleMedium(text = "rotatable = true")

    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        clipTransformToContentScale = true,
        rotatable = true
    )

    Spacer(modifier = Modifier.height(40.dp))
}

@Composable
private fun ZoomGestureCallbackDemo(imageBitmap: ImageBitmap) {
        TitleMedium(text = "gesture callbacks")

    var text by remember {
        mutableStateOf(
            "Use pinch or fling gesture\n" +
                    "to observe data"
        )
    }


    Canvas(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f)
            .zoom(
                zoomState = rememberZoomState(
                    limitPan = false,
                    rotatable = true,
                    initialPan = Offset(x=300f, y=500f)
                ),
                clip = true,
                consume = true,
                onTap = {
                    text = "onTap data: $it"
                }
            ),
    ) {
        drawCircle(
            color= Color.Red,
        )
    }

    Text(text)
}

@Composable
private fun ZoomModifierDemo(imageBitmap: ImageBitmap) {
    Column(
        modifier = Modifier
    ) {


        Text(
            text = "Zoom Modifier",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        TitleMedium(text = "Modifier.zoom(clip = true, limitPan = false)")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    clip = true,
                    zoomState = rememberZoomState(limitPan = false),
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = true, limitPan = true)")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    clip = true,
                    zoomState = rememberZoomState(limitPan = true),
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = true, rotate = true)")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    clip = true,
                    zoomState = rememberZoomState(rotatable = true),

                    ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = false, rotate = true)")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    clip = false,
                    zoomState = rememberZoomState(rotatable = true),
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = true, limitPan = false)")
        DrawPolygonPath(
            modifier = Modifier
                .padding(8.dp)
                .shadow(1.dp)
                .background(Color.White)
                .fillMaxWidth()
                .height(200.dp)
                .zoom(
                    clip = true,
                    zoomState = rememberZoomState(limitPan = false),
                )
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = false, limitPan = false)")
        DrawPolygonPath(
            modifier = Modifier
                .padding(8.dp)
                .shadow(1.dp, clip = false)
                .background(Color.White)
                .fillMaxWidth()
                .height(200.dp)
                .zoom(
                    clip = false,
                    zoomState = rememberZoomState(limitPan = false),
                )
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = false, limitPan = true)")
        DrawPolygonPath(
            modifier = Modifier
                .padding(8.dp)
                .shadow(1.dp, clip = false)
                .background(Color.White)
                .fillMaxWidth()
                .height(200.dp)
                .zoom(
                    clip = false,
                    zoomState = rememberZoomState(
                        limitPan = true
                    )
                )
        )
    }
}

@Composable
private fun DrawPolygonPath(modifier: Modifier) {
    var sides by remember { mutableStateOf(6f) }
    var cornerRadius by remember { mutableStateOf(1f) }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2
        val path = createPolygonPath(cx, cy, sides.roundToInt(), radius)

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(
                width = 4.dp.toPx(),
                pathEffect = PathEffect.cornerPathEffect(cornerRadius)
            )
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        androidx.compose.material.Text(text = "Sides ${sides.roundToInt()}")
        Slider(
            value = sides,
            onValueChange = { sides = it },
            valueRange = 3f..12f,
            steps = 10
        )

        androidx.compose.material.Text(text = "CornerRadius ${cornerRadius.roundToInt()}")

        Slider(
            value = cornerRadius,
            onValueChange = { cornerRadius = it },
            valueRange = 0f..50f,
        )
    }
}


fun createPolygonPath(cx: Float, cy: Float, sides: Int, radius: Float): Path {
    val angle = 2.0 * Math.PI / sides

    return Path().apply {
        moveTo(
            cx + (radius * cos(0.0)).toFloat(),
            cy + (radius * sin(0.0)).toFloat()
        )
        for (i in 1 until sides) {
            lineTo(
                cx + (radius * cos(angle * i)).toFloat(),
                cy + (radius * sin(angle * i)).toFloat()
            )
        }
        close()
    }
}
