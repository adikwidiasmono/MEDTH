package mas.medth.service.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by adikwidiasmono on 11/02/18.
 */

public class ReqVerifySMSOTP {
    @SerializedName("optstr")
    @Expose
    private String otpstr;
    @SerializedName("digit")
    @Expose
    private Integer digit;

    public String getOtpstr() {
        return otpstr;
    }

    public void setOtpstr(String otpstr) {
        this.otpstr = otpstr;
    }

    public Integer getDigit() {
        return digit;
    }

    public void setDigit(Integer digit) {
        this.digit = digit;
    }
}
