package mas.medth.service.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by adikwidiasmono on 11/02/18.
 */

public class ReqGetSMSOTP {
    @SerializedName("phoneNum")
    @Expose
    private String phoneNum;
    @SerializedName("digit")
    @Expose
    private Integer digit;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Integer getDigit() {
        return digit;
    }

    public void setDigit(Integer digit) {
        this.digit = digit;
    }
}
