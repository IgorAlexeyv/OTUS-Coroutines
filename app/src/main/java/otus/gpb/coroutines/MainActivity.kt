package otus.gpb.coroutines

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import otus.gpb.coroutines.databinding.ActivityMainBinding
import otus.gpb.coroutines.databinding.ContentBinding
import otus.gpb.coroutines.databinding.LoadingBinding
import otus.gpb.coroutines.databinding.LoginBinding

class MainActivity : AppCompatActivity() {

    private lateinit var login: LoginBinding
    private lateinit var loading: LoadingBinding
    private lateinit var content: ContentBinding

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        login = binding.loginScreen
        loading = binding.loadingScreen
        content = binding.contentScreen

        setContentView(binding.root)

        setupLogin()
        setupLoading()
        setupContent()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when(it) {
                        is MainActivityViewState.Content -> showContent(it)
                        MainActivityViewState.Loading -> showLoading()
                        MainActivityViewState.Login -> showLogin()
                    }
                }
            }
        }
    }

    private fun showLogin() {
        login.loginGroup.isVisible = true
        loading.loadingGroup.isVisible = false
        content.contentGroup.isVisible = false
    }

    private fun showLoading() {
        login.loginGroup.isVisible = false
        loading.loadingGroup.isVisible = true
        content.contentGroup.isVisible = false
    }

    private fun showContent(state: MainActivityViewState.Content) {
        login.loginGroup.isVisible = false
        loading.loadingGroup.isVisible = false
        content.contentGroup.isVisible = true

        content.title.title = state.name
    }

    private fun setupLogin() {
        login.loginGroup.isVisible = false
        login.loginButton.setOnClickListener {
            val name = login.login.text?.trim() ?: ""
            val password = login.password.text?.trim() ?: ""

            viewModel.login(name.toString(), password.toString())
        }
    }

    private fun setupLoading() {
        loading.loadingGroup.isVisible = false
    }

    private fun setupContent() {
        content.contentGroup.isVisible = false
        content.title.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.logout -> {
                    viewModel.logout()
                    true
                }
                else -> false
            }
        }
    }
}