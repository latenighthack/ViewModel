package com.latenighthack.viewmodel.views

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlin.math.min

public class InsetLayoutParams(width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT) : ViewGroup.MarginLayoutParams(width, height) {
    init {
        topMargin = 24.dip
        leftMargin = 48.dip
        rightMargin = 48.dip
    }
}

public class CenterHorizontalLayoutParams(width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT) : LinearLayout.LayoutParams(width, height) {
    init {
        gravity = Gravity.CENTER_HORIZONTAL
    }
}

public fun <T : View> T.addToFill(viewGroup: ViewGroup, withMargins: Array<Int> = emptyArray()): T =
    addTo(viewGroup, ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT), withMargins)

public fun <T : View> T.addToFillVertically(viewGroup: ViewGroup, withMargins: Array<Int> = emptyArray()): T =
    addTo(viewGroup, ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT), withMargins)

public fun <T : View> T.addToFillHorizontally(viewGroup: ViewGroup, withMargins: Array<Int> = emptyArray()): T =
    addTo(viewGroup, ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT), withMargins)

public fun <T : View> T.addToInset(viewGroup: ViewGroup): T =
    addTo(viewGroup, InsetLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT))

public val View.activity: Activity?
    get() {
        var context = context

        while (context != null) {
            context = when (context) {
                is Activity -> return context
                is ContextWrapper -> context.baseContext
                else -> return null
            }
        }

        return null
    }

public fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
}

public fun Activity.showKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

public fun <T : View> T.addTo(viewGroup: ViewGroup, layoutParams: ViewGroup.LayoutParams? = null, withMargins: Array<Int> = emptyArray(), gravity: Int = Gravity.NO_GRAVITY, withBottomMargin: Int = 0, withTopMargin: Int = 0): T {
    if (layoutParams != null) {
        viewGroup.addView(this, layoutParams.also {
            if (gravity != Gravity.NO_GRAVITY) {
                when (layoutParams) {
                    is FrameLayout.LayoutParams -> layoutParams.gravity = gravity
                    is LinearLayout.LayoutParams -> layoutParams.gravity = gravity
                }
            }

            when (withMargins.size) {
                4 -> {
                    (layoutParams as ViewGroup.MarginLayoutParams).also {
                        it.topMargin = withMargins[0]
                        it.leftMargin = withMargins[1]
                        it.bottomMargin = withMargins[2]
                        it.rightMargin = withMargins[3]
                    }
                }
                2 -> {
                    (layoutParams as ViewGroup.MarginLayoutParams).also {
                        it.topMargin = withMargins[0]
                        it.leftMargin = withMargins[1]
                        it.bottomMargin = withMargins[0]
                        it.rightMargin = withMargins[1]
                    }
                }
                1 -> {
                    (layoutParams as ViewGroup.MarginLayoutParams).also {
                        it.topMargin = withMargins[0]
                        it.leftMargin = withMargins[0]
                        it.bottomMargin = withMargins[0]
                        it.rightMargin = withMargins[0]
                    }
                }
                else ->
                    (layoutParams as ViewGroup.MarginLayoutParams).also {
                        it.topMargin = withTopMargin
                        it.bottomMargin = withBottomMargin
                    }
            }
        })
    } else if (withMargins.isNotEmpty() || gravity != Gravity.NO_GRAVITY || withBottomMargin != 0 || withTopMargin == 0) {
        val params = when (viewGroup) {
            is LinearLayout -> LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            is FrameLayout -> LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            else -> ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }
        addTo(viewGroup, params, withMargins, gravity, withBottomMargin, withTopMargin)
    } else {
        viewGroup.addView(this)
    }

    return this
}

public fun measureSpecSubtract(measureSpec: Int, amount: Int): Int {
    return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec) - amount, View.MeasureSpec.getMode(measureSpec))
}

@Suppress("DEPRECATION")
public val Context.displaySize: Point get() {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()

    windowManager.defaultDisplay.getSize(point)

    return point
}

public val Context.isLandscape: Boolean get() {
    val displaySize = displaySize

    return displaySize.x > displaySize.y
}

public val Context.realDisplaySize: Point get() {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()

    windowManager.defaultDisplay.getRealSize(point)

    return point
}

public val View.selectableItemBackground: Int get() {
    val outValue = TypedValue()

    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)

    return outValue.resourceId
}

public val View.selectableItemBackgroundBorderless: Int get() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        return selectableItemBackground
    }

    val outValue = TypedValue()

    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)

    return outValue.resourceId
}

internal var dipConversionRate: Float = 0f
internal var sipConversionRate: Float = 0f

public fun Context.setupViewExtensions() {
    if (dipConversionRate == 0f) {
        dipConversionRate = resources.displayMetrics.density + 0.5f
    }
    if (sipConversionRate == 0f) {
        sipConversionRate = resources.displayMetrics.scaledDensity
    }
}

public fun View.showHide(value: Boolean) {
    if (value) show() else hide()
}

public var View.isHidden: Boolean
    get() {
        return visibility != View.VISIBLE
    }

    set(value) {
        if (value) {
            visibility = View.INVISIBLE
        } else {
            visibility = View.VISIBLE
        }
    }

public fun View.show() {
    visibility = View.VISIBLE
}

public fun View.hide() {
    visibility = View.GONE
}

public fun View.fadeOut(fadeDuration: Long = 300, callback: () -> Unit = { }) {
    animation = AlphaAnimation(1f, 0f).apply {
        interpolator = AccelerateInterpolator()
        duration = fadeDuration

        setAnimationListener(object : Animation.AnimationListener {
            public override fun onAnimationEnd(animation: Animation) {
                callback()
            }

            public override fun onAnimationRepeat(animation: Animation) {
            }

            public override fun onAnimationStart(animation: Animation) {
            }
        })
    }
}

public fun View.fadeOut2(fadeDuration: Long = 300, callback: () -> Unit = { }) {
    animation = AlphaAnimation(1f, 0f).apply {
        interpolator = AccelerateInterpolator()
        duration = fadeDuration

        setAnimationListener(object : Animation.AnimationListener {
            public override fun onAnimationEnd(animation: Animation) {
                callback()
            }

            public override fun onAnimationRepeat(animation: Animation) {
            }

            public override fun onAnimationStart(animation: Animation) {
            }
        })
    }
}

public fun View.fadeIn(fadeDuration: Long = 300, callback: () -> Unit = { }) {
    animation = AlphaAnimation(0f, 1f).apply {
        interpolator = DecelerateInterpolator()
        duration = fadeDuration

        setAnimationListener(object : Animation.AnimationListener {
            public override fun onAnimationEnd(animation: Animation) {
                callback()
            }

            public override fun onAnimationRepeat(animation: Animation) {
            }

            public override fun onAnimationStart(animation: Animation) {
            }
        })
    }
}

public val Int.dip: Int
    get() {
        return (this * dipConversionRate).toInt()
    }

public val Float.dip: Float
    get() {
        return this * dipConversionRate
    }

public val Int.sip: Int
    get() {
        return (this * sipConversionRate).toInt()
    }

public val Float.sip: Float
    get() {
        return this * sipConversionRate
    }

public object MeasureSpecTools {
    public fun makeMaxWidthMeasureSpec(widthMeasureSpec: Int): Int {
        val availableWidth = if (View.MeasureSpec.getSize(widthMeasureSpec) <= 0) {
            Resources.getSystem().displayMetrics.widthPixels
        } else {
            View.MeasureSpec.getSize(widthMeasureSpec)
        }

        return View.MeasureSpec.makeMeasureSpec(availableWidth, View.MeasureSpec.EXACTLY)
    }

    public fun measureSpecSubtract(measureSpec: Int, amount: Int): Int {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec) - amount, View.MeasureSpec.getMode(measureSpec))
    }

    public fun exactly(size: Int): Int = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
    public fun atMost(size: Int): Int = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.AT_MOST)
}

public fun Activity.localize(@StringRes string: Int, vararg params: Any): String {
    return resources.getString(string, params)
}

public fun View.localize(@StringRes string: Int, vararg params: Any): String {
    return resources.getString(string, params)
}

public fun View.exactlyFullWidthMeasureSpec(widthMeasureSpec: Int): Int {
    val width = View.MeasureSpec.getSize(widthMeasureSpec)
    val measuredWidth = if (width > 0) {
        width
    } else {
        context.displaySize.x
    }

    return View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY)
}

public fun View.atMostFullWidthMeasureSpec(widthMeasureSpec: Int): Int {
    val width = View.MeasureSpec.getSize(widthMeasureSpec)
    val measuredWidth = if (width > 0) {
        width
    } else {
        context.displaySize.x
    }

    return View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.AT_MOST)
}

public fun View.exactlyFullHeightMeasureSpec(heightMeasureSpec: Int): Int {
    val height = View.MeasureSpec.getSize(heightMeasureSpec)
    val measuredHeight = if (height > 0) {
        height
    } else {
        context.displaySize.y
    }

    return View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY)
}

enum public class LayoutType {
    DEFAULT,
    FILL,
    FILL_HORIZONTALLY,
    FILL_VERTICALLY,
    CENTER,
    TOP,
    BOTTOM,
    CENTER_HORIZONTALLY,
    CENTER_VERTICALLY,
    INSET
}

public fun <ViewType : View> ViewType.addTo(
    group: ViewGroup,
    params: ViewGroup.LayoutParams
): ViewType = also {
    group.addView(it, params)
}

public fun <ViewType : View> ViewType.addTo(
    group: ViewGroup,
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT
): ViewType = addTo(group,
    if (layout == LayoutType.DEFAULT) {
        when (group) {
            is FrameLayout -> FrameLayout.LayoutParams(width, height)
            is LinearLayout -> LinearLayout.LayoutParams(width, height)
            is RelativeLayout -> RelativeLayout.LayoutParams(width, height)
            else -> ViewGroup.MarginLayoutParams(width, height)
        }.apply {
            setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
        }
    } else {
        if (layout == LayoutType.INSET) {
            ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT)
                .apply {
                    setMargins(16.dip, 8.dip, 16.dip, bottomMargin)
                }
        } else {
            when (layout) {
                LayoutType.FILL -> when (group) {
                    is LinearLayout -> LinearLayout.LayoutParams(0, 0, 1f)
                    else -> ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                LayoutType.FILL_HORIZONTALLY -> when (group) {
                    is LinearLayout -> LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    else -> ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT)
                }
                LayoutType.FILL_VERTICALLY -> when (group) {
                    is LinearLayout -> LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1f)
                    else -> ViewGroup.MarginLayoutParams(WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
                LayoutType.CENTER -> when (group) {
                    is FrameLayout -> FrameLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER }
                    is LinearLayout -> LinearLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER }
                    else -> TODO()
                }
                LayoutType.CENTER_HORIZONTALLY -> when (group) {
                    is FrameLayout -> FrameLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER_HORIZONTAL }
                    is LinearLayout -> LinearLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER_HORIZONTAL }
                    else -> TODO()
                }
                LayoutType.BOTTOM -> when (group) {
                    is FrameLayout -> FrameLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM }
                    is LinearLayout -> LinearLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM }
                    else -> TODO()
                }
                LayoutType.TOP -> when (group) {
                    is FrameLayout -> FrameLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP }
                    is LinearLayout -> LinearLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP }
                    else -> TODO()
                }
                LayoutType.CENTER_VERTICALLY -> when (group) {
                    is FrameLayout -> FrameLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER_VERTICAL }
                    is LinearLayout -> LinearLayout.LayoutParams(width, height).also { it.gravity = Gravity.CENTER_VERTICAL }
                    else -> TODO()
                }
                else -> ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            }.apply {
                setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
            }
        }
    }
)

inline public fun <reified TView: View> ViewGroup.createAndAttach(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    noinline builder: TView.() -> Unit
): TView = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, {
    val constructors = TView::class.constructors

    constructors.first { it.parameters.size == 1 }.call(context)
}, builder)

public fun <TView: View> ViewGroup.createAndAttach(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    create: (Context) -> TView,
    builder: TView.() -> Unit
): TView {
    val instance = create(this.context)

    instance.apply(builder)
    instance.addTo(this, width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout)

    return instance
}

public fun <TView: View> View.replaceWith(newView: TView) : TView {
    val parent = this.parent as ViewGroup
    val index = parent.indexOfChild(this)

    newView.layoutParams = this.layoutParams

    parent.addView(newView, index)
    parent.removeViewAt(index + 1)

    return newView
}

public fun Activity.VerticalLayout(builder: LinearLayout.() -> Unit): ViewGroup = LinearLayout(this)
    .apply(builder)
    .apply { orientation = LinearLayout.VERTICAL }

public fun Activity.FrameLayout(builder: FrameLayout.() -> Unit): FrameLayout = FrameLayout(this)
    .apply(builder)

public fun Activity.HorizontalLayout(builder: LinearLayout.() -> Unit): LinearLayout = LinearLayout(this)
    .apply(builder)
    .apply { orientation = LinearLayout.HORIZONTAL }

public fun ViewGroup.Spacer(
    width: Int = 0,
    height: Int = 0
): Spacer {
    val spacer = Spacer(context, width, height)

    addView(spacer)

    return spacer
}

public fun ViewGroup.VerticalLayout(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: LinearLayout.() -> Unit
): LinearLayout = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder)
    .apply { orientation = LinearLayout.VERTICAL }

public fun ViewGroup.TextView(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    text: String = "",
    builder: TextView.() -> Unit = {}
): TextView = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder)
    .also { it.text = text }

public fun ViewGroup.Button(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    text: String = "",
    builder: Button.() -> Unit = {}
): Button = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder)
    .also { it.text = text }

public fun ViewGroup.GridLayout(
    columns: Int,
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: GridLayout.() -> Unit
): GridLayout = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder).apply {
    columnCount = columns
}

public fun ViewGroup.HorizontalLayout(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: LinearLayout.() -> Unit
): LinearLayout = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder)
    .apply { orientation = LinearLayout.HORIZONTAL }

public fun ViewGroup.LinearLayout(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: LinearLayout.() -> Unit
): LinearLayout = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder)

public fun ViewGroup.ImageView(
    @DrawableRes resource: Int = 0,
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: ImageView.() -> Unit = {}
): ImageView = ImageView(resources.getDrawable(resource), width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder)

public fun ViewGroup.ImageView(
    drawable: Drawable? = null,
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: ImageView.() -> Unit = {}
): ImageView = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder).apply {
    setImageDrawable(drawable)
}

public fun ViewGroup.FrameLayout(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: FrameLayout.() -> Unit = {}
): FrameLayout = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder)

public fun ViewGroup.SquareLayout(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: SquareLayout.() -> Unit
): SquareLayout = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder)

public class SquareLayout(context: Context) : FrameLayout(context) {
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val dimension = min(measuredWidth, measuredHeight)

        setMeasuredDimension(dimension, dimension)
    }
}

public fun <TView : View> TView.removeFromParent(): TView {
    (parent as? ViewGroup)?.removeView(this)

    return this
}

public fun ViewPager.addAdapterWithViewCreators(vararg viewCreators: () -> View): ViewPager {
    val viewCache = MutableList<View?>(viewCreators.size) { null }

    adapter = object : PagerAdapter() {
        public override fun isViewFromObject(view: View, obj: Any): Boolean {
            return obj == view
        }

        public override fun getCount(): Int = viewCreators.size

        public override fun instantiateItem(container: ViewGroup, position: Int): Any {
            if (position >= viewCreators.size) {
                TODO("no more pages wtf")
            }

            val view = if (position >= viewCache.size || viewCache[position] == null) {
                val viewCreator = viewCreators[position]
                val createdView = viewCreator()

                viewCache.set(position, createdView)

                createdView
            } else {
                viewCache[position]!!
            }

            view.removeFromParent()
            container.addView(view)

            return view
        }

        public override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    return this
}

public fun ViewPager.addAdapterWithViews(vararg views: View): ViewPager {
    adapter = object : PagerAdapter() {
        public override fun isViewFromObject(view: View, obj: Any): Boolean {
            return obj == view
        }

        public override fun getCount(): Int = views.size

        public override fun instantiateItem(container: ViewGroup, position: Int): Any {
            if (position >= views.size) {
                TODO("no more pages wtf")
            }

            val view = views[position]

            view.removeFromParent()
            container.addView(view)

            return view
        }

        public override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    return this
}

public fun ViewGroup.VerticalRecyclerView(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: RecyclerView.() -> Unit = {}
): RecyclerView = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder).apply {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
}

public fun ViewGroup.RecyclerView(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: RecyclerView.() -> Unit = {}
): RecyclerView = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder).apply {
}

public fun ViewGroup.HorizontalRecyclerView(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0,
    layout: LayoutType = LayoutType.DEFAULT,
    builder: RecyclerView.() -> Unit = {}
): RecyclerView = createAndAttach(width, height, topMargin, leftMargin, rightMargin, bottomMargin, layout, builder).apply {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}
