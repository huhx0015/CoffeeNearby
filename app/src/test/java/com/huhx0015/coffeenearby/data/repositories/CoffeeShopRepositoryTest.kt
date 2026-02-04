package com.huhx0015.coffeenearby.data.repositories

import com.huhx0015.coffeenearby.network.CoffeeShopApi
import com.huhx0015.coffeenearby.network.models.Business
import com.huhx0015.coffeenearby.network.models.Center
import com.huhx0015.coffeenearby.network.models.CoffeeShopResponse
import com.huhx0015.coffeenearby.network.models.Coordinates
import com.huhx0015.coffeenearby.network.models.Location
import com.huhx0015.coffeenearby.network.models.Region
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.mock.Calls

class CoffeeShopRepositoryTest {

  private lateinit var coffeeShopApi: CoffeeShopApi
  private lateinit var repository: CoffeeShopRepository

  @Before
  fun setup() {
    coffeeShopApi = mockk(relaxed = true)
    repository = CoffeeShopRepository(coffeeShopApi)
  }

  @Test
  fun `getCoffeeShops with default offset returns mapped coffee shops`() = runTest {
    // Given
    val mockBusinesses = listOf(
      createMockBusiness(
        id = "1",
        name = "Caribou Coffee",
        rating = 4.5,
        price = "$$",
        distance = 1.5
      ),
      createMockBusiness(
        id = "2",
        name = "Starbucks",
        rating = 4.0,
        price = "$$$",
        distance = 2.0
      )
    )
    val mockResponse = createMockResponse(mockBusinesses)
    
    every {
      coffeeShopApi.getCoffeeShopBusinesses(
        location = "Plymouth, MN, U.S.A.",
        term = "coffee",
        limit = 20,
        sortBy = "distance",
        offset = 0
      )
    } returns Calls.response(mockResponse)

    // When
    val result = repository.getCoffeeShops()

    // Then
    assertEquals(2, result.size)
    assertEquals("1", result[0].id)
    assertEquals("Caribou Coffee", result[0].name)
    assertEquals(4.5, result[0].rating, 0.0)
    assertEquals("$$", result[0].price)
    assertEquals(1.5, result[0].distance, 0.0)
    assertEquals("123 Main St", result[0].address)

    coVerify {
      coffeeShopApi.getCoffeeShopBusinesses(
        location = "Plymouth, MN, U.S.A.",
        term = "coffee",
        limit = 20,
        sortBy = "distance",
        offset = 0
      )
    }
  }

  @Test
  fun `getCoffeeShops with custom offset returns coffee shops from that offset`() = runTest {
    // Given
    val offset = 20
    val mockBusinesses = listOf(
      createMockBusiness(
        id = "21",
        name = "Coffee Shop 21",
        rating = 3.5,
        price = "$",
        distance = 3.0
      )
    )
    val mockResponse = createMockResponse(mockBusinesses)
    
    every {
      coffeeShopApi.getCoffeeShopBusinesses(
        location = "Plymouth, MN, U.S.A.",
        term = "coffee",
        limit = 20,
        sortBy = "distance",
        offset = offset
      )
    } returns Calls.response(mockResponse)

    // When
    val result = repository.getCoffeeShops(offset = offset)

    // Then
    assertEquals(1, result.size)
    assertEquals("21", result[0].id)
    assertEquals("Coffee Shop 21", result[0].name)

    coVerify {
      coffeeShopApi.getCoffeeShopBusinesses(
        location = "Plymouth, MN, U.S.A.",
        term = "coffee",
        limit = 20,
        sortBy = "distance",
        offset = offset
      )
    }
  }

  @Test
  fun `getCoffeeShops handles null price correctly`() = runTest {
    // Given
    val mockBusinesses = listOf(
      createMockBusiness(
        id = "1",
        name = "Free Coffee Place",
        rating = 4.0,
        price = null,
        distance = 1.0
      )
    )
    val mockResponse = createMockResponse(mockBusinesses)
    
    every {
      coffeeShopApi.getCoffeeShopBusinesses(
        location = any(),
        term = any(),
        limit = any(),
        sortBy = any(),
        offset = any()
      )
    } returns Calls.response(mockResponse)

    // When
    val result = repository.getCoffeeShops()

    // Then
    assertEquals(1, result.size)
    assertEquals("", result[0].price) // Should be empty string when null
  }

  @Test
  fun `getCoffeeShops handles null address correctly`() = runTest {
    // Given
    val mockBusinesses = listOf(
      createMockBusiness(
        id = "1",
        name = "No Address Coffee",
        rating = 4.0,
        price = "$$",
        distance = 1.0,
        address = null
      )
    )
    val mockResponse = createMockResponse(mockBusinesses)
    
    every {
      coffeeShopApi.getCoffeeShopBusinesses(
        location = any(),
        term = any(),
        limit = any(),
        sortBy = any(),
        offset = any()
      )
    } returns Calls.response(mockResponse)

    // When
    val result = repository.getCoffeeShops()

    // Then
    assertEquals(1, result.size)
    assertEquals("", result[0].address) // Should be empty string when null
  }

  @Test
  fun `getCoffeeShops returns empty list when API returns no businesses`() = runTest {
    // Given
    val mockResponse = createMockResponse(emptyList())
    
    every {
      coffeeShopApi.getCoffeeShopBusinesses(
        location = any(),
        term = any(),
        limit = any(),
        sortBy = any(),
        offset = any()
      )
    } returns Calls.response(mockResponse)

    // When
    val result = repository.getCoffeeShops()

    // Then
    assertEquals(0, result.size)
  }

  // Helper functions to create mock data
  private fun createMockBusiness(
    id: String,
    name: String,
    rating: Double,
    price: String?,
    distance: Double,
    address: String? = "123 Main St"
  ): Business {
    return Business(
      id = id,
      alias = "alias-$id",
      name = name,
      imageUrl = "https://example.com/image.jpg",
      isClosed = false,
      url = "https://example.com",
      reviewCount = 100,
      categories = emptyList(),
      rating = rating,
      coordinates = Coordinates(latitude = 45.0, longitude = -93.0),
      transactions = emptyList(),
      price = price,
      location = Location(
        address1 = address,
        address2 = null,
        address3 = null,
        city = "Minneapolis",
        zipCode = "55401",
        country = "US",
        state = "MN",
        displayAddress = listOf(address ?: "", "Minneapolis, MN 55401")
      ),
      phone = "+15551234567",
      displayPhone = "(555) 123-4567",
      distance = distance
    )
  }

  private fun createMockResponse(businesses: List<Business>): CoffeeShopResponse {
    return CoffeeShopResponse(
      businesses = businesses,
      total = businesses.size,
      region = Region(
        center = Center(latitude = 45.0, longitude = -93.0)
      )
    )
  }
}
