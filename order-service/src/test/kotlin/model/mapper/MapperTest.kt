package model.mapper

import com.buoyancy.model.dto.OrderDto
import com.buoyancy.model.dto.OrderItemDto
import com.buoyancy.model.entity.Order
import com.buoyancy.model.entity.OrderItem
import com.buoyancy.model.entity.OrderStatus
import com.buoyancy.model.mapper.OrderItemMapper
import com.buoyancy.model.mapper.OrderMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*

class MapperTest {

    private val orderMapper: OrderMapper = Mappers.getMapper(OrderMapper::class.java)
    private val orderItemMapper: OrderItemMapper = Mappers.getMapper(OrderItemMapper::class.java)

    @Test
    fun `should map OrderItemDto to OrderItem`() {
        // Given
        val orderItemDto = OrderItemDto(UUID.randomUUID(), "Pickles", 1099)

        // When
        val orderItem = orderItemMapper.toEntity(orderItemDto)

        // Then
        assertEquals(orderItemDto.name, orderItem.name)
        assertEquals(orderItemDto.price, orderItem.price)
        assertEquals(orderItemDto.id, orderItem.id)
    }

    @Test
    fun `should map OrderItem to OrderItemDto`() {
        // Given
        val orderItem = OrderItem(UUID.randomUUID(), "Pickles", 1099)

        // When
        val orderItemDto = orderItemMapper.toDto(orderItem)

        // Then
        assertEquals(orderItemDto.name, orderItem.name)
        assertEquals(orderItemDto.price, orderItem.price)
        assertEquals(orderItemDto.id, orderItem.id)
    }


    private val item1 = OrderItem(UUID.randomUUID(), "Pickles", 1099)
    private val item2 = OrderItem(UUID.randomUUID(), "Cheese", 1590)
    private val dto1 = OrderItemDto(UUID.randomUUID(), "Pickles", 1099)
    private val dto2 = OrderItemDto(UUID.randomUUID(), "Cheese", 1590)
    private val orderItems = listOf(item1, item2)
    private val orderItemDtos = listOf(dto1, dto2)
    @Test
    fun `should map OrderDto to Order`() {
        // Given
        val orderItemDtos = listOf(OrderItemDto(UUID.randomUUID(), "Pickles", 1099), OrderItemDto(UUID.randomUUID(), "Cheese", 1590))
        val orderDto = OrderDto(UUID.randomUUID(), UUID.randomUUID(), OrderStatus.READY, orderItemDtos)

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