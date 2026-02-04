package com.huhx0015.coffeenearby.ui.screens.coffeeshoplist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.huhx0015.coffeenearby.data.models.CoffeeShop
import com.huhx0015.coffeenearby.data.repositories.CoffeeShopRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoffeeShopListViewModelTest {

  @get:Rule
  val instantExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = StandardTestDispatcher()
  private lateinit var repository: CoffeeShopRepository
  private lateinit var viewModel: CoffeeShopListViewModel

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    repository = mockk()
    viewModel = CoffeeShopListViewModel(repository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `initial state is correct`() = runTest {
    // Then
    val state = viewModel.state.value
    assertTrue(state.coffeeShopList.isEmpty())
    assertFalse(state.isCoffeeShopListLoading)
    assertFalse(state.isCoffeeShopListLoadingMore)
    assertFalse(state.isCoffeeShopListRefreshing)
    assertTrue(state.hasMorePages)
    assertEquals(0, state.currentOffset)
  }

  @Test
  fun `LoadCoffeeShopListIntent sets loading state and loads coffee shops`() = runTest {
    // Given
    val mockCoffeeShops = listOf(
      createMockCoffeeShop("1", "Caribou Coffee"),
      createMockCoffeeShop("2", "Starbucks")
    )
    coEvery { repository.getCoffeeShops(0) } returns mockCoffeeShops

    // When
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // Then
    val state = viewModel.state.value
    assertEquals(2, state.coffeeShopList.size)
    assertEquals("Caribou Coffee", state.coffeeShopList[0].name)
    assertEquals("Starbucks", state.coffeeShopList[1].name)
    assertFalse(state.isCoffeeShopListLoading)
    assertEquals(2, state.currentOffset)
    assertTrue(state.hasMorePages)

    coVerify { repository.getCoffeeShops(0) }
  }

  @Test
  fun `LoadCoffeeShopListIntent prevents multiple simultaneous loads`() = runTest {
    // Given
    coEvery { repository.getCoffeeShops(any()) } returns emptyList()

    // When - Send intent twice quickly
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // Then - Repository should only be called once
    coVerify(exactly = 1) { repository.getCoffeeShops(0) }
  }

  @Test
  fun `LoadCoffeeShopListIntent handles error and emits error event`() = runTest {
    // Given
    val errorMessage = "Network error"
    coEvery { repository.getCoffeeShops(any()) } throws Exception(errorMessage)

    // When
    viewModel.events.test {
      viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
      advanceUntilIdle()

      // Then
      val event = awaitItem() as CoffeeShopListEvent.ErrorEvent
      assertEquals(errorMessage, event.errorMessage)
    }

    val state = viewModel.state.value
    assertFalse(state.isCoffeeShopListLoading)
    assertTrue(state.coffeeShopList.isEmpty())
  }

  @Test
  fun `RefreshCoffeeShopListIntent sets refreshing state and reloads coffee shops`() = runTest {
    // Given - First load some data
    val initialCoffeeShops = listOf(createMockCoffeeShop("1", "Initial Coffee"))
    coEvery { repository.getCoffeeShops(0) } returns initialCoffeeShops
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // When - Refresh
    val refreshedCoffeeShops = listOf(
      createMockCoffeeShop("1", "Refreshed Coffee"),
      createMockCoffeeShop("2", "New Coffee")
    )
    coEvery { repository.getCoffeeShops(0) } returns refreshedCoffeeShops
    viewModel.sendIntent(CoffeeShopListIntent.RefreshCoffeeShopListIntent)
    advanceUntilIdle()

    // Then
    val state = viewModel.state.value
    assertEquals(2, state.coffeeShopList.size)
    assertEquals("Refreshed Coffee", state.coffeeShopList[0].name)
    assertFalse(state.isCoffeeShopListRefreshing)
    assertEquals(2, state.currentOffset) // Offset is set to list size after load
  }

  @Test
  fun `RefreshCoffeeShopListIntent resets offset and hasMorePages`() = runTest {
    // Given - Load initial data
    val initialCoffeeShops = listOf(createMockCoffeeShop("1", "Coffee 1"))
    coEvery { repository.getCoffeeShops(any()) } returns initialCoffeeShops
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // When - Refresh
    viewModel.sendIntent(CoffeeShopListIntent.RefreshCoffeeShopListIntent)
    advanceUntilIdle()

    // Then
    val state = viewModel.state.value
    assertTrue(state.hasMorePages)
  }

  @Test
  fun `LoadMoreCoffeeShopsIntent loads more coffee shops and appends to list`() = runTest {
    // Given - Initial load
    val initialCoffeeShops = listOf(
      createMockCoffeeShop("1", "Coffee 1"),
      createMockCoffeeShop("2", "Coffee 2")
    )
    coEvery { repository.getCoffeeShops(0) } returns initialCoffeeShops
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // When - Load more
    val moreCoffeeShops = listOf(
      createMockCoffeeShop("3", "Coffee 3"),
      createMockCoffeeShop("4", "Coffee 4")
    )
    coEvery { repository.getCoffeeShops(2) } returns moreCoffeeShops
    viewModel.sendIntent(CoffeeShopListIntent.LoadMoreCoffeeShopsIntent)
    advanceUntilIdle()

    // Then
    val state = viewModel.state.value
    assertEquals(4, state.coffeeShopList.size)
    assertEquals("Coffee 1", state.coffeeShopList[0].name)
    assertEquals("Coffee 4", state.coffeeShopList[3].name)
    assertFalse(state.isCoffeeShopListLoadingMore)
    assertEquals(4, state.currentOffset)
    assertTrue(state.hasMorePages)

    coVerify { repository.getCoffeeShops(2) }
  }

  @Test
  fun `LoadMoreCoffeeShopsIntent sets hasMorePages to false when empty list returned`() = runTest {
    // Given - Initial load
    val initialCoffeeShops = listOf(createMockCoffeeShop("1", "Coffee 1"))
    coEvery { repository.getCoffeeShops(0) } returns initialCoffeeShops
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // When - Load more returns empty list
    coEvery { repository.getCoffeeShops(1) } returns emptyList()
    viewModel.sendIntent(CoffeeShopListIntent.LoadMoreCoffeeShopsIntent)
    advanceUntilIdle()

    // Then
    val state = viewModel.state.value
    assertEquals(1, state.coffeeShopList.size) // Original list unchanged
    assertFalse(state.hasMorePages) // No more pages available
    assertFalse(state.isCoffeeShopListLoadingMore)
  }

  @Test
  fun `LoadMoreCoffeeShopsIntent prevents loading when already loading more`() = runTest {
    // Given - Initial load
    val initialCoffeeShops = listOf(createMockCoffeeShop("1", "Coffee 1"))
    coEvery { repository.getCoffeeShops(0) } returns initialCoffeeShops
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // When - Send load more twice
    coEvery { repository.getCoffeeShops(1) } returns listOf(createMockCoffeeShop("2", "Coffee 2"))
    viewModel.sendIntent(CoffeeShopListIntent.LoadMoreCoffeeShopsIntent)
    viewModel.sendIntent(CoffeeShopListIntent.LoadMoreCoffeeShopsIntent)
    advanceUntilIdle()

    // Then - Repository should only be called once
    coVerify(exactly = 1) { repository.getCoffeeShops(1) }
  }

  @Test
  fun `LoadMoreCoffeeShopsIntent prevents loading when no more pages`() = runTest {
    // Given - Initial load with empty result (no more pages)
    coEvery { repository.getCoffeeShops(0) } returns emptyList()
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // When - Try to load more
    viewModel.sendIntent(CoffeeShopListIntent.LoadMoreCoffeeShopsIntent)
    advanceUntilIdle()

    // Then - Should not call repository again
    coVerify(exactly = 1) { repository.getCoffeeShops(any()) }
  }

  @Test
  fun `LoadMoreCoffeeShopsIntent handles error and emits error event`() = runTest {
    // Given - Initial load
    val initialCoffeeShops = listOf(createMockCoffeeShop("1", "Coffee 1"))
    coEvery { repository.getCoffeeShops(0) } returns initialCoffeeShops
    viewModel.sendIntent(CoffeeShopListIntent.LoadCoffeeShopListIntent)
    advanceUntilIdle()

    // When - Load more fails
    val errorMessage = "Failed to load more"
    coEvery { repository.getCoffeeShops(1) } throws Exception(errorMessage)

    viewModel.events.test {
      viewModel.sendIntent(CoffeeShopListIntent.LoadMoreCoffeeShopsIntent)
      advanceUntilIdle()

      // Then
      val event = awaitItem() as CoffeeShopListEvent.ErrorEvent
      assertEquals(errorMessage, event.errorMessage)
    }

    val state = viewModel.state.value
    assertFalse(state.isCoffeeShopListLoadingMore)
    assertEquals(1, state.coffeeShopList.size) // Original list preserved
  }

  // Helper function to create mock coffee shops
  private fun createMockCoffeeShop(id: String, name: String) = CoffeeShop(
    id = id,
    name = name,
    imageUrl = "https://example.com/image.jpg",
    rating = 4.5,
    price = "$$",
    distance = 1.5,
    address = "123 Main St"
  )
}
