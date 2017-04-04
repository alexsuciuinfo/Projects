package suciu.alexandru.com.bookwormscommunity.models;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Alexandru on 11.05.2016.
 */
public class User {
    private String userId;
    private String apiKey;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String photo;
    private String birthdate;
    private String location, about, gender;
    private int nrReadBooks, nrWantedBooks, nrRatings, nrReviews, nrBooks;
    private String encryptedPhoto;

    public int getNrBooks() {
        return  nrBooks;
    }

    public void setNrBooks(int nrBooks){
        this.nrBooks = nrBooks;
    }

    public String getEncryptedPhoto() {
        return encryptedPhoto;
    }

    public void setEncryptedPhoto(String encryptedPhoto) {
        this.encryptedPhoto = encryptedPhoto;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getNrReviews() {
        return nrReviews;
    }

    public void setNrReviews(int nrReviews) {
        this.nrReviews = nrReviews;
    }

    public int getNrWantedBooks() {
        return nrWantedBooks;
    }

    public void setNrWantedBooks(int nrWantedBooks) {
        this.nrWantedBooks = nrWantedBooks;
    }

    public int getNrRatings() {
        return nrRatings;
    }

    public void setNrRatings(int nrRatings) {
        this.nrRatings = nrRatings;
    }

    public int getNrReadBooks() {
        return nrReadBooks;
    }

    public void setNrReadBooks(int nrReadBooks) {
        this.nrReadBooks = nrReadBooks;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
