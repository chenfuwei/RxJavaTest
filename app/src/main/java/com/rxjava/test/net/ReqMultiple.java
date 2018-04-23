package com.rxjava.test.net;

import com.rxjava.test.entity.UserEntity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ReqMultiple {
    final public static String URL_LOGIN = "login";
    public static Observable<UserEntity> reqLogin(String domain, HashMap<String, String> param) {
        String url = "http://" + domain + "/appapi/"+URL_LOGIN;
        String sPassword = param.get("password");
        if(null != sPassword)
        {
            sPassword = encoderByMd5(sPassword);
            param.put("password", sPassword);
        }
        return new HttpProxy().setReqParams(param).setReqUrl(url)
                .reqData(UserEntity.class).subscribeOn(Schedulers.io());
    }

    public static String encoderByMd5(String str) {
        if(null == str)
        {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[]  bytes = digest.digest(str.getBytes());
            StringBuffer sb = new  StringBuffer();
            for(int i = 0;i<bytes.length;i++){
                String s = Integer.toHexString(0xff&bytes[i]);

                if(s.length()==1){
                    sb.append("0"+s);
                }else{
                    sb.append(s);
                }
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
