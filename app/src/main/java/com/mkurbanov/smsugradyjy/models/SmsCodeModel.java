package com.mkurbanov.smsugradyjy.models;

import com.google.gson.annotations.SerializedName;

public class SmsCodeModel {
    public String phone;
    @SerializedName("smscode")
    public String smsCode;
}
