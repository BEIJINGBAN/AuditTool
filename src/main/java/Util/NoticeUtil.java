package Util;

import config.constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NoticeUtil {

    private static final Logger log = LogManager.getLogger(NoticeUtil.class);

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static final com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();

    //Json格式
    public static class BizContent {
        public String interfaceVersion;
        public String transSeqNo;
        public String type;
        public String filePath;
        public String fileName;

        public BizContent(String interfaceVersion, String transSeqNo,
                          String type, String filePath, String fileName){
            this.interfaceVersion=interfaceVersion;
            this.transSeqNo=transSeqNo;
            this.type=type;
            this.filePath = filePath;
            this.fileName = fileName;
        }
    }

    public static class RequestBody{
        public String mchId;
        public String version;
        public String signType;
        public String nonceStr;
        public String uniqueNo;
        public String bizContent;

        public RequestBody(String mchId, String version, String signType, String nonceStr, String uniqueNo, String bizContent) {
            this.mchId = mchId;
            this.version = version;
            this.signType = signType;
            this.nonceStr = nonceStr;
            this.uniqueNo = uniqueNo;
            this.bizContent = bizContent;
        }
    }

    private static String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    public static void noticeAudit(String BASE_URL, String API_PATH,
                                      String interfaceVersion, String transSeqNo,
                                      String type, String filePath, String fileName) {


        boolean result = false;

        //随机计算
        String nonceStr = generateNonceStr();
        String uniqueNo = generateNonceStr();

        TreeMap<String,Object> bizContentObj = new TreeMap<>();
        bizContentObj.put("interfaceVersion",interfaceVersion);
        bizContentObj.put("transSeqNo",transSeqNo);
        bizContentObj.put("type",type);
        bizContentObj.put("filePath",filePath);
        bizContentObj.put("fileName",fileName);

        try {
            TreeMap<String, Object> paramMap = new TreeMap<>();
            paramMap.put("bizContent", bizContentObj);
            paramMap.put("mchId", constants.MCH_ID);
            paramMap.put("version", constants.VERSION);
            paramMap.put("signType", constants.SIGN_TYPE );
            paramMap.put("nonceStr", nonceStr);
            paramMap.put("uniqueNo", uniqueNo);

            String signContent = SignUtil.genSignContentWithSalt(paramMap, constants.SALT_KEY);
            log.info("待签名字符串： " + signContent);

            String sign = SignUtil.rsaSign(signContent, constants.PRIVATE_KEY, constants.SIGN_ALGORITHM, constants.CHARSET);
            if (sign == null || sign.trim().isEmpty()) {
                log.info("签名失败，检查私钥格式");
            }
            log.info("签名" + sign);

            Map<String, Object> finalJson = new TreeMap<>();
            finalJson.putAll(paramMap);
            finalJson.put("sign", sign);

            String json = mapper.writeValueAsString(finalJson);
            log.info("请求JSON为 ： " + json);

            Request request = new Request.Builder()
                    .url(BASE_URL + API_PATH)
                    .post(okhttp3.RequestBody.create(json,MediaType.get(("application/json; charset=utf-8"))))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    log.info("通知成功，响应： " + responseBody);
                } else {
                    String errorMsg = response.body() != null ? response.body().string() : "未知错误";
                    log.error("请求失败，状态码： " + response.code() + "；响应： " + errorMsg);
                }
            }
        }catch (IOException e) {
            log.error("请求异常" + e.getMessage());
            e.printStackTrace();
        }
    }
}

