package com.huhx0015.coffeenearby.ui.screens.coffeeshoplist

import android.os.Parcelable
import com.huhx0015.coffeenearby.data.models.CoffeeShop
import com.huhx0015.coffeenearby.ui.base.BaseState
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoffeeShopListState(
  val coffeeShopList: List<CoffeeShop> = emptyList(),
  val isCoffeeShopListLoading: Boolean = false,
  val isCoffeeShopListLoadingMore: Boolean = false,
  val isCoffeeShopListRefreshing: Boolean = false,
  val hasMorePages: Boolean = true,
  val currentOffset: Int = 0
) : BaseState, Parcelable {

  fun coffeeShopLoading(): CoffeeShopListState = this.copy(
    isCoffeeShopListLoading = true
  )

  fun coffeeShopLoaded(coffeeShopList: List<CoffeeShop>): CoffeeShopListState = this.copy(
    coffeeShopList = coffeeShopList,
    isCoffeeShopListLoading = false,
    isCoffeeShopListRefreshing = false,
    currentOffset = coffeeShopList.size,
    hasMorePages = coffeeShopList.isNotEmpty()
  )

  fun coffeeShopRefreshing(): CoffeeShopListState = this.copy(
    isCoffeeShopListRefreshing = true
  )

  fun coffeeShopLoadingMore(): CoffeeShopListState = this.copy(
    isCoffeeShopListLoadingMore = true
  )

  fun coffeeShopLoadedMore(newCoffeeShops: List<CoffeeShop>): CoffeeShopListState = this.copy(
    coffeeShopList = coffeeShopList + newCoffeeShops,
    isCoffeeShopListLoadingMore = false,
    currentOffset = coffeeShopList.size + newCoffeeShops.size,
    hasMorePages = newCoffeeShops.isNotEmpty()
  )
}