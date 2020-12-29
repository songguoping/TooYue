package com.coderfeng.tooyue.home

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager


internal class GridDividerItemDecoration(context: Context) :
    ItemDecoration() {
    private val attrs = intArrayOf(R.attr.listDivider)
    private val mDividerDrawable: Drawable?
    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: State
    ) {
        super.onDraw(c, parent, state)
        drawVertical(c, parent)
        drawHorizontal(c, parent)
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params =
                child.layoutParams as LayoutParams
            val left = child.left - params.leftMargin
            var top = child.bottom + params.bottomMargin
            val right = child.right + params.rightMargin
            var bottom = top + mDividerDrawable!!.intrinsicHeight
            //画下面的线
            mDividerDrawable.setBounds(left, top, right, bottom)
            mDividerDrawable.draw(c)

            //如果是第一行 那么画上面的线
            if (isFirstRow(parent, i)) {
                top = child.top - params.topMargin
                bottom = top + mDividerDrawable.intrinsicHeight
                mDividerDrawable.setBounds(left, top, right, bottom)
                mDividerDrawable.draw(c)
            }
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params =
                child.layoutParams as LayoutParams
            var left = child.right + params.rightMargin
            val top = child.top - params.topMargin
            var right = left + mDividerDrawable!!.intrinsicWidth
            val bottom = child.bottom + params.bottomMargin

            //画右边的线
            mDividerDrawable.setBounds(left, top, right, bottom)
            mDividerDrawable.draw(c)

            //如果是第一列 画左边的线
            if (isFirstColumn(parent, i)) {
                left = child.left - params.leftMargin
                right = left + mDividerDrawable.intrinsicWidth
                mDividerDrawable.setBounds(left, top, right, bottom)
                mDividerDrawable.draw(c)
            }
        }
    }

    private fun isFirstColumn(parent: RecyclerView, position: Int): Boolean {
        val spanCount = getSpanCount(parent)
        return position % spanCount == 0
    }

    /*是否是第一行*/
    private fun isFirstRow(parent: RecyclerView, i: Int): Boolean {
        val spanCount = getSpanCount(parent)
        return i <= spanCount - 1
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        val layoutManager = parent.layoutManager
        if (layoutManager is StaggeredGridLayoutManager) {
            return layoutManager.spanCount
        } else if (layoutManager is GridLayoutManager) {
            return layoutManager.spanCount
        }
        return 0
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect[mDividerDrawable!!.intrinsicWidth, mDividerDrawable.intrinsicHeight, mDividerDrawable.intrinsicWidth] =
            mDividerDrawable.intrinsicHeight
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs)
        mDividerDrawable = typedArray.getDrawable(0)
        typedArray.recycle()
    }
}