package com.latenighthack.viewmodel.demo.activities

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.latenighthack.viewmodel.BaseActivity
import com.latenighthack.viewmodel.core.HomeViewModel
import com.latenighthack.viewmodel.core.IHomeViewModel

class MainActivity: BaseActivity<IHomeViewModel, IHomeViewModel.State, IHomeViewModel.Args>() {
    private lateinit var text: TextView

    override fun createViewModel() = HomeViewModel(args())

    override fun createView(context: Context, viewModel: IHomeViewModel): View {
        val layout = LinearLayout(context)

        text = TextView(context)

        layout.addView(text)

        return layout
    }

    override fun onBindView(viewModel: IHomeViewModel) {
        viewModel.initialState
    }

    override fun onStateChanged(viewModel: IHomeViewModel, state: IHomeViewModel.State) {
        text.text = state.text
    }
}
