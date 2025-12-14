package com.aryandi.marketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aryandi.marketplace.data.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val description: String,
    val image: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

// Extension functions for easy conversion
fun ProductEntity.toProduct(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        category = category,
        description = description,
        image = image
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        price = price,
        category = category,
        description = description,
        image = image
    )
}
