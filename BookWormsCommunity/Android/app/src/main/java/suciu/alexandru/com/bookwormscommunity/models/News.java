package suciu.alexandru.com.bookwormscommunity.models;

import suciu.alexandru.com.bookwormscommunity.models.Book;

/**
 * Created by Alexandru on 20.05.2016.
 */
public class News {
    private String message;
    private Book book;
    private String thisWeekReaders;
    private String thisWeekRatings;
    private String thisWeek;

    public String getThisWeekReaders() {
        return thisWeekReaders;
    }

    public void setThisWeekReaders(String thisWeekReaders) {
        this.thisWeekReaders = thisWeekReaders;
    }

    public String getThisWeekRatings() {
        return thisWeekRatings;
    }

    public void setThisWeekRatings(String thisWeekRatings) {
        this.thisWeekRatings = thisWeekRatings;
    }

    public String getThisWeek() {
        return thisWeek;
    }

    public void setThisWeekRead(String thisWeekRead) {
        this.thisWeek = thisWeekRead;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
