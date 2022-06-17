package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.UpdateBookDto;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/new")
    public String create(BookForm bookForm) {
        Book book = new Book(
                bookForm.getName(),
                bookForm.getPrice(),
                bookForm.getStockQuantity(),
                bookForm.getAuthor(),
                bookForm.getIsbn()
        );

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findItemOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    /**
     * form의 Id는 조작되어 넘어올 수 있기 때문에,
     * 권한을 체크할 수 있는 방법이 있어야함
     */
    @PostMapping("/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {
        UpdateBookDto bookDto = UpdateBookDto.updateBookAll(
                form.getName(),
                form.getPrice(),
                form.getStockQuantity(),
                form.getAuthor(),
                form.getIsbn()
        );

        itemService.updateItem(itemId, bookDto);
        return "redirect:/items";
    }
}



