package model.mapper

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.dto.UserDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.User
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.mapper.OrderMapper
import com.buoyancy.common.model.mapper.ProductMapper
import com.buoyancy.common.model.mapper.UserMapper
import com.buoyancy.order.OrderServiceApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
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
    lateinit var productMapper: ProductMapper
    @Autowired
    lateinit var userMapper: UserMapper

    @MockitoBean
    lateinit var userRepository: JpaRepository<User, UUID>
    @MockitoBean
    lateinit var productRepository: JpaRepository<Product, UUID>

    @Test
    fun `should map ProductDto to Product`() {
        // Given
        val productDto = ProductDto(UUID.randomUUID(), "Pickles", 1099)

        // When
        val product = productMapper.toEntity(productDto)

        // Then
        assertEquals(productDto.name, product.name)
        assertEquals(productDto.price, product.price)
        assertEquals(productDto.id, product.id)
    }

    @Test
    fun `should map Product to ProductDto`() {
        // Given
        val product = Product(UUID.randomUUID(), "Pickles", 1099)

        // When
        val productDto = productMapper.toDto(product)

        // Then
        assertEquals(productDto.name, product.name)
        assertEquals(productDto.price, product.price)
        assertEquals(productDto.id, product.id)
    }

    private val userId = UUID.randomUUID()
    private val user = User(userId, "A", "B", "cat@mouse.com")
    private val userDto = UserDto(userId, "A", "B", "cat@mouse.com")
    private final val productIds = listOf(UUID.randomUUID(), UUID.randomUUID())
    private val product1 = Product(productIds[0], "Pickles", 1099)
    private val product2 = Product(productIds[1], "Cheese", 1590)
    private val orderItems = listOf(product1, product2)

    @Test
    fun `should map OrderDto to Order`() {
        // Given
        val orderDto = OrderDto(UUID.randomUUID(), userDto.id, LocalDateTime.now(), OrderStatus.READY, productIds)
        Mockito.`when`(userRepository.getReferenceById(any(UUID::class.java))).thenReturn(user)
        Mockito.`when`(productRepository.getReferenceById(productIds[0])).thenReturn(product1)
        Mockito.`when`(productRepository.getReferenceById(productIds[1])).thenReturn(product2)

        // When
        val order = orderMapper.toEntity(orderDto)

        // Then
        assertEquals(orderDto.id, order.id)
        assertEquals(orderDto.status, order.status)
        assertTrue(order.items.isNotEmpty())
        assertEquals(order.items[0].id, productIds[0])
        assertEquals(order.items[1].id, productIds[1])
        assertEquals(order.user.id, userDto.id)
        Mockito.verify(productRepository).getReferenceById(productIds[0])
        Mockito.verify(productRepository).getReferenceById(productIds[1])
        Mockito.verify(userRepository).getReferenceById(userId)
    }

    @Test
    fun `should map Order to OrderDto`() {
        // Given
        val order = Order(UUID.randomUUID(), user, LocalDateTime.now(), OrderStatus.READY, orderItems)

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
}