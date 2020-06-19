package com.lhl.gitlab;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

/**
 * flowable Rest 请求工具类
 */
@Slf4j
public class RestHttpUtils {

    private static String GIT_TOKEN = "ou_frg-4VszXFFnb1d2D";
//        "5bftdo8Sg3y9z-b817kV";//liuwi.ext
//        "H4hGCmEuXxxQ3u1sevA7";//weijg.ext

    /**
     * 发送Get请求
     *
     * @param url     请求地址
     * @param params  请求参数集合
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, String> params) throws IOException, URISyntaxException {
        // 创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建访问的地址
        URIBuilder uriBuilder = new URIBuilder(url);
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }
        // 创建http对象
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(6000).setSocketTimeout(6000).build();
        httpGet.setConfig(requestConfig);

        // 设置认证请求头
        httpGet.setHeader("PRIVATE-TOKEN", setHeader());

        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            return EntityUtils.toString(httpEntity, "UTF-8");
        } finally {
            // 释放资源
            HttpClientUtils.closeQuietly(httpResponse);
            HttpClientUtils.closeQuietly(httpClient);
        }
    }


    /**
     * 发送Post请求
     *
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public static String doPost(String url, String json) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        // flowable官方文档要求Accept和Content-Type都为application/json
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("PRIVATE-TOKEN", setHeader());
        StringEntity entity = new StringEntity(json, "UTF-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            return EntityUtils.toString(httpEntity, "UTF-8");
        } finally {
            // 释放资源
            HttpClientUtils.closeQuietly(httpResponse);
            HttpClientUtils.closeQuietly(httpClient);
        }
    }

    /**
     * 发送Delete请求
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doDelete(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URIBuilder uriBuilder = new URIBuilder(url);
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }
        HttpDelete httpDelete = new HttpDelete(url);
        // flowable官方文档要求Accept和Content-Type都为application/json
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-Type", "application/json");
        httpDelete.setHeader("PRIVATE-TOKEN", setHeader());
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpDelete);
            HttpEntity httpEntity = httpResponse.getEntity();
            return EntityUtils.toString(httpEntity, "UTF-8");
        } finally {
            // 释放资源
            HttpClientUtils.closeQuietly(httpResponse);
            HttpClientUtils.closeQuietly(httpClient);        }
    }

    /**
     * 发送Put请求
     *
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public static String doPut(String url,  String json) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        // flowable官方文档要求Accept和Content-Type都为application/json
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-Type", "application/json");
        httpPut.setHeader("PRIVATE-TOKEN", setHeader());
        StringEntity entity = new StringEntity(json, "UTF-8");
        httpPut.setEntity(entity);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPut);
            HttpEntity httpEntity = httpResponse.getEntity();
            return EntityUtils.toString(httpEntity, "UTF-8");
        } finally {
            // 释放资源
            HttpClientUtils.closeQuietly(httpResponse);
            HttpClientUtils.closeQuietly(httpClient);        }
    }

    private static String setHeader() {
//        return "5bftdo8Sg3y9z-b817kV";liuwi.ext
//        return "H4hGCmEuXxxQ3u1sevA7";--weijg.ext
//        return "ou_frg-4VszXFFnb1d2D";//weijg.ext
        return "vZ9u3w8Y5rsf7MwWmijH";
    }

}
