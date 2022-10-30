package com.rogistudio.ncommerce.controller.auth;

import com.rogistudio.ncommerce.comm.util.CommUtil;
import com.rogistudio.ncommerce.comm.util.OKHttpClient;
import com.rogistudio.ncommerce.entity.CustomErrorType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class AuthController {

    @Value("${naver.commerce.api.host}")
    String naver_host;
    @Value("${naver.commerce.api.client_id}")
    String naver_client_id;

    @Value("${naver.commerce.api.client_secret}")
    String naver_client_secret;

    private final MediaType mediaType = MediaType.parse("application/json");

    public static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    public ResponseEntity<String> getToken() {

        String access_token="";
        JSONObject responseObj = new JSONObject();
        // 응답 header 생성
        HttpHeaders responseHeaders = new HttpHeaders();
        OKHttpClient okClient = null;

        try {

            Long timestamp = System.currentTimeMillis();
            String client_secret_sign = CommUtil.generateSignature(this.naver_client_id, this.naver_client_secret, timestamp);


            JSONObject json = new JSONObject();
            json.put("client_id", this.naver_client_id);
            json.put("timestamp", timestamp);
            json.put("client_secret_sign", client_secret_sign);
            json.put("grant_type", "client_credentials");
            json.put("type", "SELLER");
            json.put("account_id", "playbabe");

            String query_param = "?client_id="+this.naver_client_id
                               + "&timestamp="+timestamp
                               + "&client_secret_sign="+client_secret_sign
                               + "&grant_type=client_credentials"
                               + "&type=SELLER"
                               + "&account_id=playbabe";

            System.out.println("url:"+this.naver_host+"/v1/oauth2/token"+query_param);

            RequestBody body = RequestBody.create(json.toJSONString(), this.mediaType);
            Request request = new Request.Builder()
                        .url(this.naver_host+"/v1/oauth2/token"+query_param)
                        //.post(body)
                        .addHeader("content-type", this.mediaType.toString())
                        .build();

            okClient = new OKHttpClient(request);
            int code = okClient.doUsingHttp();


            if (code == 200) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(okClient.getResponseBody());

                responseObj.put("access_token", (String) jsonObj.get("access_token"));
                responseObj.put("expires_in", (String) jsonObj.get("expires_in"));
                responseObj.put("token_type", (String) jsonObj.get("token_type"));

                System.out.println(responseObj.toJSONString());

                responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
            }else{
                System.out.println("naver http code:" + code);
                System.out.println("naver http error_msg:" + okClient.getErrorMsg());
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.warn("error",e);
            return new ResponseEntity(new CustomErrorType("관리자에 문의하세요"),HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>(responseObj.toString(), responseHeaders, HttpStatus.OK);

    }
}
