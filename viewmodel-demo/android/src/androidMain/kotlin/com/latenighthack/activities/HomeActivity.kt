package com.latenighthack.activities

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.latenighthack.CoreBaseActivity
import com.latenighthack.viewmodel.core.HomeViewModel
import com.latenighthack.viewmodel.core.IHomeViewModel

class HomeActivity: CoreBaseActivity<IHomeViewModel, IHomeViewModel.State, IHomeViewModel.Args>() {
    override fun createView(context: Context, viewModel: IHomeViewModel): View {
        return LinearLayout(context)
    }

    override fun createViewModel() = HomeViewModel(args(), core.simpleStore, core.dummyService)

    override fun onStateChanged(viewModel: IHomeViewModel, state: IHomeViewModel.State) {
        println(state.text)
    }
}
