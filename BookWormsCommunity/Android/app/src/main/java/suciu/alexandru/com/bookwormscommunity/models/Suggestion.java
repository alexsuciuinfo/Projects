package suciu.alexandru.com.bookwormscommunity.models;

import suciu.alexandru.com.bookwormscommunity.models.Book;

/**
 * Created by Alexandru on 20.05.2016.
 */
public class Suggestion {


    private String message;
    private Book bookRead;
    private Book bookSuggestion;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Book getBookRead() {
        return bookRead;
    }

    public void setBookRead(Book bookRead) {
        this.bookRead = bookRead;
    }

    public Book getBookSuggestion() {
        return bookSuggestion;
    }

    public void setBookSuggestion(Book bookSuggestion) {
        this.bookSuggestion = bookSuggestion;
    }
}
