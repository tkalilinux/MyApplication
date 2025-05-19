package com.example.myapplication


import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.text.TextPaint
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.drawable.toDrawable
import android.annotation.SuppressLint
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withSave

class AnimatedCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    // Original canvas dimensions and aspect ratio
    private val originalWidth = 200f
    private val originalHeight = 200f
    private val aspectRatio = originalWidth / originalHeight

    // Current view dimensions and scaling factors
    private var currentWidth = 0f
    private var currentHeight = 0f
    private var scaleX = 1f
    private var scaleY = 1f

    // Path and paint for shape 0
    private val path0 = createPath0()
    private val paint0 = createPaint0()
    private val pathBounds0 = RectF().apply { path0.computeBounds(this, true) }
    // Text elements for item 1
    private val textPaint1 = createTextPaint1()
    private val text1 = "Chào bạn!"
    private val textOriginalX1 = 7f
    private val textOriginalY1 = 30f
    private val textPosition1 = PointF(textOriginalX1, textOriginalY1)
    // Bitmap elements for item 2
    private val bitmap2 = loadBitmap2(context)
    private val bitmapOriginalX2 = 106f
    private val bitmapOriginalY2 = 70f
    private val bitmapOriginalWidth2 = 56.25f
    private val bitmapOriginalHeight2 = 100f
    private val bitmapRect2 = RectF(
        bitmapOriginalX2,
        bitmapOriginalY2,
        bitmapOriginalX2 + bitmapOriginalWidth2,
        bitmapOriginalY2 + bitmapOriginalHeight2
    )

    private var animationProgress0 = 0f
    private var animationProgress1 = 0f
    private var animationProgress2 = 0f


    private val animatorSet = AnimatorSet()
    private var isAnimating = false

    init {
        setupAnimations()
    }


    /**
     * Path function for shape 0
     */
    private fun createPath0(): Path {
        return Path().apply {
            moveTo(-1f, 199f)
            lineTo(-2f, 101f)
            lineTo(160f, 101f)
            cubicTo(120.23956298828125f, 148.88888549804688f, 48.23956298828125f, 109.88888549804688f, 35.23956298828125f, 173.88888549804688f)
            lineTo(115.23956298828125f, 177.88888549804688f)
            lineTo(34f, 192f)
            lineTo(14f, 175f)
        }
    }

    /**
     * Paint function for shape 0
     */
    private fun createPaint0(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {

            // Stroke settings
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 4f
            strokeCap = Paint.Cap.BUTT
            strokeJoin = Paint.Join.MITER


            color = "#00FF00".toColorInt()

            // Fill settings
            style = Paint.Style.FILL_AND_STROKE

            color = "#FF0000".toColorInt()

            // Shadow settings
            setShadowLayer(
                9f,
                13f,
                20f,
                "#80000000".toColorInt()
            )
        }
    }

    /**
     * Text paint function for text item 1
     */
    private fun createTextPaint1(): TextPaint {
        return TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 30f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)

            // Shadow


            // Fill

            style = Paint.Style.FILL

            shader = LinearGradient(
                0f, 0f, 200f, 0f,
                intArrayOf("#FF0000".toColorInt(), "#FFFF00".toColorInt()),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )

            // Stroke

        }
    }

    /**
     * Bitmap function for item 2
     */
    private fun loadBitmap2(context: Context): BitmapDrawable {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bg2)

        return bitmap.toDrawable(context.resources).apply {
            // Initial bounds will be based on original coordinates
            // The actual positioning will be handled in onSizeChanged and onDraw
            setBounds(
                0,
                0,
                56.25f.toInt(),
                100f.toInt()
            )

        }
    }

    /**
     * Animation function for item 0
     */
    private fun applyAnimations0(
        canvas: Canvas,
        path: Path, paint: Paint,
        progress: Float
    ) {

        val bounds = RectF()
        path.computeBounds(bounds, true)
        val centerX = bounds.centerX()
        val centerY = bounds.centerY()


        // Draw animation
        if (progress < 1f) {
            val pathMeasure = PathMeasure(path, false)
            val partialPath = Path()
            pathMeasure.getSegment(0f, pathMeasure.length * progress, partialPath, true)
            canvas.drawPath(partialPath, paint)
            return
        }

        // Move animation
        val moveX = 0f + (0f) * progress
        val moveY = 0f + (-100f) * progress
        canvas.translate(moveX, moveY)
    }

    /**
     * Animation function for item 1
     */
    private fun applyTextAnimations1(
        canvas: Canvas,
        paint: TextPaint, position: PointF, text: String,
        progress: Float
    ) {

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val centerX = position.x + bounds.width() / 2f
        val centerY = position.y - bounds.height() / 2f


        // Move animation
        val moveX = 0f + (50f) * progress
        val moveY = 0f + (150f) * progress
        canvas.translate(moveX, moveY)
    }

    /**
     * Animation function for bitmap 2
     */
    private fun applyBitmapAnimations2(
        canvas: Canvas,
        bitmap: BitmapDrawable,
        progress: Float
    ) {
        val bounds = bitmap.bounds
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()

        // Rotate animation
        val angle = 0f + (360f) * progress
        canvas.rotate(angle, centerX, centerY)
    }


    private fun setupAnimations() {
        val animators = mutableListOf<Animator>()

        // Animator for item 0
        val animator0 = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 4000L
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                animationProgress0 = animation.animatedValue as Float
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        animators.add(animator0)

        // Animator for item 1
        val animator1 = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000L
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                animationProgress1 = animation.animatedValue as Float
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        animators.add(animator1)

        // Animator for item 2
        val animator2 = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000L
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                animationProgress2 = animation.animatedValue as Float
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        animators.add(animator2)

        if (animators.isNotEmpty()) {
            animatorSet.playTogether(animators)

            if (true) {
                startAnimations()
            }
        }
    }

    fun startAnimations() {
        if (!isAnimating && animatorSet.childAnimations != null) {
            // Reset all animation progresses
            animationProgress0 = 0f
            animationProgress1 = 0f
            animationProgress2 = 0f

            animatorSet.start()
            isAnimating = true
        }
    }

    fun stopAnimations() {
        if (isAnimating) {
            animatorSet.cancel()
            isAnimating = false

            // Reset all animation progresses
            animationProgress0 = 0f
            animationProgress1 = 0f
            animationProgress2 = 0f

            invalidate()
        }
    }

    fun updateAnimationState(animating: Boolean) {
        if (animating && !isAnimating) {
            startAnimations()
        } else if (!animating && isAnimating) {
            stopAnimations()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // Calculate the ideal width and height while maintaining aspect ratio
        var finalWidth = widthSize
        var finalHeight = heightSize

        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            // Width is fixed, adjust height to maintain aspect ratio
            finalHeight = (finalWidth / aspectRatio).toInt()
        } else if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
            // Height is fixed, adjust width to maintain aspect ratio
            finalWidth = (finalHeight * aspectRatio).toInt()
        } else if (widthMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            // Neither dimension is fixed, use original canvas size
            finalWidth = originalWidth.toInt()
            finalHeight = originalHeight.toInt()
        }

        // Apply padding
        val widthWithPadding = finalWidth + paddingLeft + paddingRight
        val heightWithPadding = finalHeight + paddingTop + paddingBottom

        setMeasuredDimension(widthWithPadding, heightWithPadding)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Calculate the available space for drawing
        currentWidth = (w - paddingLeft - paddingRight).toFloat()
        currentHeight = (h - paddingTop - paddingBottom).toFloat()

        // Calculate scaling factors
        scaleX = currentWidth / originalWidth
        scaleY = currentHeight / originalHeight

        // Update positions for all elements based on the new scale
        updateElementPositions()
    }

    private fun updateElementPositions() {
        // Update text positions
        textPosition1.set(
            textOriginalX1 * scaleX,
            textOriginalY1 * scaleY
        )

        bitmapRect2.set(
            bitmapOriginalX2 * scaleX,
            bitmapOriginalY2 * scaleY,
            (bitmapOriginalX2 + bitmapOriginalWidth2) * scaleX,
            (bitmapOriginalY2 + bitmapOriginalHeight2) * scaleY
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Apply padding translation
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        // Apply scaling
        canvas.scale(scaleX, scaleY)

        canvas.withSave {
            applyAnimations0(this, path0, paint0, animationProgress0)
        }

        canvas.withSave {
            // For text, we need to apply scaling to the position but not the font size
            canvas.translate(textPosition1.x / scaleX, textPosition1.y / scaleY)
            applyTextAnimations1(this, textPaint1, PointF(0f, 0f), text1, animationProgress1)
            drawText(text1, 0f, 0f, textPaint1)
        }

        canvas.withSave {
            // For bitmap, we need to apply scaling to the position
            canvas.translate(bitmapRect2.left / scaleX, bitmapRect2.top / scaleY)
            applyBitmapAnimations2(this, bitmap2, animationProgress2)
            bitmap2.draw(canvas)
        }
    }
}