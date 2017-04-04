package suciu.alexandru.com.bookwormscommunity.utils;

/**
 * Created by Alexandru on 11.05.2016.
 */
public class Constants {

    //Service manager
    public static final String API_URL = "http://bookwormscomunity.com/";
    public static final String PHOTO_URL = "http://bookwormscomunity.com/ProfilePictures/";

    //photo_status
    public static final String NO_PHOTO = "NOPHOTO";
    public static final String NO_CHANGE = "NOCHANGE";
    public static final String EDIT = "EDIT";
    public static final String LOADING = "Loading";

    //sorting criteria
    public static final String SEARCH_TYPE = "type";
    public static final String TITLE = "title";
    public static final String RATING = "rating";
    public static final String DATE_ADDED = "date";
    public static final String AUTHOR = "author";
    public static final String NO_RATINGS = "nrRatings";
    public static final String NO_READ = "nrRead";

    public static final String ORDER_ASC  = "asc";
    public static final String ORDER_DESC = "desc";
    public static final String ORDER = "order";

    //server errors
    public static final String SERVER_CONNECTION_ERROR = "There was a problem with server connection";
    public static final String REQUEST_FAILED = "Could not retrieve data";
    public static final String POST_FAILED = "Could not update data";

    //api parameters and headers
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String BIRTHDATE = "birthdate";
    public static final String BOOK_ID = "bookId";
    public static final String AUTHORIZATION = "authorization";
    public static final String PHOTO = "encoded_string";
    public static final String USER_ID = "userId";
    public static final String SEARCH = "search";

    //Shared preferences
    public static final String BOOK_WORMS_PREFERENCES = "USER_STATUS";

    public static final String IS_LOGGED_IN = "is_logged_in";
    public static final String ApiKey = "apikey";

    //drawer
    public static final int Opened = 1;
    public static final int Closed = 0;

    //Review
    public static final String EDIT_REVIEW = "Edit Review";
    public static final String DELETE_REVIEW = "Delete Review";
    public static final String ADD_REVIEW = "Add Review";

    //Application title
    public static final String MY_BOOKS = "My Books";
    public static final String NEWS = "News";
    public static final String RECOMMENDATIONS = "Suggestions";
    public static final String PROFILE = "My Profile";
    public static final String SEARCH_BOOKS = "Search";
    public static final String LOG_OUT = "Log out";
    public static final String APPLICATION_CHOOSE_TYPE = "Application type";

    //Books
    public static enum BookType  {CLASSICS, SCIENCE_FICTION, SCIENCE, THRILLER, PHILOSOPHY};

}
