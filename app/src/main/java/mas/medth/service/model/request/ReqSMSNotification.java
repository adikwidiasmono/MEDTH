package mas.medth.service.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by adikwidiasmono on 10/02/18.
 */

public class ReqSMSNotification {
    @SerializedName("msisdn")
    @Expose
    private String msisdn; // Phone number using country code i.e. +628xxx
    @SerializedName("content")
    @Expose
    private String content;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
