package com.coderfeng.tooyue.common.view

import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.coderfeng.tooyue.R
import com.coderfeng.tooyue.R.styleable.MiClockView
import java.lang.Math.pow
import java.util.*
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin


class ClockView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /* 小时文本画笔 */
    private var mTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /* 测量小时文本宽高的矩形 */
    private var mTextRect: Rect = Rect()

    /* 小时圆圈画笔 */
    private var mCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /* 小时圆圈线条宽度 */
    private val mCircleStrokeWidth = 2f

    /* 小时圆圈的外接矩形 */
    private var mCircleRectF: RectF = RectF()

    /* 刻度圆弧画笔 */
    private var mScaleArcPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /* 刻度圆弧的外接矩形 */
    private var mScaleArcRectF: RectF = RectF()

    /* 刻度线画笔 */
    private var mScaleLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /* 时针画笔 */
    private var mHourHandPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /* 分针画笔 */
    private var mMinuteHandPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /* 秒针画笔 */
    private var mSecondHandPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /* 时针路径 */
    private var mHourHandPath: Path = Path()

    /* 分针路径 */
    private var mMinuteHandPath: Path = Path()

    /* 秒针路径 */
    private var mSecondHandPath: Path = Path()

    /* 亮色，用于分针、秒针、渐变终止色 */
    private var mLightColor = 0

    /* 暗色，圆弧、刻度线、时针、渐变起始色 */
    private var mDarkColor = 0

    /* 背景色 */
    private var mBackgroundColor = 0

    /* 小时文本字体大小 */
    private var mTextSize = 0f

    /* 时钟半径，不包括padding值 */
    private var mRadius = 0f

    /* 刻度线长度 */
    private var mScaleLength = 0f

    /* 时针角度 */
    private var mHourDegree = 0f

    /* 分针角度 */
    private var mMinuteDegree = 0f

    /* 秒针角度 */
    private var mSecondDegree = 0f

    /* 加一个默认的padding值，为了防止用camera旋转时钟时造成四周超出view大小 */
    private var mDefaultPadding = 0f
    private var mPaddingLeft = 0f
    private var mPaddingTop = 0f
    private var mPaddingRight = 0f
    private var mPaddingBottom = 0f

    /* 梯度扫描渐变 */
    private var mSweepGradient: SweepGradient? = null

    /* 渐变矩阵，作用在SweepGradient */
    private var mGradientMatrix: Matrix = Matrix()

    /* 触摸时作用在Camera的矩阵 */
    private var mCameraMatrix: Matrix = Matrix()

    /* 照相机，用于旋转时钟实现3D效果 */
    private var mCamera: Camera = Camera()

    /* camera绕X轴旋转的角度 */
    private var mCameraRotateX = 0f

    /* camera绕Y轴旋转的角度 */
    private var mCameraRotateY = 0f

    /* camera旋转的最大角度 */
    private val mMaxCameraRotate = 10f

    /* 指针的在x轴的位移 */
    private var mCanvasTranslateX = 0f

    /* 指针的在y轴的位移 */
    private var mCanvasTranslateY = 0f

    /* 指针的最大位移 */
    private var mMaxCanvasTranslate = 0f

    /* 手指松开时时钟晃动的动画 */
    private var mShakeAnim: ValueAnimator? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, MiClockView, defStyleAttr, 0)
        mBackgroundColor = ta.getColor(R.styleable.MiClockView_backgroundColor, Color.parseColor("#237EAD"))
        setBackgroundColor(mBackgroundColor)
        mLightColor = ta.getColor(R.styleable.MiClockView_lightColor, Color.parseColor("#ffffff"))
        mDarkColor = ta.getColor(R.styleable.MiClockView_darkColor, Color.parseColor("#80ffffff"))
        mTextSize = ta.getDimension(R.styleable.MiClockView_textSize, sp2px(context, 14f).toFloat())
        ta.recycle()
        mHourHandPaint.style = Paint.Style.FILL
        mHourHandPaint.color = mDarkColor
        mMinuteHandPaint.color = mLightColor
        mSecondHandPaint.style = Paint.Style.FILL
        mSecondHandPaint.color = mLightColor
        mScaleLinePaint.style = Paint.Style.STROKE
        mScaleLinePaint.color = mBackgroundColor
        mScaleArcPaint.style = Paint.Style.STROKE
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.color = mDarkColor
        mTextPaint.textSize = mTextSize
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.strokeWidth = mCircleStrokeWidth
        mCirclePaint.color = mDarkColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec))
    }

    private fun measureDimension(measureSpec: Int): Int {
        var result: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = 800
            if (mode == MeasureSpec.AT_MOST) {
                result = min(result, size)
            }
        }
        return result
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //宽和高分别去掉padding值，取min的一半即表盘的半径
        mRadius = min(w - paddingLeft - paddingRight,
                h - paddingTop - paddingBottom) / 2.toFloat()
        mDefaultPadding = 0.12f * mRadius //根据比例确定默认padding大小
        mPaddingLeft = mDefaultPadding + w / 2 - mRadius + paddingLeft
        mPaddingTop = mDefaultPadding + h / 2 - mRadius + paddingTop
        mPaddingRight = mPaddingLeft
        mPaddingBottom = mPaddingTop
        mScaleLength = 0.12f * mRadius //根据比例确定刻度线长度
        mScaleArcPaint.strokeWidth = mScaleLength
        mScaleLinePaint.strokeWidth = 0.012f * mRadius
        mMaxCanvasTranslate = 0.02f * mRadius
        //梯度扫描渐变，以(w/2,h/2)为中心点，两种起止颜色梯度渐变
        //float数组表示，[0,0.75)为起始颜色所占比例，[0.75,1}为起止颜色渐变所占比例
        mSweepGradient = SweepGradient(w / 2f, h / 2f, intArrayOf(mDarkColor, mLightColor), floatArrayOf(0.75f, 1f))
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            setCameraRotate(it)
            getTimeDegree()
            drawTimeText(it)
            drawScaleLine(it)
            drawSecondHand(it)
            drawHourHand(it)
            drawMinuteHand(it)
        }

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mShakeAnim != null && mShakeAnim!!.isRunning) {
                    mShakeAnim!!.cancel()
                }
                getCameraRotate(event)
                getCanvasTranslate(event)
            }
            MotionEvent.ACTION_MOVE -> {
                //根据手指坐标计算camera应该旋转的大小
                getCameraRotate(event)
                getCanvasTranslate(event)
            }
            MotionEvent.ACTION_UP ->                 //松开手指，时钟复原并伴随晃动动画
                startShakeAnim()
        }
        return true
    }

    /**
     * 获取camera旋转的大小
     *
     * @param event motionEvent
     */
    private fun getCameraRotate(event: MotionEvent) {
        val rotateX = -(event.y - height / 2)
        val rotateY = event.x - width / 2
        //求出此时旋转的大小与半径之比
        val percentArr = getPercent(rotateX, rotateY)
        //最终旋转的大小按比例匀称改变
        mCameraRotateX = percentArr[0] * mMaxCameraRotate
        mCameraRotateY = percentArr[1] * mMaxCameraRotate
    }

    /**
     * 当拨动时钟时，会发现时针、分针、秒针和刻度盘会有一个较小的偏移量，形成近大远小的立体偏移效果
     * 一开始我打算使用 matrix 和 camera 的 mCamera.translate(x, y, z) 方法改变 z 的值
     * 但是并没有效果，所以就动态计算距离，然后在 onDraw()中分零件地 mCanvas.translate(x, y)
     *
     * @param event motionEvent
     */
    private fun getCanvasTranslate(event: MotionEvent) {
        val translateX = event.x - width / 2
        val translateY = event.y - height / 2
        //求出此时位移的大小与半径之比
        val percentArr = getPercent(translateX, translateY)
        //最终位移的大小按比例匀称改变
        mCanvasTranslateX = percentArr[0] * mMaxCanvasTranslate
        mCanvasTranslateY = percentArr[1] * mMaxCanvasTranslate
    }

    /**
     * 获取一个操作旋转或位移大小的比例
     *
     * @param x x大小
     * @param y y大小
     * @return 装有xy比例的float数组
     */
    private fun getPercent(x: Float, y: Float): FloatArray {
        val percentArr = FloatArray(2)
        var percentX = x / mRadius
        var percentY = y / mRadius
        if (percentX > 1) {
            percentX = 1f
        } else if (percentX < -1) {
            percentX = -1f
        }
        if (percentY > 1) {
            percentY = 1f
        } else if (percentY < -1) {
            percentY = -1f
        }
        percentArr[0] = percentX
        percentArr[1] = percentY
        return percentArr
    }

    /**
     * 设置3D时钟效果，触摸矩阵的相关设置、照相机的旋转大小
     * 应用在绘制图形之前，否则无效
     */
    private fun setCameraRotate(canvas: Canvas) {
        mCameraMatrix.reset()
        mCamera.save()
        mCamera.rotateX(mCameraRotateX) //绕x轴旋转角度
        mCamera.rotateY(mCameraRotateY) //绕y轴旋转角度
        mCamera.getMatrix(mCameraMatrix) //相关属性设置到matrix中
        mCamera.restore()
        //camera在view左上角那个点，故旋转默认是以左上角为中心旋转
        //故在动作之前pre将matrix向左移动getWidth()/2长度，向上移动getHeight()/2长度
        mCameraMatrix.preTranslate(-width / 2f, -height / 2f)
        //在动作之后post再回到原位
        mCameraMatrix.postTranslate(width / 2f, height / 2f)
        canvas.concat(mCameraMatrix) //matrix与canvas相关联
    }

    /**
     * 时钟晃动动画
     */
    private fun startShakeAnim() {
        val cameraRotateXName = "cameraRotateX"
        val cameraRotateYName = "cameraRotateY"
        val canvasTranslateXName = "canvasTranslateX"
        val canvasTranslateYName = "canvasTranslateY"
        val cameraRotateXHolder = PropertyValuesHolder.ofFloat(cameraRotateXName, mCameraRotateX, 0f)
        val cameraRotateYHolder = PropertyValuesHolder.ofFloat(cameraRotateYName, mCameraRotateY, 0f)
        val canvasTranslateXHolder = PropertyValuesHolder.ofFloat(canvasTranslateXName, mCanvasTranslateX, 0f)
        val canvasTranslateYHolder = PropertyValuesHolder.ofFloat(canvasTranslateYName, mCanvasTranslateY, 0f)
        mShakeAnim = ValueAnimator.ofPropertyValuesHolder(cameraRotateXHolder,
                cameraRotateYHolder, canvasTranslateXHolder, canvasTranslateYHolder)
        mShakeAnim?.interpolator = TimeInterpolator { input -> //http://inloop.github.io/interpolator/
            val f = 0.571429f
            (2.0.pow(-2 * input.toDouble()) * sin((input - f / 4) * (2 * Math.PI) / f) + 1).toFloat()
        }
        mShakeAnim?.duration = 1000
        mShakeAnim?.addUpdateListener(AnimatorUpdateListener { animation ->
            mCameraRotateX = animation.getAnimatedValue(cameraRotateXName) as Float
            mCameraRotateY = animation.getAnimatedValue(cameraRotateYName) as Float
            mCanvasTranslateX = animation.getAnimatedValue(canvasTranslateXName) as Float
            mCanvasTranslateY = animation.getAnimatedValue(canvasTranslateYName) as Float
        })
        mShakeAnim?.start()
    }

    /**
     * 获取当前时分秒所对应的角度
     * 为了不让秒针走得像老式挂钟一样僵硬，需要精确到毫秒
     */
    private fun getTimeDegree() {
        val calendar: Calendar = Calendar.getInstance()
        val milliSecond: Int = calendar.get(Calendar.MILLISECOND)
        val second: Float = calendar.get(Calendar.SECOND) + milliSecond / 1000f
        val minute: Float = calendar.get(Calendar.MINUTE) + second / 60
        val hour: Float = calendar.get(Calendar.HOUR) + minute / 60
        mSecondDegree = second / 60 * 360
        mMinuteDegree = minute / 60 * 360
        mHourDegree = hour / 12 * 360
    }

    /**
     * 画最外圈的时间文本和4个弧线
     */
    private fun drawTimeText(canvas: Canvas) {
        var timeText = "12"
        mTextPaint.getTextBounds(timeText, 0, timeText.length, mTextRect)
        val textLargeWidth: Int = mTextRect.width() //两位数字的宽
        canvas.drawText("12", (width / 2 - textLargeWidth / 2).toFloat(), mPaddingTop + mTextRect.height(), mTextPaint)
        timeText = "3"
        mTextPaint.getTextBounds(timeText, 0, timeText.length, mTextRect)
        val textSmallWidth: Int = mTextRect.width() //一位数字的宽
        canvas.drawText("3", width - mPaddingRight - mTextRect.height() / 2 - textSmallWidth / 2,
                (height / 2 + mTextRect.height() / 2).toFloat(), mTextPaint)
        canvas.drawText("6", (width / 2 - textSmallWidth / 2).toFloat(), height - mPaddingBottom, mTextPaint)
        canvas.drawText("9", mPaddingLeft + mTextRect.height() / 2 - textSmallWidth / 2,
                (height / 2 + mTextRect.height() / 2).toFloat(), mTextPaint)

        //画4个弧
        mCircleRectF[mPaddingLeft + mTextRect.height() / 2 + mCircleStrokeWidth / 2, mPaddingTop + mTextRect.height() / 2 + mCircleStrokeWidth / 2, width - mPaddingRight - mTextRect.height() / 2 + mCircleStrokeWidth / 2] = height - mPaddingBottom - mTextRect.height() / 2 + mCircleStrokeWidth / 2
        for (i in 0..3) {
            canvas.drawArc(mCircleRectF, (5 + 90 * i).toFloat(), 80f, false, mCirclePaint)
        }
    }

    /**
     * 画一圈梯度渲染的亮暗色渐变圆弧，重绘时不断旋转，上面盖一圈背景色的刻度线
     */
    private fun drawScaleLine(canvas: Canvas) {
        canvas.save()
        canvas.translate(mCanvasTranslateX, mCanvasTranslateY)
        mScaleArcRectF[mPaddingLeft + 1.5f * mScaleLength + mTextRect.height() / 2, mPaddingTop + 1.5f * mScaleLength + mTextRect.height() / 2, width - mPaddingRight - mTextRect.height() / 2 - 1.5f * mScaleLength] = height - mPaddingBottom - mTextRect.height() / 2 - 1.5f * mScaleLength
        //matrix默认会在三点钟方向开始颜色的渐变，为了吻合钟表十二点钟顺时针旋转的方向，把秒针旋转的角度减去90度
        mGradientMatrix.setRotate(mSecondDegree - 90, width / 2f, height / 2f)
        mSweepGradient!!.setLocalMatrix(mGradientMatrix)
        mScaleArcPaint.shader = mSweepGradient
        canvas.drawArc(mScaleArcRectF, 0f, 360f, false, mScaleArcPaint)
        //画背景色刻度线
        for (i in 0..199) {
            canvas.drawLine(width / 2f, mPaddingTop + mScaleLength + mTextRect.height() / 2,
                    width / 2f, mPaddingTop + 2 * mScaleLength + mTextRect.height() / 2, mScaleLinePaint)
            canvas.rotate(1.8f, width / 2f, height / 2f)
        }
        canvas.restore()
    }

    /**
     * 画秒针，根据不断变化的秒针角度旋转画布
     */
    private fun drawSecondHand(canvas: Canvas) {
        canvas.save()
        canvas.translate(mCanvasTranslateX, mCanvasTranslateY)
        canvas.rotate(mSecondDegree, width / 2f, height / 2f)
        if (mSecondHandPath.isEmpty) {
            mSecondHandPath.reset()
            val offset: Float = mPaddingTop + mTextRect.height() / 2
            mSecondHandPath.moveTo(width / 2f, offset + 0.26f * mRadius)
            mSecondHandPath.lineTo(width / 2 - 0.05f * mRadius, offset + 0.34f * mRadius)
            mSecondHandPath.lineTo(width / 2 + 0.05f * mRadius, offset + 0.34f * mRadius)
            mSecondHandPath.close()
            mSecondHandPaint.color = mLightColor
        }
        canvas.drawPath(mSecondHandPath, mSecondHandPaint)
        canvas.restore()
    }

    /**
     * 画时针，根据不断变化的时针角度旋转画布
     * 针头为圆弧状，使用二阶贝塞尔曲线
     */
    private fun drawHourHand(canvas: Canvas) {
        canvas.save()
        canvas.translate(mCanvasTranslateX * 1.2f, mCanvasTranslateY * 1.2f)
        canvas.rotate(mHourDegree, width / 2f, height / 2f)
        if (mHourHandPath.isEmpty) {
            mHourHandPath.reset()
            val offset: Float = mPaddingTop + mTextRect.height() / 2
            mHourHandPath.moveTo(width / 2 - 0.018f * mRadius, height / 2 - 0.03f * mRadius)
            mHourHandPath.lineTo(width / 2 - 0.009f * mRadius, offset + 0.48f * mRadius)
            mHourHandPath.quadTo(width / 2f, offset + 0.46f * mRadius,
                    width / 2 + 0.009f * mRadius, offset + 0.48f * mRadius)
            mHourHandPath.lineTo(width / 2 + 0.018f * mRadius, height / 2 - 0.03f * mRadius)
            mHourHandPath.close()
        }
        mHourHandPaint.style = Paint.Style.FILL
        canvas.drawPath(mHourHandPath, mHourHandPaint)
        mCircleRectF[width / 2 - 0.03f * mRadius, height / 2 - 0.03f * mRadius, width / 2 + 0.03f * mRadius] = height / 2 + 0.03f * mRadius
        mHourHandPaint.style = Paint.Style.STROKE
        mHourHandPaint.strokeWidth = 0.01f * mRadius
        canvas.drawArc(mCircleRectF, 0f, 360f, false, mHourHandPaint)
        canvas.restore()
    }

    /**
     * 画分针，根据不断变化的分针角度旋转画布
     */
    private fun drawMinuteHand(canvas: Canvas) {
        canvas.save()
        canvas.translate(mCanvasTranslateX * 2f, mCanvasTranslateY * 2f)
        canvas.rotate(mMinuteDegree, width / 2f, height / 2f)
        if (mMinuteHandPath.isEmpty) {
            mMinuteHandPath.reset()
            val offset: Float = mPaddingTop + mTextRect.height() / 2
            mMinuteHandPath.moveTo(width / 2 - 0.01f * mRadius, height / 2 - 0.03f * mRadius)
            mMinuteHandPath.lineTo(width / 2 - 0.008f * mRadius, offset + 0.365f * mRadius)
            mMinuteHandPath.quadTo(width / 2f, offset + 0.345f * mRadius,
                    width / 2 + 0.008f * mRadius, offset + 0.365f * mRadius)
            mMinuteHandPath.lineTo(width / 2 + 0.01f * mRadius, height / 2 - 0.03f * mRadius)
            mMinuteHandPath.close()
        }
        mMinuteHandPaint.style = Paint.Style.FILL
        canvas.drawPath(mMinuteHandPath, mMinuteHandPaint)
        mCircleRectF[width / 2 - 0.03f * mRadius, height / 2 - 0.03f * mRadius, width / 2 + 0.03f * mRadius] = height / 2 + 0.03f * mRadius
        mMinuteHandPaint.style = Paint.Style.STROKE
        mMinuteHandPaint.strokeWidth = 0.02f * mRadius
        canvas.drawArc(mCircleRectF, 0f, 360f, false, mMinuteHandPaint)
        canvas.restore()
    }

    private fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.resources.displayMetrics).toInt()
    }
}