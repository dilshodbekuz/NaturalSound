package uz.apprica.calmSounds.composable

import android.graphics.drawable.Drawable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.isActive
import uz.apprica.calmSounds.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val maxInitialSnowflakes = 100
private val incrementRange = 0.2f..0.6f
private val sizeRange = 14f..22f
private const val angleSeed = 2.0f
private const val baseFrameDurationMillis = 16L
private const val baseSpeedPxAt60Fps = 7

fun Modifier.snowfall(): Modifier = composed {
    val context = LocalContext.current
    val drawables = remember {
        listOf(
            ContextCompat.getDrawable(context, R.drawable.snowflake_svgrepo_com)!!,
            ContextCompat.getDrawable(context, R.drawable.snowflake_2)!!,
            ContextCompat.getDrawable(context, R.drawable.snowflake_3)!!,
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    var snowflakesState by remember { mutableStateOf(SnowflakesState()) }
    var isActive by remember { mutableStateOf(true) }
    var lastTickNanos by remember { mutableLongStateOf(0L) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    isActive = false
                    lastTickNanos = snowflakesState.tickNanos
                }

                Lifecycle.Event.ON_RESUME -> {
                    isActive = true
                    snowflakesState = snowflakesState.copy(tickNanos = lastTickNanos)
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isActive) {
        while (isActive && coroutineContext.isActive) {
            withFrameNanos { frameTimeNanos ->
                snowflakesState = snowflakesState.update(frameTimeNanos)
            }
        }
    }

    this
        .onSizeChanged { newSize ->
            snowflakesState = snowflakesState.resize(newSize, preserveExisting = true)
        }
        .drawWithContent {
            drawContent()
            if (isActive) {
                drawIntoCanvas { canvas ->
                    snowflakesState.draw(canvas, drawables)
                }
            }
        }
}

@Immutable
internal data class SnowflakesState(
    val tickNanos: Long = -1L,
    val snowflakes: ImmutableList<Snowflake> = persistentListOf(),
    val canvasSize: IntSize = IntSize(0, 0),
    val maxSnowflakes: Int = 45,
    val snowflakeCreationChance: Float = 0.8f,
) {
    fun resize(newSize: IntSize, preserveExisting: Boolean = false): SnowflakesState {
        return if (preserveExisting && snowflakes.isNotEmpty()) {
            val adjustedSnowflakes = snowflakes.map { it.copyWithNewCanvasSize(newSize) }
            copy(
                snowflakes = adjustedSnowflakes.toImmutableList(),
                canvasSize = newSize,
            )
        } else {
            copy(
                snowflakes = createInitialSnowflakes(newSize).toImmutableList(),
                canvasSize = newSize,
            )
        }
    }

    fun update(frameTimeNanos: Long): SnowflakesState {
        val elapsedMillis = if (tickNanos > 0) {
            (frameTimeNanos - tickNanos) / 1_000_000
        } else {
            0
        }

        val updatedSnowflakes = snowflakes
            .map { it.update(elapsedMillis) }
            .filter { it.isVisible() }
            .toMutableList()

        while (updatedSnowflakes.size < maxSnowflakes && Random.nextFloat() < snowflakeCreationChance) {
            updatedSnowflakes.add(createSnowflake(canvasSize))
        }

        return copy(
            tickNanos = frameTimeNanos,
            snowflakes = updatedSnowflakes.toImmutableList(),
        )
    }

    fun draw(canvas: Canvas, drawables: List<Drawable>) {
        snowflakes.forEach { it.draw(canvas, drawables) }
    }

    companion object {
        private fun createInitialSnowflakes(canvasSize: IntSize): List<Snowflake> {
            val initialCount = (maxInitialSnowflakes * (canvasSize.width / 1000f)).toInt()
                .coerceIn(5, 25) //Increased max
            return List(initialCount) { createSnowflake(canvasSize) }
        }

        private fun createSnowflake(canvasSize: IntSize): Snowflake {
            return Snowflake(
                incrementFactor = incrementRange.random(),
                size = sizeRange.random(),
                canvasSize = canvasSize,
                position = randomStartingPosition(canvasSize),
                angle = angleSeed.randomAngle(),
                drawableIndex = Random.nextInt(2),
                rotation = Random.nextFloat() * 360f,
            )
        }

        private fun randomStartingPosition(canvasSize: IntSize): Offset {
            val startX = Random.nextInt(canvasSize.width).toFloat()
            val startY = -sizeRange.endInclusive - Random.nextFloat() * canvasSize.height * 0.2f
            return Offset(startX, startY)
        }
    }
}

internal data class Snowflake(
    private val incrementFactor: Float,
    private val size: Float,
    private val canvasSize: IntSize,
    private val position: Offset,
    private val angle: Double,
    private val drawableIndex: Int,
    private val rotation: Float,
) {
    fun update(elapsedMillis: Long): Snowflake {
        val increment =
            incrementFactor * elapsedMillis * baseSpeedPxAt60Fps / baseFrameDurationMillis

        val xDelta = (increment * cos(angle) * 0.4).toFloat()
        val yDelta = (increment * sin(angle)).toFloat()

        val newRotation = (rotation + (increment * 0.5f)) % 360f

        return copy(
            position = Offset(position.x + xDelta, position.y + yDelta),
            rotation = newRotation,
        )
    }

    fun isVisible(): Boolean {
        return position.y <= canvasSize.height + size &&
                position.x >= -size &&
                position.x <= canvasSize.width + size
    }

    fun copyWithNewCanvasSize(newSize: IntSize): Snowflake {
        val xRatio = newSize.width.toFloat() / canvasSize.width
        val yRatio = newSize.height.toFloat() / canvasSize.height

        val newPosition = Offset(
            position.x * xRatio,
            position.y * yRatio,
        )

        return copy(
            canvasSize = newSize,
            position = newPosition,
        )
    }

    fun draw(canvas: Canvas, drawables: List<Drawable>) {
        drawables.getOrNull(drawableIndex)?.let { drawable ->
            canvas.withSave {
                val halfSize = size / 2
                val left = position.x - halfSize
                val top = position.y - halfSize
                val right = position.x + halfSize
                val bottom = position.y + halfSize

                canvas.translate(position.x, position.y)
                canvas.rotate(rotation)
                canvas.translate(-position.x, -position.y)

                drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())

                drawable.draw(canvas.nativeCanvas)
            }
        }
    }
}

private fun ClosedRange<Float>.random() = Random.nextFloat() * (endInclusive - start) + start
private fun Float.randomAngle() = Random.nextDouble(0.0, this.toDouble())
