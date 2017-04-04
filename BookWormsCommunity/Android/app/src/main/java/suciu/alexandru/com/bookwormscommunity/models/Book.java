package suciu.alexandru.com.bookwormscommunity.models;

/**
 * Created by Alexandru on 13.05.2016.
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private double rating;
    private String imageURL;
    private String category;
    private String nrRatings;
    private String nrRead;
    private String date;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Book(int id, String title, String author, double rating, String imageURL, String category, String nrRatings) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.imageURL = imageURL;
        this.category = category;
        this.nrRatings = nrRatings;
    }

    public String getNrRead() {
        return nrRead;
    }

    public void setNrRead(String nrRead) {
        this.nrRead = nrRead;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNrRatings() {
        return nrRatings;
    }

    public void setNrRatings(String nrRatings) {
        this.nrRatings = nrRatings;
    }
}
