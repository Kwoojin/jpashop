package jpabook.jpashop.service;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
