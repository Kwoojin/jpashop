package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 화면이나 API 에 맞춘 서비스
 * OSIV = false 위함
 * OrderService  - 핵심 비즈니스 로직
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QueryOrderService {

    private final OrderRepository orderRepository;

    public List<OrderTestDto> findAllWithItem() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderTestDto> result = orders.stream()
                .map(OrderTestDto::createOrderTestDto)
                .collect(toList());

        return result;
    }

}
