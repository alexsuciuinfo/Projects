package suciu.alexandru.com.bookwormscommunity.models;

import java.util.List;

import suciu.alexandru.com.bookwormscommunity.models.Book;

/**
 * Created by Alexandru on 13.06.2016.
 */
public class BookCategories {
    String category;
    List<Book> bookList;

    public BookCategories(String category, List<Book> book) {
        this.category = category;
        this.bookList = book;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> book) {
        this.bookList = book;
    }
}
