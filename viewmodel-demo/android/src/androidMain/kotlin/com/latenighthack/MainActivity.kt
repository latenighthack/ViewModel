package com.latenighthack

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.latenighthack.viewmodel.BaseActivity
import com.latenighthack.viewmodel.core.HomeViewModel
import com.latenighthack.viewmodel.core.IHomeViewModel

class MainActivity : BaseActivity<IHomeViewModel, IHomeViewModel.State, IHomeViewModel.Args>() {
    override fun createView(context: Context, viewModel: IHomeViewModel): View {
        return LinearLayout(context)
    }

    override fun createViewModel() = HomeViewModel(args())

    override fun onStateChanged(viewModel: IHomeViewModel, state: IHomeViewModel.State) {
        println(state.text)
    }
}
