package com.huhx0015.coffeenearby.ui.screens.coffeeshoplist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.huhx0015.coffeenearby.R
import com.huhx0015.coffeenearby.ui.screens.coffeeshoplist.compose.CoffeeShopScreen
import com.huhx0015.coffeenearby.ui.theme.CoffeeNearbyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoffeeShopListActivity : ComponentActivity() {
  private val viewModel: CoffeeShopListViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    observeEvents()
    renderCompose()
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
  }

  private fun observeEvents() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.events.collect { event ->
          when (event) {
            is CoffeeShopListEvent.ErrorEvent -> {
              val message = event.errorMessage ?: getString(R.string.error_message)
              Toast.makeText(this@CoffeeShopListActivity, message, Toast.LENGTH_LONG).show()
            }
          }
        }
      }
    }
  }

  private fun renderCompose() {
    setContent {
      CoffeeNearbyTheme {
        val state by viewModel.state.collectAsState()
        CoffeeShopScreen(
          state = state,
          onRefresh = { viewModel.sendIntent(CoffeeShopListIntent.RefreshCoffeeShopListIntent) },
          onLoadMore = { viewModel.sendIntent(CoffeeShopListIntent.LoadMoreCoffeeShopsIntent) }
        )
      }
    }
  }
}