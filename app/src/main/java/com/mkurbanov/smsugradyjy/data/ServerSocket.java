package com.mkurbanov.smsugradyjy.data;

import static com.mkurbanov.smsugradyjy.config.BASE_URL;
import static com.mkurbanov.smsugradyjy.config.LOG_TAG;

import android.util.Log;

import com.mkurbanov.smsugradyjy.config;
import com.mkurbanov.smsugradyjy.ui.main.SocketOnErrorListener;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ServerSocket {
    private static ServerSocket serverSocket;
    private Socket mSocket;
    private SocketOnErrorListener onErrorListener;

    {
        try {
            mSocket = IO.socket(BASE_URL);
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "socket connection error");
            if (onErrorListener != null)
                onErrorListener.onError();
        }
    }

    public static ServerSocket getInstance() {
        if (serverSocket == null)
            serverSocket = new ServerSocket();
        return serverSocket;
    }

    public Boolean isConnected() {
        return mSocket.connected();
    }

    public void addListener(String event, Emitter.Listener listener) {
        mSocket.on(event, listener);
    }

    public void removeListener(String event, Emitter.Listener listener) {
        mSocket.off(event, listener);
    }

    public void removeErrorListener() {
        onErrorListener = null;
    }

    public void addListener(SocketOnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void emit(String event, Object... args){
        this.mSocket.emit(event, args);
    }

    public void emit(String event){
        this.mSocket.emit(event);
    }

    public void connect() {
        mSocket.connect();
    }

    public void disconnect() {
        mSocket.disconnect();
    }
}
