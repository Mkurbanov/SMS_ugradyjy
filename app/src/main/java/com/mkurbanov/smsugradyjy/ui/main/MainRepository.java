package com.mkurbanov.smsugradyjy.ui.main;

import com.mkurbanov.smsugradyjy.data.ServerSocket;

import io.socket.emitter.Emitter;

public class MainRepository {
    private static MainRepository mainRepository;

    public static MainRepository getInstance() {
        if (mainRepository == null)
            mainRepository = new MainRepository();
        return mainRepository;
    }

    public Boolean isConnected(){
        return ServerSocket.getInstance().isConnected();
    }

    public void connect() {
        ServerSocket.getInstance().connect();
    }
    public void disconnect() {
        ServerSocket.getInstance().disconnect();
    }

    public void emit(String event, Object... args){
        ServerSocket.getInstance().emit(event, args);
    }

    public void emit(String event){
        ServerSocket.getInstance().emit(event);
    }

    public void addListener(String event, Emitter.Listener listener) {
        ServerSocket.getInstance().addListener(event, listener);
    }

    public void addListener(SocketOnErrorListener onErrorListener) {
        ServerSocket.getInstance().addListener(onErrorListener);
    }

    public void removeListener(String event, Emitter.Listener listener){
        ServerSocket.getInstance().removeListener(event, listener);
    }

    public void removeErrorListener(){
        ServerSocket.getInstance().removeErrorListener();
    }


}
