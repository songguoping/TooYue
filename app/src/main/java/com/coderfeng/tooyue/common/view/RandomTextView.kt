package com.coderfeng.tooyue.common.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.coderfeng.tooyue.R


class RandomTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mHeight: Int = 0
    private var mWidth: Int = 0;
    private val TAG: String = "TAG_SGP"
    private var beforeText: CharArray = charArrayOf()
    private var currText: CharArray = charArrayOf()
    private var mAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var startY: Float = 0f
    private val mPaint: Paint = Paint()
    private val metrics: Paint.FontMetrics
    private var baseline: Float = 0f
    private var tWidth: Float = 0f
    private var startWidth: Float = 0f
    var currentValue: String? = ""

    init {
        mPaint.textSize = resources.getDimension(R.dimen.sp_30)
        metrics = mPaint.fontMetrics
        tWidth = mPaint.measureText("a")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //宽度的模式
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        //宽度大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        //如果明确大小,直接设置大小
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize
        } else {
            //计算宽度,可以根据实际情况进行计算
            mWidth = paddingLeft + paddingRight
            //如果为AT_MOST, 不允许超过默认宽度的大小
            if (widthMode == MeasureSpec.AT_MOST) {
                mWidth = mWidth.coerceAtMost(widthSize)
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize
        } else {
            mHeight = paddingTop + paddingBottom
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = mHeight.coerceAtMost(heightSize)
            }
        }

        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        baseline = height / 2 + (metrics.bottom - metrics.top) / 2 - metrics.bottom
        mAnimator.setFloatValues(0f, baseline)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        currText = this.currentValue?.toCharArray()!!
        drawNumber(canvas)
        //Log.d(TAG, "currText:$currentValue before:$String(beforeText)")
    }

    private fun drawNumber(canvas: Canvas?) {
        startWidth = width / 2 - mPaint.measureText(currentValue) / 2

        if (currText.size >= beforeText.size) {
            val num = currText.size - beforeText.size
            for (i in currText.indices) {
                if (i < num) continue
                if (beforeText[i - num] == currText[i]) {
                    canvas?.drawText(
                        beforeText[i - num].toString(), startWidth + i * tWidth, baseline, mPaint
                    )
                } else {
                    canvas?.drawText(
                        beforeText[i - num].toString(),
                        startWidth + i * tWidth,
                        baseline + startY,
                        mPaint
                    )
                    canvas?.drawText(
                        currText[i].toString(), startWidth + i * tWidth, startY, mPaint
                    )
                }
            }
        } else {

        }
    }

    fun setCurrValue(value: String) {
        currentValue = value
    }

    fun startAnimation() {
        mAnimator.duration = 500
        mAnimator.addUpdateListener(AnimatorUpdateListener { animation ->
            startY = animation.animatedValue as Float
            postInvalidate()
        })
        mAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                beforeText = currText
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        mAnimator.start()
    }

    fun stopAnimation() {
        if (mAnimator.isRunning) {
            mAnimator.end()
        }
    }
}