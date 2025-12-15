package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.local.dao.CartDao
import com.aryandi.marketplace.data.local.entity.CartEntity
import com.aryandi.marketplace.data.local.entity.CartProductData
import com.aryandi.marketplace.data.local.entity.toCart
import com.aryandi.marketplace.data.local.entity.toEntity
import com.aryandi.marketplace.data.model.*
import com.aryandi.marketplace.data.remote.CartApi
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.util.Resource
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartRepositoryImplTest {
    private lateinit var cartApi: CartApi
    private lateinit var productApi: ProductApi
    private lateinit var cartDao: CartDao
    private lateinit var repository: CartRepositoryImpl
    
    private val testUserId = 1
    private val testCartId = 10
    private val testProductId = 42
    private val testQuantity = 2
    private val testDate = "2025-12-16" // Today's date
    private val testProduct = Product(
        id = testProductId,
        title = "Test Product",
        price = 29.99,
        category = "Test Category",
        description = "Test Description",
        image = "test.jpg"
    )
    private val testProductData = CartProductData(productId = testProductId, quantity = testQuantity)
    private val testCartItem = CartItem(product = testProduct, quantity = testQuantity)
    private val testCart = Cart(
        id = testCartId,
        userId = testUserId,
        date = testDate,
        products = listOf(CartProduct(productId = testProductId, quantity = testQuantity))
    )
    private val testCartEntity = CartEntity(
        id = testCartId,
        userId = testUserId,
        date = testDate,
        products = listOf(testProductData),
        lastUpdated = System.currentTimeMillis()
    )
    
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    @Before
    fun setUp() {
        cartApi = mockk()
        productApi = mockk()
        cartDao = mockk()
        repository = CartRepositoryImpl(cartApi, productApi, cartDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUserCart returns Resource Success when cache is valid`() = runBlocking {
        coEvery { cartDao.getUserCart(testUserId) } returns testCartEntity
        coEvery { productApi.getProductById(testProductId) } returns testProduct

        val result = repository.getUserCart(testUserId)

        assertTrue(result is Resource.Success)
        assertEquals(listOf(testCartItem), result.data)

        coVerify(exactly = 2) { cartDao.getUserCart(testUserId) }
        coVerify(exactly = 1) { productApi.getProductById(testProductId) }
        coVerify(exactly = 0) { cartApi.getUserCart(any()) }
        confirmVerified(cartDao, productApi, cartApi)
    }

    @Test
    fun `getUserCart refreshes from API when cache is invalid`() = runBlocking {
        val expiredCache = testCartEntity.copy(
            lastUpdated = System.currentTimeMillis() - 15 * 60 * 1000 // 15 minutes ago
        )
        
        coEvery { cartDao.getUserCart(testUserId) } returns expiredCache andThen testCartEntity
        coEvery { cartApi.getUserCart(testUserId) } returns listOf(testCart)
        coEvery { productApi.getProductById(testProductId) } returns testProduct
        coEvery { cartDao.insertCart(any()) } returns 1L
        coEvery { cartDao.cleanupOldCarts(testUserId) } returns Unit

        val result = repository.getUserCart(testUserId)

        assertTrue(result is Resource.Success)
        assertEquals(listOf(testCartItem), result.data)

        coVerify(exactly = 2) { cartDao.getUserCart(testUserId) }
        coVerify(exactly = 1) { cartApi.getUserCart(testUserId) }
        coVerify(exactly = 1) { cartDao.insertCart(any()) }
        coVerify(exactly = 1) { cartDao.cleanupOldCarts(testUserId) }
        coVerify(exactly = 1) { productApi.getProductById(testProductId) }
    }

    @Test
    fun `getUserCart returns from API when no cached cart exists`() = runBlocking {
        coEvery { cartDao.getUserCart(testUserId) } returns null andThen testCartEntity
        coEvery { cartApi.getUserCart(testUserId) } returns listOf(testCart)
        coEvery { productApi.getProductById(testProductId) } returns testProduct
        coEvery { cartDao.insertCart(any()) } returns 1L
        coEvery { cartDao.cleanupOldCarts(testUserId) } returns Unit

        val result = repository.getUserCart(testUserId)

        assertTrue(result is Resource.Success)
        assertEquals(listOf(testCartItem), result.data)

        coVerify(exactly = 2) { cartDao.getUserCart(testUserId) }
        coVerify(exactly = 1) { cartApi.getUserCart(testUserId) }
        coVerify(exactly = 1) { cartDao.insertCart(any()) }
        coVerify(exactly = 1) { cartDao.cleanupOldCarts(testUserId) }
    }

    @Test
    fun `getUserCart returns Resource Error when API throws exception`() = runBlocking {
        val errorMessage = "Network error"
        coEvery { cartDao.getUserCart(testUserId) } returns null
        coEvery { cartApi.getUserCart(testUserId) } throws Exception(errorMessage)

        val result = repository.getUserCart(testUserId)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
        
        coVerify(exactly = 1) { cartDao.getUserCart(testUserId) }
        coVerify(exactly = 1) { cartApi.getUserCart(testUserId) }
    }

    @Test
    fun `addToCart with new cart creates new entry`() = runBlocking {
        coEvery { cartDao.getUserCart(testUserId) } returns null
        coEvery { cartDao.insertCart(any()) } returns 0L
        coEvery { cartDao.cleanupOldCarts(testUserId) } returns Unit
        
        // The cart passed to the API will have the current date
        val cartSlot = slot<Cart>()
        coEvery { cartApi.addToCart(capture(cartSlot)) } answers {
            val cart = cartSlot.captured ?: testCart
            cart.copy(id = cart.id)
        }

        val result = repository.addToCart(testUserId, testProductId, testQuantity)

        assertTrue(result is Resource.Success)
        assertNotNull(result.data)
        assertEquals(testUserId, result.data!!.userId)
        assertEquals(getCurrentDate(), result.data!!.date)
        assertEquals(1, result.data!!.products.size)
        assertEquals(testProductId, result.data!!.products[0].productId)
        assertEquals(testQuantity, result.data!!.products[0].quantity)

        coVerify(exactly = 1) { cartDao.getUserCart(testUserId) }
        coVerify(exactly = 1) { cartDao.insertCart(any()) }
        coVerify(exactly = 1) { cartDao.cleanupOldCarts(testUserId) }
        coVerify(exactly = 1) { cartApi.addToCart(any()) }
        coVerify(exactly = 0) { cartApi.updateCart(any(), any()) }
    }

    @Test
    fun `addToCart with existing cart updates product quantity`() = runBlocking {
        val existingProductData = CartProductData(productId = testProductId, quantity = 1)
        val existingCartEntity = testCartEntity.copy(
            products = listOf(existingProductData)
        )
        
        coEvery { cartDao.getUserCart(testUserId) } returns existingCartEntity
        coEvery { cartDao.insertCart(any()) } returns 1L
        coEvery { cartDao.cleanupOldCarts(testUserId) } returns Unit
        
        val updateCartSlot = slot<Cart>()
        coEvery { cartApi.updateCart(any(), capture(updateCartSlot)) } answers {
            val cart = updateCartSlot.captured ?: testCart
            cart.copy(id = cart.id)
        }

        val result = repository.addToCart(testUserId, testProductId, testQuantity)

        assertTrue(result is Resource.Success)
        assertNotNull(result.data)
        assertEquals(testCartId, result.data!!.id)
        assertEquals(testUserId, result.data!!.userId)
        assertEquals(getCurrentDate(), result.data!!.date)
        assertEquals(1, result.data!!.products.size)
        assertEquals(testProductId, result.data!!.products[0].productId)
        assertEquals(1 + testQuantity, result.data!!.products[0].quantity)

        coVerify(exactly = 1) { cartDao.getUserCart(testUserId) }
        coVerify(exactly = 1) { cartDao.insertCart(any()) }
        coVerify(exactly = 1) { cartDao.cleanupOldCarts(testUserId) }
        coVerify(exactly = 1) { cartApi.updateCart(any(), any()) }
        coVerify(exactly = 0) { cartApi.addToCart(any()) }
    }

    @Test
    fun `addToCart adds new product to existing cart`() = runBlocking {
        val newProductId = 99
        val newProduct = testProduct.copy(id = newProductId)
        
        coEvery { cartDao.getUserCart(testUserId) } returns testCartEntity
        coEvery { cartDao.insertCart(any()) } returns 1L
        coEvery { cartDao.cleanupOldCarts(testUserId) } returns Unit
        coEvery { productApi.getProductById(newProductId) } returns newProduct
        
        val updateCartSlot = slot<Cart>()
        coEvery { cartApi.updateCart(any(), capture(updateCartSlot)) } answers {
            val cart = updateCartSlot.captured ?: testCart
            cart.copy(id = cart.id)
        }

        val result = repository.addToCart(testUserId, newProductId, testQuantity)

        assertTrue(result is Resource.Success)
        assertNotNull(result.data)
        assertEquals(testCartId, result.data!!.id)
        assertEquals(testUserId, result.data!!.userId)
        assertEquals(getCurrentDate(), result.data!!.date)
        assertEquals(2, result.data!!.products.size)
        
        assertEquals(testProductId, result.data!!.products[0].productId)
        assertEquals(testQuantity, result.data!!.products[0].quantity)
        assertEquals(newProductId, result.data!!.products[1].productId)
        assertEquals(testQuantity, result.data!!.products[1].quantity)

        coVerify(exactly = 1) { cartDao.insertCart(any()) }
        coVerify(exactly = 1) { cartApi.updateCart(any(), any()) }
    }

    @Test
    fun `addToCart returns Resource Error when API throws exception`() = runBlocking {
        val errorMessage = "Failed to add item to cart"
        coEvery { cartDao.getUserCart(testUserId) } returns null
        coEvery { cartDao.insertCart(any()) } returns 0L
        coEvery { cartApi.addToCart(any()) } throws Exception(errorMessage)

        val result = repository.addToCart(testUserId, testProductId, testQuantity)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `updateCart updates both cache and API`() = runBlocking {
        coEvery { cartDao.insertCart(any()) } returns 1L
        coEvery { cartApi.updateCart(testCartId, testCart) } returns testCart

        val result = repository.updateCart(testCartId, testCart)

        assertTrue(result is Resource.Success)
        assertEquals(testCart, result.data)

        coVerify(exactly = 1) { cartDao.insertCart(any()) }
        coVerify(exactly = 1) { cartApi.updateCart(testCartId, testCart) }
    }

    @Test
    fun `updateCart returns Resource Error when API throws exception`() = runBlocking {
        val errorMessage = "Failed to update cart"
        coEvery { cartDao.insertCart(any()) } returns 1L
        coEvery { cartApi.updateCart(testCartId, testCart) } throws Exception(errorMessage)

        val result = repository.updateCart(testCartId, testCart)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `deleteCart removes cart from both cache and API`() = runBlocking {
        coEvery { cartDao.deleteCartById(testCartId) } returns Unit
        coEvery { cartApi.deleteCart(testCartId) } returns testCart

        val result = repository.deleteCart(testCartId)

        assertTrue(result is Resource.Success)
        assertEquals(testCart, result.data)

        coVerify(exactly = 1) { cartDao.deleteCartById(testCartId) }
        coVerify(exactly = 1) { cartApi.deleteCart(testCartId) }
    }

    @Test
    fun `deleteCart returns Resource Error when API throws exception`() = runBlocking {
        val errorMessage = "Failed to delete cart"
        coEvery { cartDao.deleteCartById(testCartId) } returns Unit
        coEvery { cartApi.deleteCart(testCartId) } throws Exception(errorMessage)

        val result = repository.deleteCart(testCartId)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `getCurrentDate returns today's date in correct format`() = runBlocking {
        // Using reflection to access private method
        val method = repository::class.java.getDeclaredMethod("getCurrentDate")
        method.isAccessible = true
        val result = method.invoke(repository) as String
        
        // Verify format is YYYY-MM-DD using regex
        assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `isCacheValid returns true for fresh cache`() = runBlocking {
        val now = System.currentTimeMillis()
        val freshCache = testCartEntity.copy(lastUpdated = now - 5 * 60 * 1000) // 5 minutes ago

        // Using reflection to access private method
        val method = repository::class.java.getDeclaredMethod("isCacheValid", CartEntity::class.java)
        method.isAccessible = true
        val result = method.invoke(repository, freshCache) as Boolean

        assertTrue(result)
    }

    @Test
    fun `isCacheValid returns false for stale cache`() = runBlocking {
        val now = System.currentTimeMillis()
        val staleCache = testCartEntity.copy(lastUpdated = now - 15 * 60 * 1000) // 15 minutes ago

        // Using reflection to access private method
        val method = repository::class.java.getDeclaredMethod("isCacheValid", CartEntity::class.java)
        method.isAccessible = true
        val result = method.invoke(repository, staleCache) as Boolean

        assertFalse(result)
    }

    @Test
    fun `isCacheValid returns false for null cache`() = runBlocking {
        // Using reflection to access private method
        val method = repository::class.java.getDeclaredMethod("isCacheValid", CartEntity::class.java)
        method.isAccessible = true
        val result = method.invoke(repository, null) as Boolean

        assertFalse(result)
    }
}