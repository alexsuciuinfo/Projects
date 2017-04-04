package suciu.alexandru.com.bookwormscommunity.models;

/**
 * Created by Alexandru on 21.05.2016.
 */
public class BookReview {
    User user;
    Review review;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
