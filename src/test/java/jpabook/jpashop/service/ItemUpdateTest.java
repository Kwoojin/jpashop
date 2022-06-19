package jpabook.jpashop.service;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    private @Autowired EntityManager em;

    @Test
    public void updateTest() {
//        BookTest book = em.find(BookTest.class, 1L);
//        book.setName("JPA2");
    }

    @Entity
    @DiscriminatorValue("B")
    @Getter @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    static class BookTest extends ItemTest {

        private String author;
        private String isbn;

        public BookTest(String name, int price, int stockQuantity, String author, String isbn) {
            super(name, price, stockQuantity);
            this.author = author;
            this.isbn = isbn;
        }

        public BookTest(Long id, String name, int price, int stockQuantity, String author, String isbn) {
            super(id, name, price, stockQuantity);
            this.author = author;
            this.isbn = isbn;
        }
    }

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "dtype")
    @Getter @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    static abstract class ItemTest {

        @Id @GeneratedValue
        @Column(name = "item_id")
        private Long id;

        private String name;
        private int price;
        private int stockQuantity;

        public ItemTest(String name, int price, int stockQuantity){
            this.name = name;
            this.price = price;
            this.stockQuantity = stockQuantity;
        }

        public ItemTest(Long id, String name, int price, int stockQuantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stockQuantity = stockQuantity;
        }

        @ManyToMany(mappedBy = "items")
        private List<Category> categories = new ArrayList<>();

        //==비즈니스 로직==//

        /**
         * stock 증가
         */
        public void addStock(int quantity) {
            this.stockQuantity += quantity;
        }

        /**
         * stock 감소
         */
        public void removeStock(int quantity) {
            int restStock = this.stockQuantity - quantity;
            if(restStock < 0){
                throw new NotEnoughStockException("need more stock");
            }
            this.stockQuantity = restStock;
        }
    }
}
