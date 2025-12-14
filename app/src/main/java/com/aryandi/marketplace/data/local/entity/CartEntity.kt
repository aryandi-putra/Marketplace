package com.aryandi.marketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.aryandi.marketplace.data.model.Cart
import com.aryandi.marketplace.data.model.CartProduct
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "carts")
@TypeConverters(CartProductListConverter::class)
data class CartEntity(
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProductData>,
    val lastUpdated: Long = System.currentTimeMillis(),
    val needsSync: Boolean = false  // Flag to track if changes need API sync
)

@kotlinx.serialization.Serializable
data class CartProductData(
    val productId: Int,
    val quantity: Int
)

class CartProductListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromCartProductList(value: List<CartProductData>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toCartProductList(value: String): List<CartProductData> {
        return json.decodeFromString(value)
    }
}

fun CartEntity.toCart(): Cart {
    return Cart(
        id = id,
        userId = userId,
        date = date,
        products = products.map {
            CartProduct(productId = it.productId, quantity = it.quantity)
        }
    )
}

fun Cart.toEntity(): CartEntity {
    return CartEntity(
        id = id,
        userId = userId,
        date = date,
        products = products.map {
            CartProductData(productId = it.productId, quantity = it.quantity)
        }
    )
}
