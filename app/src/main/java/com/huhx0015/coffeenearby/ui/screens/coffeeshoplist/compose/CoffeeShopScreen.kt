package com.huhx0015.coffeenearby.ui.screens.coffeeshoplist.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.huhx0015.coffeenearby.R
import com.huhx0015.coffeenearby.data.models.CoffeeShop
import com.huhx0015.coffeenearby.ui.screens.coffeeshoplist.CoffeeShopListState
import com.huhx0015.coffeenearby.ui.theme.CoffeeNearbyTheme

@Composable
internal fun CoffeeShopScreen(
  state: CoffeeShopListState,
  onRefresh: () -> Unit = {},
  onLoadMore: () -> Unit = {}
) {
  Scaffold(
    topBar = { CoffeeShopScreenTopBar() },
    floatingActionButton = {
      CoffeeShopFloatingActionButton(
        floatingActionButtonClick = { onRefresh() }
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize()) {
      CoffeeShopScreenContent(
        state = state,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        modifier = Modifier.padding(paddingValues)
      )

      if (state.isCoffeeShopListRefreshing && state.coffeeShopList.isNotEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoffeeShopScreenTopBar() {
  TopAppBar(
    title = { Text(
      text = stringResource(R.string.app_name)
    ) },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primary,
      titleContentColor = MaterialTheme.colorScheme.onPrimary
    )
  )
}

@Composable
private fun CoffeeShopFloatingActionButton(floatingActionButtonClick: () -> Unit) {
  FloatingActionButton(
    onClick = { floatingActionButtonClick.invoke() },
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary
  ) {
    Icon(
      painter = painterResource(R.drawable.ic_coffee),
      contentDescription = stringResource(R.string.floating_button_description)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoffeeShopScreenContent(
  state: CoffeeShopListState,
  onRefresh: () -> Unit,
  onLoadMore: () -> Unit,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()

  LaunchedEffect(listState) {
    snapshotFlow {
      val layoutInfo = listState.layoutInfo
      val totalItemsCount = layoutInfo.totalItemsCount
      val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

      lastVisibleItemIndex >= totalItemsCount - 3
    }.collect { shouldLoadMore ->
      if (shouldLoadMore && state.hasMorePages && !state.isCoffeeShopListLoadingMore) {
        onLoadMore()
      }
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    if ((state.isCoffeeShopListLoading ||
          state.isCoffeeShopListRefreshing) && state.coffeeShopList.isEmpty()) {
      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        color = MaterialTheme.colorScheme.primary
      )
    } else {
      val showPullToRefresh = state.isCoffeeShopListRefreshing && state.coffeeShopList.isEmpty()
      
      PullToRefreshBox(
        isRefreshing = showPullToRefresh,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
      ) {
        LazyColumn(
          state = listState,
          modifier = Modifier.padding(8.dp)
        ) {
          items(state.coffeeShopList) { coffeeShop ->
            CoffeeShopItem(coffeeShop = coffeeShop)
            Spacer(modifier = Modifier.height(8.dp))
          }
          if (state.isCoffeeShopListLoadingMore) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                contentAlignment = Alignment.Center
              ) {
                CircularProgressIndicator(
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CoffeeShopItem(
  coffeeShop: CoffeeShop,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Card(
      modifier = Modifier.size(72.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      CoffeeShopImage(imageUrl = coffeeShop.imageUrl)
    }

    Spacer(modifier = Modifier.width(16.dp))

    CoffeeShopDetails(
      name = coffeeShop.name,
      address = coffeeShop.address,
      price = coffeeShop.price
    )
  }
}

@Composable
private fun CoffeeShopImage(imageUrl: String?) {
  Box(modifier = Modifier.fillMaxSize()) {
    if (!imageUrl.isNullOrEmpty()) {
      var isLoading by remember { mutableStateOf(false) }

      AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(R.string.coffee_shop_item_image_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        placeholder = painterResource(R.drawable.ic_coffee),
        error = painterResource(R.drawable.ic_coffee),
        onLoading = { isLoading = true },
        onSuccess = { isLoading = false },
        onError = { isLoading = false }
      )
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.align(Alignment.Center)
        )
      }
    } else {
      AsyncImage(
        model = R.drawable.ic_coffee,
        contentDescription = stringResource(R.string.coffee_shop_item_image_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

@Composable
private fun CoffeeShopDetails(name: String, address: String?, price: String?) {
  Column {
    Text(
      text = name,
      style = MaterialTheme.typography.titleMedium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = address.orEmpty(),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = price.orEmpty(),
      style = MaterialTheme.typography.labelLarge
    )
  }
}

@Preview(showBackground = true, name = "Coffee Shop Screen - With Data")
@Composable
private fun CoffeeShopScreenPreview() {
  CoffeeNearbyTheme {
    val mockState = CoffeeShopListState(
      coffeeShopList = listOf(
        CoffeeShop(
          id = "1",
          name = "Caribou Coffee",
          imageUrl = null,
          rating = 4.5,
          price = "$$",
          distance = 1.5,
          address = "123 Main St"
        ),
        CoffeeShop(
          id = "2",
          name = "Starbucks",
          imageUrl = null,
          rating = 4.0,
          price = "$$$",
          distance = 2.0,
          address = "456 Oak Ave"
        )
      ),
      isCoffeeShopListLoading = false,
      isCoffeeShopListLoadingMore = false,
      isCoffeeShopListRefreshing = false
    )
    CoffeeShopScreen(
      state = mockState,
      onRefresh = {},
      onLoadMore = {}
    )
  }
}

@Preview(showBackground = true, name = "Coffee Shop Screen - Loading")
@Composable
private fun CoffeeShopScreenLoadingPreview() {
  CoffeeNearbyTheme {
    val mockState = CoffeeShopListState(
      coffeeShopList = emptyList(),
      isCoffeeShopListLoading = true,
      isCoffeeShopListLoadingMore = false,
      isCoffeeShopListRefreshing = false
    )
    CoffeeShopScreen(
      state = mockState,
      onRefresh = {},
      onLoadMore = {}
    )
  }
}

@Preview(showBackground = true, name = "Top App Bar")
@Composable
private fun CoffeeShopScreenTopBarPreview() {
  CoffeeNearbyTheme {
    CoffeeShopScreenTopBar()
  }
}

@Preview(showBackground = true, name = "Floating Action Button")
@Composable
private fun CoffeeShopFloatingActionButtonPreview() {
  CoffeeNearbyTheme {
    CoffeeShopFloatingActionButton(
      floatingActionButtonClick = {}
    )
  }
}

@Preview(showBackground = true, name = "Coffee Shop Item")
@Composable
private fun CoffeeShopItemPreview() {
  CoffeeNearbyTheme {
    CoffeeShopItem(
      coffeeShop = CoffeeShop(
        id = "1",
        name = "Caribou Coffee",
        imageUrl = null,
        rating = 4.5,
        price = "$$",
        distance = 1.5,
        address = "123 Main Street, Minneapolis, MN"
      )
    )
  }
}

@Preview(showBackground = true, name = "Coffee Shop Image")
@Composable
private fun CoffeeShopImagePreview() {
  CoffeeNearbyTheme {
    Box(modifier = Modifier.size(80.dp)) {
      CoffeeShopImage(imageUrl = null)
    }
  }
}

@Preview(showBackground = true, name = "Coffee Shop Details")
@Composable
private fun CoffeeShopDetailsPreview() {
  CoffeeNearbyTheme {
    CoffeeShopDetails(
      name = "Caribou Coffee",
      address = "123 Main Street, Minneapolis, MN",
      price = "$$"
    )
  }
}