package com.mkurbanov.smsugradyjy.ui.main;

import static com.mkurbanov.smsugradyjy.config.LOG_TAG;

import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.mkurbanov.smsugradyjy.models.SmsCodeModel;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class MainViewModel extends ViewModel implements DefaultLifecycleObserver {
    private MainRepository mainRepository = MainRepository.getInstance();
    public MutableLiveData<Boolean> connected, messageSended;
    public MutableLiveData<SmsCodeModel> smsCodeModelLiveData;
    private Handler mHandler = new Handler();

    public MainViewModel() {
        connected = new MutableLiveData<>();
        messageSended = new MutableLiveData<>();
        smsCodeModelLiveData = new MutableLiveData<>();
        connectToServer();
    }


    private void connectToServer() {
        mainRepository.addListener("connected", onConnectedListener);
        mainRepository.addListener("send", onMessageListener);
        mainRepository.addListener(socketOnErrorListener);
        mainRepository.connect();
        StartServerStatusChecker();
    }


    private void sendSMS(SmsCodeModel smsCodeModel) {
        smsCodeModelLiveData.postValue(smsCodeModel);
        // sending message...
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsCodeModel.phone, null, smsCodeModel.smsCode, null, null);
        //we can't check is message received. So will return sended.
        messageSended.postValue(true);
        sended(smsCodeModel);
    }

    private void sended(SmsCodeModel smsCodeModel){
        mainRepository.emit("sended", smsCodeModel.phone);
    }

    private Emitter.Listener onConnectedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            connected.postValue(true);
        }
    };

    private Emitter.Listener onMessageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject) args[0];
            SmsCodeModel smsCodeModel = new Gson().fromJson(jsonObject.toString(), SmsCodeModel.class);
            sendSMS(smsCodeModel);
        }
    };

    private SocketOnErrorListener socketOnErrorListener = new SocketOnErrorListener() {
        @Override
        public void onError() {
            connected.postValue(false);
        }
    };

    private void StartServerStatusChecker(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "status =  " + mainRepository.isConnected());
                mHandler.postDelayed(this, 3000);
                if (mainRepository.isConnected())
                    return;
                connected.postValue(false);
            }
        };
        mHandler.postDelayed(runnable, 3000);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        mainRepository.disconnect();
        mainRepository.removeListener("connected", onConnectedListener);
        mainRepository.removeListener("send", onMessageListener);
        mainRepository.removeErrorListener();
    }

}
