package mas.medth.main;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mas.medth.service.api.XSightImpl;
import mas.medth.service.model.request.ReqGetSMSOTP;
import mas.medth.service.model.request.ReqVerifySMSOTP;
import mas.medth.service.model.response.ResAccessToken;
import mas.medth.service.model.response.ResOTPRequest;
import mas.medth.service.model.response.ResOTPVerification;
import mas.medth.utils.ProgressBarCircularIndeterminate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginOTPActivity extends AppCompatActivity {
    private EditText etPIN;
    private TextInputLayout tilPIN;
    private Button btVerify;
    private ProgressBarCircularIndeterminate pbVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Login OTP");

        etPIN = findViewById(R.id.et_pin);
        tilPIN = findViewById(R.id.til_pin);
        btVerify = findViewById(R.id.bt_verify);
        pbVerify = findViewById(R.id.pb_verify);
        pbVerify.setVisibility(View.GONE);

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tilPIN.setErrorEnabled(false);
                if (etPIN.getText() == null || etPIN.getText().toString() == null || etPIN.getText().toString().trim().length() < 0) {
                    tilPIN.setError("Invalid PIN Format");
                    tilPIN.setErrorEnabled(true);
                    return;
                }

                pbVerify.setVisibility(View.VISIBLE);
                Call tokenCall = XSightImpl
                        .getInstance()
                        .getAccessToken();
                tokenCall.enqueue(new Callback<ResAccessToken>() {
                    @Override
                    public void onResponse(Call<ResAccessToken> call, Response<ResAccessToken> response) {
                        if (response.isSuccessful()) {
                            ResAccessToken accessToken = response.body();
                            final ReqVerifySMSOTP req = new ReqVerifySMSOTP();
                            req.setOtpstr(etPIN.getText().toString());
                            req.setDigit(4);

                            Call reqOTPCall = XSightImpl
                                    .getInstance()
                                    .verifySMSOTP(accessToken.getAccessToken(), req);
                            reqOTPCall.enqueue(new Callback<ResOTPVerification>() {
                                @Override
                                public void onResponse(Call<ResOTPVerification> call, Response<ResOTPVerification> response) {
                                    pbVerify.setVisibility(View.GONE);

                                    if (response.isSuccessful()) {
                                        ResOTPVerification res = response.body();
                                        if (res.getStatus()) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.e("Verify OTP", "onResponse " + res.getMessage());
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "Failed to authenticate, please try again",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.e("Verify OTP", "onResponse " + response.code());
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Failed to authenticate, please try again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResOTPVerification> call, Throwable t) {
                                    t.printStackTrace();
                                    Log.e("Verify OTP", "onFailure " + t.getLocalizedMessage());
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Failed to authenticate, please try again",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            pbVerify.setVisibility(View.GONE);

                            Log.e("Get Access Token", "onResponse " + response.code());
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Failed to authenticate, please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResAccessToken> call, Throwable t) {
                        pbVerify.setVisibility(View.GONE);

                        t.printStackTrace();
                        Log.e("Get Access Token", "onFailure " + t.getLocalizedMessage());
                        Toast.makeText(
                                getApplicationContext(),
                                "Failed to authenticate, please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
