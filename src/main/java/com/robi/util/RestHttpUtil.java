package com.robi.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.robi.data.ApiResult;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestHttpUtil.class);
    private static HttpComponentsClientHttpRequestFactory httpFactory = null;
    
    public static final int METHOD_GET      = 0;
    public static final int METHOD_POST     = 1;
    public static final int METHOD_PUT      = 2;
    public static final int METHOD_DELETE   = 3;

    static {
        httpFactory = new HttpComponentsClientHttpRequestFactory();
        httpFactory.setReadTimeout(5000);
        httpFactory.setConnectTimeout(5000);
        httpFactory.setHttpClient(HttpClientBuilder.create()
            .setMaxConnTotal(100)
            .setMaxConnPerRoute(20)
            .build());
    }

    protected static RestTemplate getInstance() {
        return new RestTemplate(httpFactory);
    }

    public static String urlConnection(String url, int method, Map<String, Object> reqHeadMap, Map<String, Object> reqBodyMap) {
        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
        
        for (String key : reqHeadMap.keySet()) {
            Object value = reqHeadMap.get(key);
            if (value != null) {
                httpHeader.add(key, value.toString());
            }
        }

        boolean isReqBodyExist = (reqBodyMap != null && reqBodyMap.size() > 0);
        JSONObject bodyJsonObj = null;
        HttpEntity<String> httpEntity = null;

        if (method == RestHttpUtil.METHOD_GET) {
            if (isReqBodyExist) {
                try {
                    StringBuilder bodyParamSb = new StringBuilder("?");
                    for (String key : reqBodyMap.keySet()) {
                        Object value = reqBodyMap.get(key);
                        if (value != null) {
                            bodyParamSb.append(URLEncoder.encode(key, "utf-8")).append("=")
                                       .append(URLEncoder.encode(value.toString(), "utf-8")).append("&");
                        }
                    }
                    bodyParamSb.setLength(bodyParamSb.length() - 1);
                    url += bodyParamSb.toString();
                }
                catch (UnsupportedEncodingException e) {
                    logger.error("Exception!", e);
                    return null;
                }
            }
        }
        else {
            if (isReqBodyExist) {
                try {
                    bodyJsonObj = new JSONObject(reqBodyMap);
                }
                catch (JSONException e) {
                    logger.error("Exception!", e);
                    return null;
                }

                httpEntity = new HttpEntity<String>(bodyJsonObj.toString(), httpHeader);
            }
        }

        RestTemplate restTemplate = RestHttpUtil.getInstance();
        ResponseEntity<String> rpyStr = null;

        switch (method) {
            case RestHttpUtil.METHOD_GET:
                rpyStr = restTemplate.getForEntity(url, String.class);
                break;
            case RestHttpUtil.METHOD_POST:
            default:
                rpyStr = restTemplate.postForEntity(url, httpEntity, String.class);
                break;
            /*
                [Note] put(), delete()는 응답결과를 받아올 수 없는 듯 하다.
                철저히 REST의 본연의 특징을 살려서 설계된 클래스인듯...
                추후 메서드 설계 변경해야 함.
            case RestHttpUtil.METHOD_PUT:
                restTemplate.put(url, 업데이트될 객체, paramMap);
                break;
            case RestHttpUtil.METHOD_DELETE:
                restTemplate.delete(url, reqBodyMap);
                break;
            */
        }

        return (rpyStr != null ? rpyStr.getBody() : null);
    }
}