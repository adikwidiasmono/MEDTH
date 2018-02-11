package mas.medth.service.api;

import mas.medth.service.model.request.ReqGetSMSOTP;
import mas.medth.service.model.request.ReqSMSNotification;
import mas.medth.service.model.request.ReqVerifySMSOTP;
import mas.medth.service.model.response.ResAccessToken;
import mas.medth.service.model.response.ResGISResponse;
import mas.medth.service.model.response.ResOTPRequest;
import mas.medth.service.model.response.ResOTPVerification;
import mas.medth.service.model.response.ResPushSMSNotification;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by adikwidiasmono on 10/02/18.
 */

public interface XSightAPI {
    String TOKEN_KEY = "WGI2Rl91MzlsWlZpRjlCZjR5TFVPVmVCWlFzYTpsQWcyY0E1dERyX1F5a3hwbVBxN2ZydTY3YXdh";
    String BODY_GRANT_TYPE = "client_credentials";

    String BASE_URL = "http://api.mainapi.net";

    // Get Access Token
    String GET_ACCESS_TOKEN = "/token";

    // Services
    // 35 is Point Of Interest (POI) ID for Hospital
    String GET_NEARBY_HOSPITAL_LOCATION = "/arcgis/0.0.2/35/query/1000";
    // medthotp is a unique key to do OTP, you can fill any phrase you like
    String GET_SMS_OTP = "/smsotp/1.0.1/otp/medthotp";
    String VERIFY_SMS_OTP = "/smsotp/1.0.1/otp/medthotp/verifications";
    String SEND_SMS_NOTIFICATION = "/smsnotification/1.0.0/messages";

    @FormUrlEncoded
    @POST(GET_ACCESS_TOKEN)
    Call<ResAccessToken> getAccessToken(
            @Header("Authorization") String headerAuth,
            @Field("grant_type") String bodyGrantType
    );

    @GET(GET_NEARBY_HOSPITAL_LOCATION)
    Call<ResGISResponse> getNearbyHospitalLocation(
            @Header("Authorization") String headerAuth,
            @Query("geometry") String LongLat
    );

    @PUT(GET_SMS_OTP)
    Call<ResOTPRequest> getSMSOTP(
            @Header("Authorization") String headerAuth,
            @Body ReqGetSMSOTP reqGetSMSOTP
    );

    @POST(VERIFY_SMS_OTP)
    Call<ResOTPVerification> verifySMSOTP(
            @Header("Authorization") String headerAuth,
            @Body ReqVerifySMSOTP reqVerifySMSOTP
    );

    @Multipart
    @POST(SEND_SMS_NOTIFICATION)
    Call<ResPushSMSNotification> pushSMSNotification(
            @Header("Authorization") String headerAuth,
            @Part("msisdn") RequestBody msisdn,
            @Part("content") RequestBody content
    );

}
