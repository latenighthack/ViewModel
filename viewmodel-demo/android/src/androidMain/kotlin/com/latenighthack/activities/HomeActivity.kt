package com.latenighthack.activities

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.latenighthack.CoreBaseActivity
import com.latenighthack.viewmodel.core.HomeViewModel
import com.latenighthack.viewmodel.core.IHomeListItemAViewModel
import com.latenighthack.viewmodel.core.IHomeListItemBViewModel
import com.latenighthack.viewmodel.core.IHomeViewModel
import com.latenighthack.viewmodel.items
import com.latenighthack.viewmodel.views.*

class HomeActivity: CoreBaseActivity<IHomeViewModel, IHomeViewModel.State, IHomeViewModel.Args>() {
    private lateinit var startButton: Button
    private lateinit var headerText: TextView

    override fun createView(context: Context, viewModel: IHomeViewModel): View = VerticalLayout {
        headerText = TextView(text = "Test", layout = LayoutType.DEFAULT)
        startButton = Button(text = "Click Me", layout = LayoutType.DEFAULT)

        VerticalRecyclerView(layout = LayoutType.FILL_VERTICALLY) {
            items(bindingScope, viewModel.items) {
                layout<TextView, IHomeListItemAViewModel, IHomeListItemAViewModel.State> {
                    onBindView {
                        setTextColor(0xffff0000.toInt())
                    }

                    onStateChanged { _, state ->
                        text = state.titleA
                    }
                }

                layout<TextView, IHomeListItemBViewModel, IHomeListItemBViewModel.State> {
                    onStateChanged { _, state ->
                        text = state.titleB
                    }
                }
            }
        }
    }

    override fun createViewModel() = HomeViewModel(args(), core.simpleStore, core.dummyService)

    override fun onBindView(viewModel: IHomeViewModel) {
        startButton.onClick(bindingScope, viewModel::onStartTapped)
    }

    override fun onStateChanged(viewModel: IHomeViewModel, state: IHomeViewModel.State) {
        headerText.text = state.text
    }
}
