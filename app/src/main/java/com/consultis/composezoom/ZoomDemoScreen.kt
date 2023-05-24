package com.consultis.composezoom

import androidx.compose.runtime.Composable
import com.consultis.composezoom.demo.*
import com.consultis.composezoom.widget.PagerContent
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ZoomDemoScreen() {
    PagerContent(
        content = mapOf<String, @Composable () -> Unit>(
            "Zoom" to { ZoomDemo() },
            "Zoom2" to { ZoomDemo2() },
            "Enhanced Zoom" to { EnhancedZoomDemo() },
            "Enhanced Zoom2" to { EnhancedZoomDemo2() },
            "Enhanced Zoom Crop" to { EnhancedZoomCropDemo() },
            "Animated Zoom" to { AnimatedZoomDemo() },
            "Zoomable List" to { ZoomableListDemo() }
        )
    )
}
