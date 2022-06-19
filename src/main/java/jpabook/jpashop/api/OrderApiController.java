package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.QueryOrderService;
import jpabook.jpashop.service.query.OrderTestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    private final QueryOrderService queryOrderService;

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
     * 컬렉션 페치 조인은 1개만 사용
     */
    @GetMapping("/api/v3/orders")
    public Result orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(OrderDto::createOrderDto)
                .collect(toList());

        return new Result<>(result);
    }

    /**
     * orderV3() 에서
     * open-in-view false 로 수정 시
     */
    @GetMapping("/api/v3/orders-oiv")
    public Result orderV3_oiv() {
        List<OrderTestDto> result = queryOrderService.findAllWithItem();
        return new Result<>(result);
    }

    @GetMapping("/api/v3.1/orders")
    public Result orderV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<OrderDto> orders = orderRepository.findAllWithMemberDelivery(offset, limit)
                .stream()
                .map(OrderDto::createOrderDto)
                .collect(toList());

        return new Result<>(orders);
    }

    /**
     * 데이터 조회 결과가 1건일 경우 유리
     */
    @GetMapping("/api/v4/orders")
    public Result orderV4() {
        List<OrderQueryDto> orders = orderQueryRepository.findOrderQueryDtos();
        return new Result<>(orders);
    }

    /**
     * Query : root 1번, 컬렉션 1번
     * xToOne : join
     * xToMany : xToOne 에서 얻은 식별자를 통해 MAP을 이용하여  성능 향상
     * 페이징 가능
     */
    @GetMapping("/api/v5/orders")
    public Result orderV5() {
        List<OrderQueryDto> orders = orderQueryRepository.findAllByDto_optimization();
        return new Result<>(orders);
    }

    /**
     * 장점 : Query 1번 실행
     * 단점 :
     * 1.DB에서 애플리케이션에 중복 데이터가 전달되어 느릴 수 있음
     * 2.애플리케이션에서 추가 작업이 크며, 페이징 불가능
     */
    @GetMapping("/api/v6/orders")
    public Result orderV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        List<OrderQueryDto> dtos = flats.stream()
                .collect(groupingBy(
                        o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(),
                                o.getOrderDate(),
                                o.getOrderStatus(),
                                o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                        o.getItemName(),
                                        o.getOrderPrice(),
                                        o.getCount()),
                                toList()
                        )
                ))
                .entrySet().stream()
                .map(e -> new OrderQueryDto(
                        e.getKey().getOrderId(),
                        e.getKey().getName(),
                        e.getKey().getOrderDate(),
                        e.getKey().getOrderStatus(),
                        e.getKey().getAddress(),
                        e.getValue()
                ))
                .collect(toList());

        return new Result<>(dtos);
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
