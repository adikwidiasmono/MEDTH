package mas.medth.service.api;

import android.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Map;

import mas.medth.service.model.request.ReqGetSMSOTP;
import mas.medth.service.model.request.ReqSMSNotification;
import mas.medth.service.model.request.ReqVerifySMSOTP;
import mas.medth.service.model.response.ResAccessToken;
import mas.medth.service.model.response.ResGISResponse;
import mas.medth.service.model.response.ResOTPRequest;
import mas.medth.service.model.response.ResOTPVerification;
import mas.medth.service.model.response.ResPushSMSNotification;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("phoneNum", reqGetSMSOTP.getPhoneNum());
        jsonParams.put("digit", reqGetSMSOTP.getDigit());

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        return xSightAPI.getSMSOTP(
                "Bearer " + accessToken,
                reqGetSMSOTP
        );
    }

    public Call<ResOTPVerification> verifySMSOTP(
            String accessToken,
            ReqVerifySMSOTP reqVerifySMSOTP
    ) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("otpstr", reqVerifySMSOTP.getOtpstr());
        jsonParams.put("digit", reqVerifySMSOTP.getDigit());

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        return xSightAPI.verifySMSOTP(
                "Bearer " + accessToken,
                reqVerifySMSOTP
        );
    }

    public Call<ResPushSMSNotification> pushSMSNotification(
            String accessToken,
            ReqSMSNotification reqSMSNotification
    ) {
        RequestBody msisdn = RequestBody.create(MediaType.parse("text/plain"), reqSMSNotification.getMsisdn());
        RequestBody content = RequestBody.create(MediaType.parse("text/plain"), reqSMSNotification.getContent());

        return xSightAPI.pushSMSNotification(
                "Bearer " + accessToken,
                msisdn,
                content
        );
    }

}
