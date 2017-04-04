package suciu.alexandru.com.bookwormscommunity.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.models.Book;
import suciu.alexandru.com.bookwormscommunity.models.BookDetails;
import suciu.alexandru.com.bookwormscommunity.models.BookReview;
import suciu.alexandru.com.bookwormscommunity.models.LoginRegisterModel;
import suciu.alexandru.com.bookwormscommunity.models.News;
import suciu.alexandru.com.bookwormscommunity.models.Review;
import suciu.alexandru.com.bookwormscommunity.models.Suggestion;
import suciu.alexandru.com.bookwormscommunity.models.User;

/**
 * Created by Alexandru on 11.05.2016.
 */
public interface ApiInterface {

    @FormUrlEncoded
    @POST("/login")
    Call<LoginRegisterModel> Login(
            @Field(Constants.USERNAME) String username,
            @Field(Constants.PASSWORD) String password
    );

    @FormUrlEncoded
    @POST("/register")
    Call<LoginRegisterModel> Register(
            @Field(Constants.USERNAME) String username,
            @Field(Constants.PASSWORD) String password,
            @Field(Constants.FIRSTNAME) String firstname,
            @Field(Constants.LASTNAME) String lastname,
            @Field(Constants.BIRTHDATE) String birthdate,
            @Field(Constants.PHOTO) String photo
    );

    @FormUrlEncoded
    @POST("/getUserBooks")
    Call<List<Book>> getUserBooks(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.USER_ID) String userId,
            @Field(Constants.ORDER) String order,
            @Field(Constants.SEARCH_TYPE) String type
    );


    @FormUrlEncoded
    @POST("/getUserReadBooks")
    Call<List<Book>> getUserReadBooks(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.USER_ID) String userId,
            @Field(Constants.ORDER) String order,
            @Field(Constants.SEARCH_TYPE) String type
    );

    @FormUrlEncoded
    @POST("/getUserWantedBooks")
    Call<List<Book>> getUserWantedBooks(
            @Header(Constants.AUTHORIZATION ) String apikey,
            @Field(Constants.USER_ID) String userId,
            @Field(Constants.ORDER) String order,
            @Field(Constants.SEARCH_TYPE) String type
    );

    @GET("/getNews")
    Call<List<News>> getNews(
            @Header(Constants.AUTHORIZATION ) String apikey
    );

    @GET("/getUserSuggestion")
    Call<List<Suggestion>> getUserSuggestion(
            @Header(Constants.AUTHORIZATION ) String apikey
    );

    @FormUrlEncoded
    @POST("/getBookInfoById")
    Call<BookDetails> getBookInfo(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.BOOK_ID) int book_id
    );

    @GET("/getUserInfo")
    Call<User> getUserInfo(
            @Header(Constants.AUTHORIZATION) String apikey
    );

    @POST("/getUserReview")
    @FormUrlEncoded
    Call<Review> getUserReview(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.BOOK_ID) int book_id
    );


    @POST("/addReview")
    Call<String> addReview(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Body Review review
            );


    @POST("/editReview")
    Call<String> editReview(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Body Review review
    );

    @POST("/getBookReviews")
    @FormUrlEncoded
    Call<List<BookReview>> getBookReviews(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.BOOK_ID) int bookId
    );

    @POST("/addRating")
    @FormUrlEncoded
    Call<String> addRating(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.BOOK_ID) int bookId,
            @Field(Constants.RATING) double rating,
            @Field(Constants.DATE_ADDED) String date
    );

    @POST("/removeRating")
    @FormUrlEncoded
    Call<String> removeRating(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.BOOK_ID) int bookId
    );

    @POST("/removeBookFromUserBooks")
    @FormUrlEncoded
    Call<String> removeBookFromUserBooks(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.BOOK_ID) int bookId
    );

    @POST("/addBookToRead")
    @FormUrlEncoded
    Call<String> addBookToRead(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.BOOK_ID) int bookId,
            @Field(Constants.DATE_ADDED) String date
    );

    @POST("/addBookToWanted")
    @FormUrlEncoded
    Call<String> addBookToWanted(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.BOOK_ID) int bookId,
            @Field(Constants.DATE_ADDED) String date
    );

    @POST("/getUserInfo")
    @FormUrlEncoded
    Call<User> getUserInfo(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.USER_ID) String userId
    );

    @POST("/editUserInfo")
    Call<LoginRegisterModel> editUserInfo(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Body User user
    );

    @POST("/searchBooks")
    @FormUrlEncoded
    Call<List<Book>> searchBooks(
            @Header(Constants.AUTHORIZATION) String apikey,
            @Field(Constants.SEARCH) String search
    );

    @GET("/getBooks")
    Call<List<Book>> getBooks();

    @GET("/getCategories")
    Call<List<String>> getCategories(
            @Header(Constants.AUTHORIZATION) String apikey
    );
}

