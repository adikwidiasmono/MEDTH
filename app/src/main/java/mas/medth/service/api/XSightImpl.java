package mas.medth.service.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mas.medth.service.model.request.ReqGetSMSOTP;
import mas.medth.service.model.request.ReqSMSNotification;
import mas.medth.service.model.request.ReqVerifySMSOTP;
import mas.medth.service.model.response.ResAccessToken;
import mas.medth.service.model.response.ResGISResponse;
import mas.medth.service.model.response.ResOTPRequest;
import mas.medth.service.model.response.ResOTPVerification;
import mas.medth.service.model.response.ResPushSMSNotification;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Adik Widiasmono on 10/28/2015.
 */
public class XSightImpl {
    private static XSightImpl impl;
    private XSightAPI xSightAPI;

    private XSightImpl() {
        xSightAPI = ServiceGenerator.createService(XSightAPI.class);
    }

    public static XSightImpl getInstance() {
        if (impl == null)
            impl = new XSightImpl();

        return impl;
    }

    public Call<ResAccessToken> getAccessToken() {
        return xSightAPI.getAccessToken(
                "Basic " + XSightAPI.TOKEN_KEY,
                XSightAPI.BODY_GRANT_TYPE
        );
    }

    public Call<ResGISResponse> getNearbyHospitalLocation(
            String accessToken,
            String LongLat
    ) {
        return xSightAPI.getNearbyHospitalLocation(
                "Bearer " + accessToken,
                LongLat
        );
    }

    public Call<ResOTPRequest> getSMSOTP(
            String accessToken,
            ReqGetSMSOTP reqGetSMSOTP
    ) {
        return xSightAPI.getSMSOTP(
                "Bearer " + accessToken,
                reqGetSMSOTP
        );
    }

    public Call<ResOTPVerification> verifySMSOTP(
            String accessToken,
            ReqVerifySMSOTP reqVerifySMSOTP
    ) {
        return xSightAPI.verifySMSOTP(
                "Bearer " + accessToken,
                reqVerifySMSOTP
        );
    }

    public Call<ResPushSMSNotification> pushSMSNotification(
            String accessToken,
            ReqSMSNotification reqSMSNotification
    ) {
        return xSightAPI.pushSMSNotification(
                "Bearer " + accessToken,
                reqSMSNotification
        );
    }

}
