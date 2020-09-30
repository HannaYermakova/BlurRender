package by.aermakova.blurrender

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import kotlin.math.roundToInt

object BlurBuilder {

    private const val BITMAP_SCALE = 0.6f

    fun blur(context: Context, image: Bitmap, blurRadius: Float): Bitmap {
        val width = (image.width * BITMAP_SCALE).roundToInt()
        val height = (image.height * BITMAP_SCALE).roundToInt()
        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val renderScript = RenderScript.create(context)
        val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap)

        with(intrinsicBlur) {
            setRadius(blurRadius)
            setInput(tmpIn)
            forEach(tmpOut)
            tmpOut.copyTo(outputBitmap)
        }
        return outputBitmap
    }
}