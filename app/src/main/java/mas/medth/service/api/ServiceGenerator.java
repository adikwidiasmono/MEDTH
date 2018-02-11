package mas.medth.service.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Adik Widiasmono on 3/7/2016.
 */
public class ServiceGenerator {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(XSightAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit;

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, final String tokenType, final String accessToken) {
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder;

                if (accessToken != null) {
//                    Log.e("TOKEN", "|" + accessToken + "|");
                    requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Authorization", tokenType + " " + accessToken)
                            .method(original.method(), original.body());
                } else {
                    requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body());
                }

                Request newRequest = requestBuilder.build();
                Response response = chain.proceed(newRequest);

//                LOGPLAINREQUEST(newRequest);
//                LOGPLAINRESPONSE(response);

                return response;
            }
        });

        // Add logger
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        httpClient.addInterceptor(interceptor);

        // Set timeout
        httpClient.connectTimeout(25, TimeUnit.SECONDS);
        httpClient.readTimeout(40, TimeUnit.SECONDS);
        httpClient.writeTimeout(25, TimeUnit.SECONDS);

        retrofit = retrofit();
        return retrofit.create(serviceClass);
    }

    public static Retrofit retrofit() {
        OkHttpClient client = httpClient.build();
        retrofit = builder.client(client).build();
        return retrofit;
    }

    private static void LOGPLAINREQUEST(Request newRequest) {
        try {
            final Request copy = newRequest.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            Log.e("PLAIN REQUEST", buffer.readUtf8());
        } catch (final IOException e) {
            Log.e("PLAIN REQUEST", "did not work");
        }
    }

    private static void LOGPLAINRESPONSE(Response response) {
        final Response copy = response.newBuilder().build();
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(copy.body().byteStream()));
            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("PLAIN RESPONSE", sb.toString());
    }
}
