package com.huhx0015.coffeenearby.data.repositories

import com.huhx0015.coffeenearby.data.models.CoffeeShop
import com.huhx0015.coffeenearby.network.CoffeeShopApi
import com.huhx0015.coffeenearby.network.models.Business
import retrofit2.await

class CoffeeShopRepository(private val coffeeShopApi: CoffeeShopApi) {

  companion object {
    private const val DEFAULT_ADDRESS = "Plymouth, MN, U.S.A."
    private const val DEFAULT_LIMIT = 20
    private const val DEFAULT_TERM = "coffee"
    private const val SORT_BY_DISTANCE = "distance"
  }

  suspend fun getCoffeeShops(offset: Int = 0): List<CoffeeShop> {
    val response = coffeeShopApi.getCoffeeShopBusinesses(
      location = DEFAULT_ADDRESS,
      term = DEFAULT_TERM,
      limit = DEFAULT_LIMIT,
      sortBy = SORT_BY_DISTANCE,
      offset = offset
    ).await()
    return response.businesses.map { it.toCoffeeShop() }
  }

  private fun Business.toCoffeeShop() = CoffeeShop(
    id = id,
    name = name,
    imageUrl = imageUrl,
    rating = rating,
    price = price ?: String(),
    distance = distance,
    address = location.address1 ?: String()
  )
}