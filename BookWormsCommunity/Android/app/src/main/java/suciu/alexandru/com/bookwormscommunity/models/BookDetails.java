package suciu.alexandru.com.bookwormscommunity.models;

import suciu.alexandru.com.bookwormscommunity.models.Book;

/**
 * Created by Alexandru on 21.05.2016.
 */
public class BookDetails {
    Book book;
    int wantBook, readBook;
    double myRating;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getWantBook() {
        return wantBook;
    }

    public void setWantBook(int wantBook) {
        this.wantBook = wantBook;
    }

    public int getReadBook() {
        return readBook;
    }

    public void setReadBook(int readBook) {
        this.readBook = readBook;
    }

    public double getMyRating() {
        return myRating;
    }

    public void setMyRating(double myRating) {
        this.myRating = myRating;
    }
}
