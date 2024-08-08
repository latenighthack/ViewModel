package com.latenighthack.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.latenighthack.viewmodel.list.Delta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

public interface RecyclerViewLayoutProvider {
    public fun matches(item: Any): Boolean

    public fun createView(context: Context): View

    public fun onBindView(view: View, item: Any)

    public fun onStateChanged(view: View, item: Any, state: Any)
}

public data class LayoutBuilderCallback<T>(var retained: T? = null)

public val TAG_RECYCLER_VIEW_BUILDERS_TEMP: Int = "RecyclerView.TAG_RECYCLER_VIEW_BUILDERS_TEMP".hashCode()

public class RecyclerViewAdapterBuilder(
    public val parent: RecyclerView
) {
    public inner class ViewModelLayoutBuilder<ViewType: View, ModelType: ViewModel<StateType>, StateType>(
        private val binder: LayoutBuilderCallback<ViewType.(ModelType) -> Unit>,
        private val stateUpdater: LayoutBuilderCallback<ViewType.(ModelType, StateType) -> Unit>
    ) {
        public fun onBindView(handler: ViewType.(ModelType) -> Unit) {
            binder.retained = handler
        }

        public fun onStateChanged(handler: ViewType.(ModelType, StateType) -> Unit) {
            stateUpdater.retained = handler
        }
    }

    public inner class LayoutBuilder<ViewType: View, ModelType>(
        private val binder: LayoutBuilderCallback<ViewType.(ModelType) -> Unit>,
    ) {
        public fun onBindView(handler: ViewType.(ModelType) -> Unit) {
            binder.retained = handler
        }
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <reified ViewType: View, reified ModelType> layoutModel(builder: LayoutBuilder<ViewType, ModelType>.() -> Unit) {
        val binder = LayoutBuilderCallback<ViewType.(ModelType) -> Unit>()
        val layoutBuilder = LayoutBuilder(binder)

        layoutBuilder.apply(builder)

        val layoutProvider = object : RecyclerViewLayoutProvider {
            override fun matches(item: Any): Boolean {
                return item is ModelType
            }

            override fun createView(context: Context): View {
                val constructors = ViewType::class.java.constructors

                return constructors.first().newInstance(context) as ViewType
            }

            override fun onBindView(view: View, item: Any) {
                val onBindView = binder.retained ?: { }

                (view as ViewType).onBindView(item as ModelType)
            }

            override fun onStateChanged(view: View, item: Any, state: Any) {
            }
        }

        val builders = parent.getTag(TAG_RECYCLER_VIEW_BUILDERS_TEMP) as MutableList<RecyclerViewLayoutProvider>

        builders.add(layoutProvider)
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <reified ViewType: View, reified ModelType: ViewModel<StateType>, StateType> layout(builder: ViewModelLayoutBuilder<ViewType, ModelType, StateType>.() -> Unit) {
        val binder = LayoutBuilderCallback<ViewType.(ModelType) -> Unit>()
        val stateUpdater = LayoutBuilderCallback<ViewType.(ModelType, StateType) -> Unit>()
        val layoutBuilder = ViewModelLayoutBuilder(binder, stateUpdater)

        layoutBuilder.apply(builder)

        val layoutProvider = object : RecyclerViewLayoutProvider {
            override fun matches(item: Any): Boolean {
                return item is ModelType
            }

            override fun createView(context: Context): View {
                return ViewType::class.java.constructors.first().newInstance(context) as ViewType
            }

            override fun onBindView(view: View, item: Any) {
                val onBindView = binder.retained ?: { }

                (view as ViewType).onBindView(item as ModelType)
            }

            override fun onStateChanged(view: View, item: Any, state: Any) {
                val onStateChanged = stateUpdater.retained ?: { _, _ -> }

                (view as ViewType).onStateChanged(item as ModelType, state as StateType)
            }
        }

        val builders = parent.getTag(TAG_RECYCLER_VIEW_BUILDERS_TEMP) as MutableList<RecyclerViewLayoutProvider>

        builders.add(layoutProvider)
    }
}

@SuppressLint("NotifyDataSetChanged")
public fun <ModelType> RecyclerView.items(bindingScope: CoroutineScope, items: Flow<Delta<ModelType>>, builder: RecyclerViewAdapterBuilder.() -> Unit) {
    val builders = mutableListOf<RecyclerViewLayoutProvider>()
    val adapterBuilder = RecyclerViewAdapterBuilder(this)

    setTag(TAG_RECYCLER_VIEW_BUILDERS_TEMP, builders)

    adapterBuilder.builder()

    setTag(TAG_RECYCLER_VIEW_BUILDERS_TEMP, null)

    val adapter = FlowListRecyclerViewAdapter(bindingScope, items, builders)

    this.adapter = adapter

    adapter.notifyDataSetChanged()
}
