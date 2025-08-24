package Util;

import com.alibaba.fastjson.JSON;
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

    private static final OkHttpClient client = new OkHttpClient.Builder()//TODO 消除重传 异步
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static final com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();

    private static String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    public static void noticeAudit(String interfaceVersion, String transSeqNo,
                                   String type, String filePath, String fileName) {//TODO 进一步封装 (已解决）
        //随机计算
        String nonceStr = generateNonceStr();
        String uniqueNo = generateNonceStr();

        TreeMap<String,Object> bizContentObj = getBizContent(interfaceVersion,transSeqNo,type,filePath,fileName);//TODO 封装到另一个 (已解决）

        try {
            TreeMap<String, Object> paramMap = getParamMap(bizContentObj,nonceStr,uniqueNo);

            String signContent = SignUtil.genSignContentWithSalt(paramMap, constants.SALT_KEY);
            log.info("待签名字符串： " + signContent);

            String sign = SignUtil.rsaSign(signContent, constants.PRIVATE_KEY, constants.SIGN_TYPE, constants.CHARSET);// TODO 签名未处理
            if (sign == null || sign.trim().isEmpty()) {
                log.info("签名失败，检查私钥格式");
            }
            log.info("签名" + sign);

            Map<String, Object> finalJson = new TreeMap<>();// TODO 命名
            finalJson.putAll(paramMap);
            finalJson.put("sign", sign);

            String json = JSON.toJSONString(finalJson);//TODO 学习// mapper.writeValueAsString(finalJson);
            log.info("请求JSON为 ： " + json);          //TODO 日志规范

            Request request = new Request.Builder()
                    .url(constants.BASE_PATH + constants.API_PATH)
                    .post(okhttp3.RequestBody.create(json,MediaType.get(("application/json; charset=utf-8"))))
                    .build();

            try (Response response = client.newCall(request).execute()) {//TODO
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();//TODO 了解
                    log.info("通知成功，响应： " + responseBody);     //TODO 具体信息
                                                                  //TODO 通知码输出/判断
                } else {
                    String errorMsg = response.body() != null ? response.body().string() : "未知错误";
                    log.error("请求失败，状态码： " + response.code() + "；响应： " + errorMsg);
                }
            }
        }catch (IOException e) {
            log.error("请求异常",  e);
        }
    }

    public static TreeMap getBizContent(String interfaceVersion, String transSeqNo, String type, String filePath, String fileName) {
        TreeMap bizContentObj = new TreeMap<>();
        bizContentObj.put("interfaceVersion",interfaceVersion);
        bizContentObj.put("transSeqNo",transSeqNo);
        bizContentObj.put("type",type);
        bizContentObj.put("filePath",filePath);
        bizContentObj.put("fileName",fileName);
        return bizContentObj;
    }

    public static TreeMap getParamMap(TreeMap bizContentObj,String nonceStr,String uniqueNo) {
        TreeMap paramMap = new TreeMap<>();
        paramMap.put("bizContent", bizContentObj);
        paramMap.put("mchId", constants.MCH_ID);
        paramMap.put("version", constants.VERSION);
        paramMap.put("signType", constants.SIGN_TYPE );
        paramMap.put("nonceStr", nonceStr);
        paramMap.put("uniqueNo", uniqueNo);
        return paramMap;
    }
}

