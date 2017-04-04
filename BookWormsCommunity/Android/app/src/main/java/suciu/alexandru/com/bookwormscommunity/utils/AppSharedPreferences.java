package suciu.alexandru.com.bookwormscommunity.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alexandru on 11.05.2016.
 */
public class AppSharedPreferences {

    //set user Status
    public static void setUserStaus(Context context, String storeName, Boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(storeName, value);
        editor.commit();
    }

    //get user Stauts
    public static boolean getUserStatus(Context context, String storeName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(storeName, false);
    }

    //set user Api_Key
    public static void setApiKey(Context context, String storeName, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(storeName, value);
        editor.commit();
    }

    //get user Api_Key
    public static String getApiKey(Context context, String storeName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(storeName, "");
    }

    //set userName
    public static void setUsername(Context context, String storeName, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(storeName, value);
        editor.commit();
    }

    //get userName
    public  static String getUsername(Context context, String storeName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(storeName, "");
    }

    //set User ID
    public static void setUserId(Context context, String storeName, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(storeName, value);
        editor.commit();
    }

    //get userId
    public static String getUserId(Context context, String storeName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(storeName, "");
    }

    //dtop Session
    public static void stopSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.BOOK_WORMS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
