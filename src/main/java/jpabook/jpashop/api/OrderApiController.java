package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.OrderRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        all.stream().forEach(o -> {
            o.getMember().getName();
            o.getDelivery().getAddress();
            o.getOrderItems().stream().forEach(oi -> oi.getItem().getName());
        });
        return all;
    }

    @GetMapping("/api/v2/orders")
    public Result orderV2() {
        return new Result<>(orderRepository.findAll(new OrderSearch())
                .stream()
                .map(OrderDto::createOrderDto)
                .collect(toList()));
    }

    /**
     * 1:N 조회 시 Fetch Join을 사용할 경우에
     * 페이징 불가능
     * 페이징 사용할 경우 메모리를 사용하여 페이징 처리가 적용이 됨
     */
    @GetMapping("/api/v3/orders")
    public Result orderV3() {
        return new Result<>(orderRepository.findAllWithItem()
                .stream()
                .map(OrderDto::createOrderDto)
                .collect(toList()));
    }


    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public static OrderDto createOrderDto(Order o) {
            OrderDto dto = new OrderDto();
            dto.orderId = o.getId();
            dto.name = o.getMember().getName();
            dto.orderDate = o.getOrderDate();
            dto.orderStatus = o.getStatus();
            dto.address = o.getDelivery().getAddress();
            dto.orderItems = o.getOrderItems().stream()
                    .map(OrderItemDto::createOrderItemDto)
                    .collect(toList());

            return dto;
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public static OrderItemDto createOrderItemDto(OrderItem orderItem) {
            OrderItemDto dto = new OrderItemDto();
            dto.itemName = orderItem.getItem().getName();
            dto.orderPrice = orderItem.getOrderPrice();
            dto.count = orderItem.getCount();
            return dto;
        }
    }

    @Getter
    static class Result<T> {
        private T data;

        public Result(T data) {
            this.data = data;
        }
    }





}
