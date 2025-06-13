package model.mapper

import com.buoyancy.OrderServiceApplication
import com.buoyancy.model.dto.OrderDto
import com.buoyancy.model.dto.ProductDto
import com.buoyancy.model.entity.Order
import com.buoyancy.model.entity.OrderStatus
import com.buoyancy.model.entity.Product
import com.buoyancy.model.mapper.OrderMapper
import com.buoyancy.model.mapper.ProductMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest(classes = [OrderServiceApplication::class])
class MapperTest {

    @Autowired
    lateinit var orderMapper: OrderMapper
    @Autowired
    lateinit var productMapper: ProductMapper

    @Test
    fun `should map OrderItemDto to OrderItem`() {
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
    fun `should map OrderItem to OrderItemDto`() {
        // Given
        val product = Product(UUID.randomUUID(), "Pickles", 1099)

        // When
        val productDto = productMapper.toDto(product)

        // Then
        assertEquals(productDto.name, product.name)
        assertEquals(productDto.price, product.price)
        assertEquals(productDto.id, product.id)
    }


    private val item1 = Product(UUID.randomUUID(), "Pickles", 1099)
    private val item2 = Product(UUID.randomUUID(), "Cheese", 1590)
    private val dto1 = ProductDto(UUID.randomUUID(), "Pickles", 1099)
    private val dto2 = ProductDto(UUID.randomUUID(), "Cheese", 1590)
    private val orderItems = listOf(item1, item2)
    private val orderItemDtos = listOf(dto1, dto2)
    @Test
    fun `should map OrderDto to Order`() {
        // Given
        val productDtos = listOf(ProductDto(UUID.randomUUID(), "Pickles", 1099), ProductDto(UUID.randomUUID(), "Cheese", 1590))
        val orderDto = OrderDto(UUID.randomUUID(), UUID.randomUUID(), OrderStatus.READY, productDtos)

        // When
        val order = orderMapper.toEntity(orderDto)

        // Then
        assertEquals(orderDto.items[0].name, orderItems[0].name)
        assertEquals(orderDto.items[1].name, orderItems[1].name)
        assertEquals(orderDto.id, order.id)
        assertEquals(orderDto.userId, order.userId)
        assertEquals(orderDto.status, order.status)
    }

    @Test
    fun `should map Order to OrderDto`() {
        // Given
        val order = Order(UUID.randomUUID(), UUID.randomUUID(), OrderStatus.READY, orderItems)

        // When
        val orderDto = orderMapper.toDto(order)

        // Then
        assertEquals(orderDto.items[0].name, orderItemDtos[0].name)
        assertEquals(orderDto.items[1].name, orderItemDtos[1].name)
        assertEquals(orderDto.id, order.id)
        assertEquals(orderDto.userId, order.userId)
        assertEquals(orderDto.status, order.status)
    }
}