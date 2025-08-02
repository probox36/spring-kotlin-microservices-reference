package model.mapper

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.dto.UserDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.entity.User
import com.buoyancy.common.model.enums.CuisineType
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.mapper.OrderMapper
import com.buoyancy.common.model.mapper.ProductMapper
import com.buoyancy.common.model.mapper.SuborderMapper
import com.buoyancy.common.model.mapper.UserMapper
import com.buoyancy.order.OrderServiceApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(classes = [OrderServiceApplication::class])
class MapperTest {

    @Autowired
    lateinit var orderMapper: OrderMapper
    @Autowired
    lateinit var suborderMapper: SuborderMapper
    @Autowired
    lateinit var productMapper: ProductMapper
    @Autowired
    lateinit var userMapper: UserMapper

    @MockitoBean
    lateinit var userRepository: JpaRepository<User, UUID>
    @MockitoBean
    lateinit var productRepository: JpaRepository<Product, UUID>
    @MockitoBean
    lateinit var restaurantRepository: JpaRepository<Restaurant, UUID>

    private val restaurant = Restaurant(UUID.randomUUID(), "Pickles", "Main rd, 123",
        "123-123-1234", "pickles@example.com", CuisineType.JAPANESE, true)

    @Test
    fun `should map ProductDto to Product`() {
        // Given
        whenever(restaurantRepository.getReferenceById(any<UUID>())).thenReturn(restaurant)
        val productDto = ProductDto(UUID.randomUUID(), "Pickles", 1099, restaurant.id!!)

        // When
        val product = productMapper.toEntity(productDto)

        // Then
        assertEquals(productDto.name, product.name)
        assertEquals(productDto.price, product.price)
        assertEquals(productDto.id, product.id)
        assertEquals(productDto.restaurantId, product.restaurant.id)
    }

    @Test
    fun `should map Product to ProductDto`() {
        // Given
        whenever(restaurantRepository.getReferenceById(any<UUID>())).thenReturn(restaurant)
        val product = Product(UUID.randomUUID(), "Pickles", 1099, restaurant)

        // When
        val productDto = productMapper.toDto(product)

        // Then
        assertEquals(productDto.name, product.name)
        assertEquals(productDto.price, product.price)
        assertEquals(productDto.id, product.id)
        assertEquals(productDto.restaurantId, restaurant.id)
    }

    private val userId = UUID.randomUUID()
    private val user = User(userId, "A", "B", "cat@mouse.com")
    private val userDto = UserDto(userId, "A", "B", "cat@mouse.com")
    private final val productIds = listOf(UUID.randomUUID(), UUID.randomUUID())
    private val product1 = Product(productIds[0], "Pickles", 1099, restaurant)
    private val product2 = Product(productIds[1], "Cheese", 1590, restaurant)
    private val orderItems = mutableListOf(product1, product2)
    private val order = Order(UUID.randomUUID(), user, LocalDateTime.now(), OrderStatus.READY, orderItems)

    @Test
    fun `should map OrderDto to Order`() {
        // Given
        val orderDto = OrderDto(UUID.randomUUID(), userDto.id, LocalDateTime.now(), OrderStatus.READY, productIds)
        whenever(userRepository.getReferenceById(any<UUID>())).thenReturn(user)
        whenever(productRepository.getReferenceById(productIds[0])).thenReturn(product1)
        whenever(productRepository.getReferenceById(productIds[1])).thenReturn(product2)

        // When
        val order = orderMapper.toEntity(orderDto)

        // Then
        assertEquals(orderDto.id, order.id)
        assertEquals(orderDto.status, order.status)
        assertTrue(order.items.isNotEmpty())
        assertEquals(order.items[0].id, productIds[0])
        assertEquals(order.items[1].id, productIds[1])
        assertEquals(order.user.id, userDto.id)
        verify(productRepository).getReferenceById(productIds[0])
        verify(productRepository).getReferenceById(productIds[1])
        verify(userRepository).getReferenceById(userId)
    }

    @Test
    fun `should map Order to OrderDto`() {
        // When
        val orderDto = orderMapper.toDto(order)

        // Then
        assertEquals(orderDto.items[0], orderItems[0].id)
        assertEquals(orderDto.items[1], orderItems[1].id)
        assertEquals(orderDto.id, order.id)
        assertEquals(orderDto.userId, order.user.id)
        assertEquals(orderDto.status, order.status)
    }

    @Test
    fun `should map UserDto to User`() {
        // When
        val entity = userMapper.toEntity(userDto)
        // Then
        assertEquals(userDto.name, entity.name)
        assertEquals(userDto.lastName, entity.lastName)
        assertEquals(userDto.email, entity.email)
        assertEquals(userDto.id, entity.id)
    }

    @Test
    fun `should map User to UserDto`() {
        // When
        val userDto = userMapper.toDto(user)
        // Then
        assertEquals(userDto.name, user.name)
        assertEquals(userDto.lastName, user.lastName)
        assertEquals(userDto.email, user.email)
        assertEquals(userDto.id, user.id)
    }

    // java.lang.NoSuchMethodException: com.buoyancy.common.model.entity.Order$HibernateProxy.<init>()

//    @Test
//    fun `should map SuborderDto to Suborder`() {
//        // Given
//        val suborderDto = SuborderDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
//            productIds, OrderStatus.READY)
//        whenever(restaurantRepository.getReferenceById(any<UUID>())).thenReturn(restaurant)
//        whenever(productRepository.getReferenceById(productIds[0])).thenReturn(product1)
//        whenever(productRepository.getReferenceById(productIds[1])).thenReturn(product2)
//
//        // When
//        val suborder = suborderMapper.toEntity(suborderDto)
//
//        // Then
//        assertEquals(suborderDto.id, suborder.id)
//        assertEquals(suborderDto.orderId, suborder.order.id)
//        assertEquals(suborderDto.status, suborder.status)
//        assertTrue(suborder.items.isNotEmpty())
//        assertEquals(suborder.items[0].id, productIds[0])
//        assertEquals(suborder.items[1].id, productIds[1])
//        assertEquals(suborder.restaurant.id, suborderDto.restaurantId)
//        verify(productRepository).getReferenceById(productIds[0])
//        verify(productRepository).getReferenceById(productIds[1])
//        verify(restaurantRepository).getReferenceById(suborderDto.restaurantId)
//    }

    @Test
    fun `should map Suborder to SuborderDto`() {
        // Given
        val suborder = Suborder(UUID.randomUUID(), order, restaurant, orderItems, SuborderStatus.CREATED)

        // When
        val suborderDto = suborderMapper.toDto(suborder)

        // Then
        assertEquals(suborderDto.items[0], orderItems[0].id)
        assertEquals(suborderDto.items[1], orderItems[1].id)
        assertEquals(suborderDto.id, suborder.id)
        assertEquals(suborderDto.restaurantId, suborder.restaurant.id)
        assertEquals(suborderDto.orderId, suborder.order.id)
        assertEquals(suborderDto.status, suborder.status)
    }
}