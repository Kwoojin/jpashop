package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 * 1. Entity -> DTO 변환
 * 2. 필요 시 Fetch Join 성능 최적화 ( 대부분 성능 이슈 해결 )
 * 3. 더 필요하다면 DTO로 조회하는 방법 사용
 * 4. 최후의 방법 JPA(Native SQL) 또는 SQL Mapper
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * 엔티티를 직접 노출할 경우, 한 곳은 @JsonIgnore 처리 필수(무한 루프 발생함)
     * Hibernate5Module 사용하기 보단 DTO 사용
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        all.stream().forEach(order -> {
            order.getMember().getName();
            order.getDelivery().getStatus();
        }); // Lazy 강제 초기화
        return all;
    }

    /**
     * Entity -> DTO
     * N+1 문제
     * 해당 예제 ) 1 + N + N
     */
    @GetMapping("/api/v2/simple-orders")
    public Result orderV2() {
        List<SimpleOrderDto> result = orderRepository.findAll(new OrderSearch())
                .stream()
                .map(SimpleOrderDto::createSimpleOrderDto)
                .collect(Collectors.toList());

        return Result.builder().data(result).build();
    }

    /**
     * fetch join 으로 인한 성능 향상
     */
    @GetMapping("/api/v3/simple-orders")
    public Result orderV3() {
        List<SimpleOrderDto> result = orderRepository.findAllWithMemberDelivery()
                .stream()
                .map(SimpleOrderDto::createSimpleOrderDto)
                .collect(Collectors.toList());

        return Result.builder().data(result).build();
    }

    /**
     * select 간소화(성능 최적화)
     * 재사용성 x
     * [ v3 <-> v4 ]  간 트레이드 오프
     */
    @GetMapping("/api/v4/simple-orders")
    public Result orderV4() {
        List<OrderSimpleQueryDto> result = orderSimpleQueryRepository.findOrderDtos();
        return Result.builder().data(result).build();
    }

    @Data
    @NoArgsConstructor
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public static SimpleOrderDto createSimpleOrderDto(Order o) {
            SimpleOrderDto dto = new SimpleOrderDto();
            dto.orderId = o.getId();
            dto.name = o.getMember().getName();
            dto.orderDate = o.getOrderDate();
            dto.orderStatus = o.getStatus();
            dto.address = o.getDelivery().getAddress();
            return dto;

        }
    }

    @Data
    @Builder
    static class Result<T> {
        private int count;
        private T data;
    }
}
