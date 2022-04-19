package com.ldl.magnitudo.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ldl.magnitudo.R
import com.ldl.magnitudo.productSans
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private var stepSize = 1
private var factor = 1f

@Composable
fun CircularSlider(
    shapeSize: Dp = 200.dp,
    label: String = "",
    textColor: Color = colorResource(id = R.color.accent),
    pinColor: Color = colorResource(id = R.color.accent),
    inactiveBarColor: Color = Color.Black.copy(alpha = 0.10f),
    activeBarColor: Color = Color.Red,
    initialValue: Any = 0,
    strokeWidth: Dp = 6.dp,
    valueRange: List<Any> = listOf(),
    onValueChange: (Any) -> Unit
) {
    stepSize = valueRange.size
    factor = (270f / stepSize)
    val startAngle = getAngle(valueRange.indexOf(initialValue))

    var radius by remember {
        mutableStateOf(0f)
    }

    var shapeCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var handleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var angle by remember {
        mutableStateOf(startAngle)
    }

    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(shapeSize)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val newOffset = handleCenter + dragAmount
                        angle = getRotationAngle(newOffset, shapeCenter)

                        onValueChange.invoke(valueRange[getStep(drawAngle(angle))])

                        change.consumeAllChanges()
                    }
                }
                .padding(30.dp)

        ) {
            shapeCenter = center

            radius = size.minDimension / 2
            val x = (shapeCenter.x + cos(Math.toRadians(angle)) * radius).toFloat()
            val y = (shapeCenter.y + sin(Math.toRadians(angle)) * radius).toFloat()

            if (drawAngle(angle) > 0f && drawAngle(angle) < 270f)
                handleCenter = Offset(x, y)
            else {
                if (drawAngle(angle) == 0f) {
                    val x1 = (shapeCenter.x + cos(Math.toRadians(getAngle(0))) * radius).toFloat()
                    val y1 = (shapeCenter.y + sin(Math.toRadians(getAngle(0))) * radius).toFloat()
                    handleCenter = Offset(x1, y1)
                }
                if (drawAngle(angle) == 270f) {
                    val x1 = (shapeCenter.x + cos(Math.toRadians(405.0)) * radius).toFloat()
                    val y1 = (shapeCenter.y + sin(Math.toRadians(405.0)) * radius).toFloat()
                    handleCenter = Offset(x1, y1)
                }
            }

            drawArc(
                color = inactiveBarColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                size = Size(size.width, size.height),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = activeBarColor,
                startAngle = 135f,
                sweepAngle = drawAngle(angle = angle),
                useCenter = false,
                size = Size(size.width, size.height),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            drawCircle(
                color = pinColor,
                center = handleCenter,
                radius = (strokeWidth.toPx() * 2).dec()
            )
        }
        Canvas(modifier = Modifier
            .size(shapeSize / 2)
            .pointerInput(Unit) {

            }) {
            drawCircle(color = Color.Transparent)
        }
        Column(
            modifier = Modifier
                .background(Color.Transparent)
                .wrapContentSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valueRange[getStep(drawAngle(angle))].toString(),
                style = TextStyle(
                    color = textColor,
                    fontSize = 48.sp,
                    fontFamily = productSans,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.padding(16.dp, 4.dp, 16.dp, 2.dp)
            )

            Text(
                text = label,
                style = TextStyle(
                    color = colorResource(id = R.color.colorPrimaryDark),
                    fontSize = 14.sp,
                    fontFamily = productSans,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start
                )
            )
        }
        Log.i("Value", initialValue.toString())
    }
}

private fun drawAngle(angle: Double): Float {
    return if (angle > 0 && angle < 45)
        (225 + angle).toFloat()
    else if (angle >= 45 && angle < 119)
        270f
    else if (angle >= 119 && angle < 135)
        0f
    else if (angle == 270.0)
        angle.toFloat()
    else
        angle.toFloat() - 135f
}

private fun getAngle(step: Int): Double {
    return if(step == stepSize - 1)
        270.0
    else
        (step * factor + 135f).toDouble()
}

private fun getStep(angle: Float): Int {
    val step = (angle / factor).roundToInt()
    if (step >= stepSize)
        return stepSize -1
    return step
}

private fun getRotationAngle(currentPosition: Offset, center: Offset): Double {
    val (dx, dy) = currentPosition - center
    val theta = atan2(dy, dx).toDouble()

    var angle = Math.toDegrees(theta)

    if (angle < 0) {
        angle += 360.0
    }

    return angle
}
