package suciu.alexandru.com.bookwormscommunity.models;

import java.sql.Date;

/**
 * Created by Alexandru on 21.05.2016.
 */
public class Review {
    String message;
    String date;
    int bookId;
    double rating;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
