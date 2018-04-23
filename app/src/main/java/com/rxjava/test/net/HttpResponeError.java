package com.rxjava.test.net;

import java.util.HashMap;
import java.util.Map;

public class HttpResponeError {
    private static Map<Integer, String> errorMap;

    private static void initErrorMap()
    {
        if(null == errorMap)
        {
            errorMap = new HashMap<>();
        }
        //以下对应错误码
    }

    public static String getErrorMsg(int keyError)
    {
        if(null == errorMap)
        {
            initErrorMap();
        }

        String errorMsg = errorMap.get(keyError);
        if(null == errorMsg)
        {
            errorMsg =  "网络请求异常，请检查!";
        }
        return errorMsg;
    }
}
