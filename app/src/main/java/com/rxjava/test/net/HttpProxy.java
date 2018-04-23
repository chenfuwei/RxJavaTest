package com.rxjava.test.net;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;

public class HttpProxy {
    final public static String URL_TOEKN_TEST2 = "https://test-api.pingan.com.cn:20443/oauth/oauth2/" +
            "access_token?client_id=P_SLDJF23LKJSVJLK_STG2&grant_type=client_credentials&client_secret=K3XAU5P4";



    private Map<String, String> reqParams;
    private String reqUrl;
    public static final int HTTP_REQ_TIMEOUT = 10000;

    public HttpProxy setReqUrl(String url)
    {
        this.reqUrl = url;
        return this;
    }

    public HttpProxy setReqParams(HashMap<String, String> params)
    {
        if(null == this.reqParams)
        {
            this.reqParams = new HashMap<>();
        }
        this.reqParams.putAll(params);
        return this;
    }


    public <T> Observable<T> reqData(final Class<T> tClass)
    {
        Observable<T> observable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> ee) throws Exception {
                HttpResult httpResult = new HttpResult();
                try {
                    byte[] requestData = getRequestData();
                    URL url = new URL(reqUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(HTTP_REQ_TIMEOUT);     //设置连接超时时间\
                    httpURLConnection.setReadTimeout(HTTP_REQ_TIMEOUT);
                    httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                    httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
                    httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
                    //httpURLConnection.setRequestProperty("User-Agent", GenseeConfig.getUA());
                    //设置请求体的类型是文本类型
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    if(null != requestData && requestData.length > 0) {
                        //设置请求体的长度
                        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(requestData.length));
                        //获得输出流，向服务器写入数据
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(requestData);
                    }

                    int response = httpURLConnection.getResponseCode();            //获得服务器的响应码

                    if (response  == HttpURLConnection.HTTP_OK) {
                        InputStream inptStream = httpURLConnection.getInputStream();
                        String sReturnValue =  dealResponseResult(inptStream);                     //处理服务器的响应结果
                        httpResult.setResult(response);
                        httpResult.setValue(sReturnValue);

                        T a = tClass.newInstance();
                        ee.onNext(a);
                    } else {
                        httpResult.setResult(response);
                    }

                }  catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    ee.onError(new HttpReqError(HttpResult.ResultCode.RESULT_TIME_OUT, -1));
                    httpResult.setResult(HttpResult.ResultCode.RESULT_TIME_OUT);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    httpResult.setResult(HttpResult.ResultCode.RESULT_CONNECT_FAILURE);
                } catch (Exception e) {
                    e.printStackTrace();
                    httpResult.setResult(HttpResult.ResultCode.RESULT_FAILURE);
                }
            }
        });
        return observable;
    }


    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    private byte[] getRequestData()
    {
        if(null != reqParams && reqParams.size() > 0)
        {
            StringBuffer stringBuffer = new StringBuffer();
            for(Map.Entry<String, String> entry : reqParams.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(checkString(entry.getValue())))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
            return stringBuffer.toString().getBytes();
        }
        return null;
    }
    private String checkString(String data)
    {
        if(null == data)
        {
            return "";
        }
        else
        {
            return data;
        }
    }
}
