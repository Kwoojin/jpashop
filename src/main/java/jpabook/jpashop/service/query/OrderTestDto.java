package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class OrderTestDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemTestDto> orderItems;

    public static OrderTestDto createOrderTestDto(Order o) {
        OrderTestDto dto = new OrderTestDto();
        dto.orderId = o.getId();
        dto.name = o.getMember().getName();
        dto.orderDate = o.getOrderDate();
        dto.orderStatus = o.getStatus();
        dto.address = o.getDelivery().getAddress();
        dto.orderItems = o.getOrderItems().stream()
                .map(OrderItemTestDto::createOrderItemTestDto)
                .collect(toList());

        return dto;
    }
}
