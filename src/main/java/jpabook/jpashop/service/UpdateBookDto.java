package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class UpdateBookDto {

    private String name;
    private int price;
    private int stockQuantity;
    private String author;
    private String isbn;

    public static UpdateBookDto updateBookAll(
            String name, int price, int stockQuantity,
            String author, String isbn) {
        UpdateBookDto bookDto = new UpdateBookDto();
        bookDto.name = name;
        bookDto.price = price;
        bookDto.stockQuantity = stockQuantity;
        bookDto.author = author;
        bookDto.isbn = isbn;
        return bookDto;
    }
}
