package suciu.alexandru.com.bookwormscommunity.services;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;

/**
 * Created by Alexandru on 10.05.2016.
 */
public class ServiceManager {

    private ApiInterface apiInterface;

    public ApiInterface getApiInterface() {
        if (apiInterface == null) {
            apiInterface = new Retrofit.Builder()
                    .baseUrl(Constants.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder().build())
                    .build()
                    .create(ApiInterface.class);
        }
        return apiInterface;
    }
    //private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//    private static Retrofit.Builder builder = new Retrofit.Builder()
//            .baseUrl(API_URL)
//            .addConverterFactory(GsonConverterFactory.create());
//
//    public static <T> T makeService(Class<T> serviceClass) {
//        Retrofit retrofit = builder.client((httpClient.build())).build();
//        return retrofit.create(serviceClass);
//    }

}