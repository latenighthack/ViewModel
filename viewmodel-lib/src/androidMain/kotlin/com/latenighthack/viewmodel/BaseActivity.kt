package com.latenighthack.viewmodel

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.*

public abstract class BaseActivity<ViewModelType : NavigableViewModel<StateType, ArgsType>, StateType, ArgsType : NavigatorArgs> :
    Activity() {
    private lateinit var rootLayout: View
    protected lateinit var viewModel: ViewModelType

    protected inline fun <reified ArgType : NavigatorArgs> args(): ArgType {
        return ArgType::class.javaObjectType.newInstance().also {
            val argsBundle = intent.extras?.getBundle("args") ?: Bundle()

            it.bundle.putAll(argsBundle)
        }
    }

    private val job: CompletableJob = SupervisorJob()
    protected val bindingScope: CoroutineScope = MainScope() + job

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as? ActivitiesProvider)?.addActivity(this)

        viewModel = createViewModel()

        rootLayout = doCreateView(this, viewModel)

        super.onCreate(savedInstanceState)

        setContentView(rootLayout)

        bindingScope.launch {
            viewModel.state.collect { state ->
                rootLayout.post {
                    onStateChanged(viewModel, state)
                }
            }
        }
    }

    protected abstract fun createViewModel(): ViewModelType

    protected abstract fun createView(context: Context, viewModel: ViewModelType): View

    private fun doCreateView(context: Context, viewModel: ViewModelType): View {
        val rootLayout = createView(context, viewModel)

        onBindView(viewModel)

        return rootLayout
    }

    protected open fun onBindView(viewModel: ViewModelType) {
    }

    protected open fun onStateChanged(viewModel: ViewModelType, state: StateType) {
    }

    protected fun <T : ViewModel<U>, U> bindChildViewModel(childViewModel: T, stateChanged: (T, U) -> Unit) {
        bindingScope.launch {
            childViewModel.state.collect { state ->
                rootLayout.post {
                    stateChanged(childViewModel, state)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        (application as? ActivitiesProvider)?.removeActivity(this)

        job.complete()
    }
}
