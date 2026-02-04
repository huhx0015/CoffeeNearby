package com.huhx0015.coffeenearby.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoffeeShopResponse(
  @Json(name = "businesses")
  val businesses: List<Business>,

  @Json(name = "total")
  val total: Int,

  @Json(name = "region")
  val region: Region
)

@JsonClass(generateAdapter = true)
data class Business(
  @Json(name = "id")
  val id: String,

  @Json(name = "alias")
  val alias: String,

  @Json(name = "name")
  val name: String,

  @Json(name = "image_url")
  val imageUrl: String,

  @Json(name = "is_closed")
  val isClosed: Boolean,

  @Json(name = "url")
  val url: String,

  @Json(name = "review_count")
  val reviewCount: Int,

  @Json(name = "categories")
  val categories: List<Category>,

  @Json(name = "rating")
  val rating: Double,

  @Json(name = "coordinates")
  val coordinates: Coordinates,

  @Json(name = "transactions")
  val transactions: List<String>,

  @Json(name = "price")
  val price: String?,

  @Json(name = "location")
  val location: Location,

  @Json(name = "phone")
  val phone: String,

  @Json(name = "display_phone")
  val displayPhone: String,

  @Json(name = "distance")
  val distance: Double
)

@JsonClass(generateAdapter = true)
data class Category(
  @Json(name = "alias")
  val alias: String,

  @Json(name = "title")
  val title: String
)

@JsonClass(generateAdapter = true)
data class Coordinates(
  @Json(name = "latitude")
  val latitude: Double,

  @Json(name = "longitude")
  val longitude: Double
)

@JsonClass(generateAdapter = true)
data class Location(
  @Json(name = "address1")
  val address1: String?,

  @Json(name = "address2")
  val address2: String?,

  @Json(name = "address3")
  val address3: String?,

  @Json(name = "city")
  val city: String,

  @Json(name = "zip_code")
  val zipCode: String,

  @Json(name = "country")
  val country: String,

  @Json(name = "state")
  val state: String,

  @Json(name = "display_address")
  val displayAddress: List<String>
)

@JsonClass(generateAdapter = true)
data class Region(
  @Json(name = "center")
  val center: Center
)

@JsonClass(generateAdapter = true)
data class Center(
  @Json(name = "longitude")
  val longitude: Double,

  @Json(name = "latitude")
  val latitude: Double
)