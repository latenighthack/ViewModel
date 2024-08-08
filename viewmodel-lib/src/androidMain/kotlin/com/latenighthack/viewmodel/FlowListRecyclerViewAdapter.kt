package com.latenighthack.viewmodel

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.latenighthack.viewmodel.list.Change
import com.latenighthack.viewmodel.list.Delta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

public class FlowListRecyclerViewAdapter(
    private val itemScope: CoroutineScope,
    private val items: Flow<Delta<*>>,
    private val layoutProviders: List<RecyclerViewLayoutProvider>
): RecyclerView.Adapter<FlowListRecyclerViewAdapter.StateViewHolder>() {
    public inner class StateViewHolder(itemView: View, private val provider: RecyclerViewLayoutProvider): RecyclerView.ViewHolder(itemView) {
        private var itemJob: Job? = null

        public fun onBind(item: Any) {
            provider.onBindView(itemView, item)

            if (item is ViewModel<*>) {
                provider.onStateChanged(itemView, item, item.initialState!!)

                itemJob = itemScope.launch {
                    item.state
                        .drop(1)
                        .collect { state ->
                            provider.onStateChanged(itemView, item, state!!)
                        }
                }
            }
        }

        public fun onRecycled() {
            itemJob?.cancel()
        }
    }

    private var parent: RecyclerView? = null
    private var currentItems: List<*>? = emptyList<Any>()
    private var itemsChangedJob: Job? = null

    override fun getItemCount(): Int = currentItems?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        val item = currentItems?.get(position) ?: return 0

        return layoutProviders.indexOfFirst { it.matches(item) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val provider = layoutProviders[viewType]
        val view = provider.createView(parent.context)

        return StateViewHolder(view, provider)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        val item = currentItems?.get(position) ?: return

        holder.onBind(item)
    }

    override fun onViewRecycled(holder: StateViewHolder) {
        super.onViewRecycled(holder)

        holder.onRecycled()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        start()
        parent = recyclerView

        recyclerView.addOnAttachStateChangeListener(object :
            View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(p0: View) {
                stop()
            }

            override fun onViewAttachedToWindow(p0: View) {
                start()
            }
        })
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        stop()
        parent = null
    }

    private fun stop() {
        if (itemsChangedJob != null) {
            return
        }

        itemsChangedJob?.cancel()
        itemsChangedJob = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun start() {
        if (itemsChangedJob != null) {
            return
        }

        itemsChangedJob = itemScope.launch(Dispatchers.Main.immediate) {
            items.collect {
                currentItems = it.items

                it.change.forEach { change ->
                    when (change) {
                        is Change.Deleted -> notifyItemRangeRemoved(change.from.first, change.from.endInclusive - change.from.first + 1)
                        is Change.Inserted -> notifyItemRangeInserted(change.to.first, change.to.endInclusive - change.to.first + 1)
                        is Change.Moved -> notifyItemMoved(change.from, change.to)
                        is Change.Refresh -> notifyItemChanged(change.index)
                        is Change.Reloaded -> notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
