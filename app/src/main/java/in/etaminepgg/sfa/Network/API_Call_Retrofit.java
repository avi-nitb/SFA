package in.etaminepgg.sfa.Network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class API_Call_Retrofit
{

    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();


    public static Retrofit getretrofit(final Context ctx)
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit;
    }

}
