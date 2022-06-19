package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemTestDto {

    private String itemName;
    private int orderPrice;
    private int count;

    public static OrderItemTestDto createOrderItemTestDto(OrderItem orderItem) {
        OrderItemTestDto dto = new OrderItemTestDto();
        dto.itemName = orderItem.getItem().getName();
        dto.orderPrice = orderItem.getOrderPrice();
        dto.count = orderItem.getCount();
        return dto;
    }
}
