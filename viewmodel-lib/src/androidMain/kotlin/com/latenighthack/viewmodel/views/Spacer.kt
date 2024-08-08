package com.latenighthack.viewmodel.views

import android.content.Context
import android.view.View

public class Spacer @JvmOverloads constructor(
    context: Context,
    public val exactWidth: Int,
    public val exactHeight: Int
) : View(context, null, 0) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(exactWidth, exactHeight)
    }
}
