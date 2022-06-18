package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;


/**
 * 총 2주문 2개
 * userA
 *  - JPA1 BOOK
 *  - JPA2 BOOK
 * userB
 *  - SPRING1 BOOK
 *  - SPRING2 BOOK
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;


    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, "김영한", "11-11-11", 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, "김영한", "22-22-22", 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = Delivery.builder()
                    .address(member.getAddress())
                    .status(DeliveryStatus.READY)
                    .build();
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", "김포", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, "토비", "33-33-33", 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, "토비", "44-44-44", 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = Delivery.builder()
                    .address(member.getAddress())
                    .status(DeliveryStatus.READY)
                    .build();
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = Member.builder()
                    .name(name)
                    .address(new Address(city, street, zipcode))
                    .build();
            return member;
        }

        private Book createBook(String name, int price, String author, String isbn, int stockQuantity) {
            Book book = Book.builder()
                    .name(name)
                    .price(price)
                    .stockQuantity(stockQuantity)
                    .author(author)
                    .isbn(isbn)
                    .build();
            return book;
        }


    }

}
