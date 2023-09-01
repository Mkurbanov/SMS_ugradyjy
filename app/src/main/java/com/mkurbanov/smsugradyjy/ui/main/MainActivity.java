package com.mkurbanov.smsugradyjy.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.mkurbanov.smsugradyjy.R;
import com.mkurbanov.smsugradyjy.models.SmsCodeModel;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private TextView tvStatus;
    private LottieAnimationView lottieView;
    private Boolean dontTouchToLottie = false;
    RelativeLayout rlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        checkSMSPermission();

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.connected.observe(this, this::onServerStatusChanged);
        mainViewModel.smsCodeModelLiveData.observe(this, this::onNewMessage);
        mainViewModel.messageSended.observe(this, this::sended);
        getLifecycle().addObserver(mainViewModel);
    }

    private void initViews() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        lottieView = findViewById(R.id.animation_view);
        tvStatus = findViewById(R.id.text_view_status);
        rlMain = findViewById(R.id.rl_main);
    }

    private void connectionError() {
        tvStatus.setText(getString(R.string.connect_error));
        lottieView.pauseAnimation();
        rlMain.setBackgroundColor(getColor(R.color.red_dark));
    }

    private void connectedToServer() {
        if (dontTouchToLottie)
            return;
        tvStatus.setText(getString(R.string.listening_server));
        lottieView.setSpeed(0.1f);
        lottieView.setMinFrame(1);
        lottieView.setMaxFrame(10);
        lottieView.playAnimation();
        rlMain.setBackgroundColor(getColor(android.R.color.background_light));
    }

    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
    }

    private void onNewMessage(SmsCodeModel smsCodeModel) {
        tvStatus.setText(getString(R.string.message_sending));
        Toast.makeText(this, "Telefon belgi = " + smsCodeModel.phone + " smsCode = " + smsCodeModel.smsCode, Toast.LENGTH_SHORT).show();
        dontTouchToLottie = true;
        lottieView.setMinFrame(10);
        lottieView.setMaxFrame(70);
        lottieView.setSpeed(0.5f);
        lottieView.playAnimation();
        lottieView.postDelayed(() -> {
            dontTouchToLottie = false;
            connectedToServer();
        }, 8000);
    }

    private void sended(Boolean bool) {
        if (!bool)
            return;

        tvStatus.setText(getString(R.string.message_sended));
        connectedToServer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "SMS uçin rugsat berilmedi.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private void onServerStatusChanged(Boolean connected) {
        if (connected) connectedToServer();
        else connectionError();
    }
}