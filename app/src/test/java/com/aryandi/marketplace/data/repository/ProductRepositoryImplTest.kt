package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.Product
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.util.Resource
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ProductRepositoryImplTest {
    private lateinit var productApi: ProductApi
    private lateinit var repository: ProductRepositoryImpl
    
    // Test data
    private val testProductId = 1
    private val testCategory = "electronics"
    private val testProduct = Product(
        id = testProductId,
        title = "Test Product",
        price = 29.99,
        category = testCategory,
        description = "Test Description",
        image = "test.jpg"
    )
    private val testProducts = listOf(testProduct)
    private val testCategories = listOf(testCategory, "clothing", "books")

    @Before
    fun setUp() {
        productApi = mockk()
        repository = ProductRepositoryImpl(productApi)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllProducts returns Resource Success with products list when API call succeeds`() = runBlocking {
        coEvery { productApi.getAllProducts() } returns testProducts

        val result = repository.getAllProducts()

        assertTrue(result is Resource.Success)
        assertEquals(testProducts, result.data)
        coVerify(exactly = 1) { productApi.getAllProducts() }
    }

    @Test
    fun `getAllProducts returns Resource Error when API call fails`() = runBlocking {
        val errorMessage = "Network error"
        coEvery { productApi.getAllProducts() } throws Exception(errorMessage)

        val result = repository.getAllProducts()

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
        coVerify(exactly = 1) { productApi.getAllProducts() }
    }

    @Test
    fun `getAllProducts returns Resource Error with default message when API throws exception without message`() = runBlocking {
        coEvery { productApi.getAllProducts() } throws Exception()

        val result = repository.getAllProducts()

        assertTrue(result is Resource.Error)
        assertEquals("An unexpected error occurred", result.message)
        coVerify(exactly = 1) { productApi.getAllProducts() }
    }

    @Test
    fun `getAllCategories returns Resource Success with categories list when API call succeeds`() = runBlocking {
        coEvery { productApi.getAllCategories() } returns testCategories

        val result = repository.getAllCategories()

        assertTrue(result is Resource.Success)
        assertEquals(testCategories, result.data)
        coVerify(exactly = 1) { productApi.getAllCategories() }
    }

    @Test
    fun `getAllCategories returns Resource Error when API call fails`() = runBlocking {
        val errorMessage = "Categories fetch error"
        coEvery { productApi.getAllCategories() } throws Exception(errorMessage)

        val result = repository.getAllCategories()

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
        coVerify(exactly = 1) { productApi.getAllCategories() }
    }

    @Test
    fun `getProductsByCategory returns Resource Success with filtered products when API call succeeds`() = runBlocking {
        coEvery { productApi.getProductsByCategory(testCategory) } returns testProducts

        val result = repository.getProductsByCategory(testCategory)

        assertTrue(result is Resource.Success)
        assertEquals(testProducts, result.data)
        coVerify(exactly = 1) { productApi.getProductsByCategory(testCategory) }
    }

    @Test
    fun `getProductsByCategory returns empty list when API returns empty list`() = runBlocking {
        coEvery { productApi.getProductsByCategory(testCategory) } returns emptyList()

        val result = repository.getProductsByCategory(testCategory)

        assertTrue(result is Resource.Success)
        assertTrue(result.data?.isEmpty() ?: false)
        coVerify(exactly = 1) { productApi.getProductsByCategory(testCategory) }
    }

    @Test
    fun `getProductsByCategory returns Resource Error when API call fails`() = runBlocking {
        val errorMessage = "Category products fetch error"
        coEvery { productApi.getProductsByCategory(testCategory) } throws Exception(errorMessage)

        val result = repository.getProductsByCategory(testCategory)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
        coVerify(exactly = 1) { productApi.getProductsByCategory(testCategory) }
    }

    @Test
    fun `getProductById returns Resource Success with product when API call succeeds`() = runBlocking {
        coEvery { productApi.getProductById(testProductId) } returns testProduct

        val result = repository.getProductById(testProductId)

        assertTrue(result is Resource.Success)
        assertEquals(testProduct, result.data)
        coVerify(exactly = 1) { productApi.getProductById(testProductId) }
    }

    @Test
    fun `getProductById returns Resource Error when API call fails`() = runBlocking {
        val errorMessage = "Product not found"
        coEvery { productApi.getProductById(testProductId) } throws Exception(errorMessage)

        val result = repository.getProductById(testProductId)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
        coVerify(exactly = 1) { productApi.getProductById(testProductId) }
    }

    @Test
    fun `getProductById returns Resource Error when product with given ID doesn't exist`() = runBlocking {
        val nonExistentId = 999
        val errorMessage = "Product with ID $nonExistentId not found"
        coEvery { productApi.getProductById(nonExistentId) } throws Exception(errorMessage)

        val result = repository.getProductById(nonExistentId)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
        coVerify(exactly = 1) { productApi.getProductById(nonExistentId) }
    }
}