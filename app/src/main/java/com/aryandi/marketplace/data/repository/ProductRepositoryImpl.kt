package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.local.dao.ProductDao
import com.aryandi.marketplace.data.local.entity.toEntity
import com.aryandi.marketplace.data.local.entity.toProduct
import com.aryandi.marketplace.data.model.Product
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.util.Resource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val productDao: ProductDao
) : ProductRepository {

    companion object {
        // Cache validity: 1 hour
        private const val CACHE_VALIDITY_DURATION = 60 * 60 * 1000L
    }

    override suspend fun getAllProducts(): Resource<List<Product>> {
        return try {
            // Check cache first
            val cachedProducts = productDao.getAllProducts().first()

            if (cachedProducts.isNotEmpty() && isCacheValid()) {
                // Return cached data if valid
                Resource.Success(cachedProducts.map { it.toProduct() })
            } else {
                // Fetch from network
                val products = productApi.getAllProducts()

                // Update cache
                productDao.deleteAllProducts()
                productDao.insertProducts(products.map { it.toEntity() })

                Resource.Success(products)
            }
        } catch (e: Exception) {
            // On error, try to return cached data as fallback
            val cachedProducts = productDao.getAllProducts().first()
            if (cachedProducts.isNotEmpty()) {
                Resource.Success(cachedProducts.map { it.toProduct() })
            } else {
                Resource.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    override suspend fun getAllCategories(): Resource<List<String>> {
        return try {
            // Check cache first
            val cachedCategories = productDao.getAllCategories()

            if (cachedCategories.isNotEmpty() && isCacheValid()) {
                Resource.Success(cachedCategories)
            } else {
                // Fetch from network
                val categories = productApi.getAllCategories()
                Resource.Success(categories)
            }
        } catch (e: Exception) {
            // On error, try to return cached categories
            val cachedCategories = productDao.getAllCategories()
            if (cachedCategories.isNotEmpty()) {
                Resource.Success(cachedCategories)
            } else {
                Resource.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    override suspend fun getProductsByCategory(category: String): Resource<List<Product>> {
        return try {
            // Check cache first
            val cachedProducts = productDao.getProductsByCategory(category).first()

            if (cachedProducts.isNotEmpty() && isCacheValid()) {
                Resource.Success(cachedProducts.map { it.toProduct() })
            } else {
                // Fetch from network
                val products = productApi.getProductsByCategory(category)

                // Update cache for this category
                productDao.insertProducts(products.map { it.toEntity() })

                Resource.Success(products)
            }
        } catch (e: Exception) {
            // On error, try to return cached data
            val cachedProducts = productDao.getProductsByCategory(category).first()
            if (cachedProducts.isNotEmpty()) {
                Resource.Success(cachedProducts.map { it.toProduct() })
            } else {
                Resource.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    override suspend fun getProductById(id: Int): Resource<Product> {
        return try {
            // Check cache first
            val cachedProduct = productDao.getProductById(id)

            if (cachedProduct != null && isCacheValid()) {
                Resource.Success(cachedProduct.toProduct())
            } else {
                // Fetch from network
                val product = productApi.getProductById(id)

                // Update cache
                productDao.insertProduct(product.toEntity())

                Resource.Success(product)
            }
        } catch (e: Exception) {
            // On error, try to return cached data
            val cachedProduct = productDao.getProductById(id)
            if (cachedProduct != null) {
                Resource.Success(cachedProduct.toProduct())
            } else {
                Resource.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    private suspend fun isCacheValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        val validTime = currentTime - CACHE_VALIDITY_DURATION
        return productDao.getCacheValidCount(validTime) > 0
    }
}
