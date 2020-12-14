package ru.neikila.rxplayground

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.view.View

class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val width: Int = 2 * view.width / 3
        val height: Int = 2 * view.height / 3

        size.set(width, height)
        touch.set((width * 0.38).toInt(), (height * 0.38).toInt())
    }

    override fun onDrawShadow(canvas: Canvas) {
        val newBitmap = Bitmap.createScaledBitmap(
            loadBitmapFromView(view),
            2 * view.width / 3,
            2 * view.height / 3,
            true)
        val paint = Paint()
        paint.alpha = (255 * 0.6).toInt()
        canvas.drawBitmap(newBitmap, 0f, 0f, paint)
        newBitmap.recycle()
    }

    private fun loadBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }
}