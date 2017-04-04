package suciu.alexandru.com.bookwormscommunity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alexandru on 23.05.2016.
 */
public class Util {

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }

    private static boolean isAvailableNetwork(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static boolean isActiveNetwork(Context context) {
        if (isAvailableNetwork(context)) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204")
                        .openConnection());
                if (urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        try {
            if (new Network().execute(context).get()) {
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

}
