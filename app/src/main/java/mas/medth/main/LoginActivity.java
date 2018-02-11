package mas.medth.main;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import mas.medth.service.api.XSightImpl;
import mas.medth.service.model.request.ReqGetSMSOTP;
import mas.medth.service.model.response.ResAccessToken;
import mas.medth.service.model.response.ResOTPRequest;
import mas.medth.utils.ProgressBarCircularIndeterminate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final int PHONE_STATE_PERMISSION = 1;
    private final int LOCATION_PERMISSION = 2;

    private Button btLogin;
    private ProgressBarCircularIndeterminate pbLogin;
    private String currentPhoneNumber = "+6285736260367";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestPhoneStatePermission();
        requestLocationPermission();
        initView();
        initAction();
    }

    private void requestPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_SMS, android.Manifest.permission.READ_PHONE_STATE},
                    PHONE_STATE_PERMISSION);
        } else {
            getCurrentPhoneNumber();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PHONE_STATE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                    getCurrentPhoneNumber();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_SMS, android.Manifest.permission.READ_PHONE_STATE},
                            PHONE_STATE_PERMISSION);
                }
                return;
            }
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION);
                }
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (isValidMobile(tMgr.getLine1Number())) {
            currentPhoneNumber = tMgr.getLine1Number();
        }
    }

    private void initView() {
        pbLogin = findViewById(R.id.pb_login);
        btLogin = findViewById(R.id.bt_login);

        pbLogin.setVisibility(View.GONE);
    }

    private void initAction() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(getApplicationContext(), LoginOTPActivity.class));
//                    }
//                }, 3000);

                pbLogin.setVisibility(View.VISIBLE);
                Call tokenCall = XSightImpl
                        .getInstance()
                        .getAccessToken();
                tokenCall.enqueue(new Callback<ResAccessToken>() {
                    @Override
                    public void onResponse(Call<ResAccessToken> call, Response<ResAccessToken> response) {
                        if (response.isSuccessful()) {
                            ResAccessToken accessToken = response.body();
                            final ReqGetSMSOTP req = new ReqGetSMSOTP();
                            req.setPhoneNum(currentPhoneNumber);
                            req.setDigit(4);

                            Call reqOTPCall = XSightImpl
                                    .getInstance()
                                    .getSMSOTP(accessToken.getAccessToken(), req);
                            reqOTPCall.enqueue(new Callback<ResOTPRequest>() {
                                @Override
                                public void onResponse(Call<ResOTPRequest> call, Response<ResOTPRequest> response) {
                                    pbLogin.setVisibility(View.GONE);

                                    if (response.isSuccessful()) {
                                        ResOTPRequest res = response.body();
                                        if (res.getStatus()) {
                                            startActivity(new Intent(getApplicationContext(), LoginOTPActivity.class));
                                        } else {
                                            Log.e("Get OTP Req", "onResponse " + res.getMessage());
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "Failed to login, please try again",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.e("Get OTP Req", "onResponse " + response.code());
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Failed to login, please try again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResOTPRequest> call, Throwable t) {
                                    t.printStackTrace();
                                    Log.e("Get OTP Req", "onFailure " + t.getLocalizedMessage());
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Failed to login, please try again",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            pbLogin.setVisibility(View.GONE);

                            Log.e("Get Access Token", "onResponse " + response.code());
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Failed to login, please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResAccessToken> call, Throwable t) {
                        pbLogin.setVisibility(View.GONE);

                        t.printStackTrace();
                        Log.e("Get Access Token", "onFailure " + t.getLocalizedMessage());
                        Toast.makeText(
                                getApplicationContext(),
                                "Failed to login, please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

}
