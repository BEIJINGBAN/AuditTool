import config.constants;
import domian.ReconBill;
import Util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Audit321 {
    public static void Audit321(int excelSize) throws IOException, NoSuchAlgorithmException {
        //应收账单3.2.1
        //日期格式
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String soleID = "";

        //命名数据
        String fileType = "Tran";//文件类型 Tran\Alloc\BankFlow\TranAuditResult
        String entCode = "ZN330301"; //企业编号
        String tranTime = sdf.format(new Date());//当前时间
        String tranType = "转账";//企业收银方式
        String opCode = "ADD";//操作码 ADD UPDATE DEL NOTICE
        String businessTag = "CRM";//业务类型

        //文件地址
        String zipPath = "./src/";
        String ExcelPath = "./src/main/audit321/";

        //通知信息
        String interfaceVersion = "1.0";
        String transSeqNo = "NoABC";
        String type = "1";

        //应收上传
        String excelName = fileType + "_" + entCode + "_" + tranTime + "_" + tranType + "_" + opCode;
        String zipName = fileType + "_" + entCode + "_" + businessTag + "_" + tranTime;
        //生成的Excel
        List<ReconBill> info = new ArrayList<ReconBill>();

        //工具类的创建
        ExcelUtil excel = new ExcelUtil();
        ZipUtil zip = new ZipUtil();
        SftpUtil sftp = new SftpUtil();
        NoticeUtil notice = new NoticeUtil();
        //测试数据
        ReconBill test1 = new ReconBill();
        ReconBill test2 = new ReconBill();

        test1.setOrderOrgCode("S112");
        test1.setAcquiringMerchantNo("11889900");
        test1.setBizSystemCode("XSC");
        test1.setReceivableOrderNo("XSC0000001");
        test1.setMerchantOrderNo("0000000001");
        test1.setIncomeExpenseFlag("2"); // 收入
        test1.setAmount("100"); // 100分 = 1元
        test1.setTradeTime(new Date());
        test1.setOriginalTradeTime(null); // 可选，非现金上缴可为空
        test1.setEnterpriseCashieType("转账");
        test1.setAuditNotifyUrl("http://10.60.45.65:9091/fileUpload/callback");
        test1.setDeliveryOrderFlag("1"); // 是发货订单
        test1.setConfirmReceiveTime(new Date());
        test1.setExtendInfo("{\"extend1\":\"手机\",\"extend2\":\"1\",\"extend3\":\"0.5\",\"extend5\":\"李四\"}");

        test2.setOrderOrgCode("S113");
        test2.setAcquiringMerchantNo("11889911");
        test2.setBizSystemCode("HY");
        test2.setReceivableOrderNo("HY0000001");
        test2.setMerchantOrderNo("0000000002");
        test2.setIncomeExpenseFlag("2"); // 收入
        test2.setAmount("500"); // 100分 = 1元
        test2.setTradeTime(new Date());
        test2.setOriginalTradeTime(null); // 可选，非现金上缴可为空
        test2.setEnterpriseCashieType("转账");
        test2.setAuditNotifyUrl("http://10.60.45.65:9091/fileUpload/callback");
        test2.setDeliveryOrderFlag("1"); // 是发货订单
        test2.setConfirmReceiveTime(new Date()); //TODO 111
        test2.setExtendInfo("{ \"extend1\": \"商品名称\", \"extend2\": \"购买数量\", \"extend3\": \"运费\", \"extend4\": \"单位金额\", \"extend5\": \"购买人名称\", \"extend6\": \"客户ID\", \"extend20\": \"其他自定义字段，最多支持20个\"}");

        // 添加到列表
        info.add(test1);
        info.add(test2);
        //生成唯一ID
        for (ReconBill data : info) {
            String recordId = data.getRecordId();
            data.setRecordId(recordId);
        }
        LinkedHashMap<String, List<ReconBill>> infoMap = excel.PartitionExcel(info,excelSize,excelName);
        if (infoMap == null) {
            return;
        }
        for (Map.Entry<String, List<ReconBill>> entry : infoMap.entrySet()) {
            String filePath = ExcelPath + entry.getKey();
            List<ReconBill> data = entry.getValue();
            int total = data.size();
            //总金额
            int amount = data.stream()
                    .mapToInt(d -> Integer.parseInt(d.getAmount()))
                    .sum();

            ExcelUtil.ExcelGenerator excelGenerator = ExcelUtil.ExcelGenerator.create()
                    //表一
                    .sheet("总览表", new String[]{"总笔数", "总金额"},
                            Arrays.<String[]>asList(
                                    new String[]{String.valueOf(total), String.valueOf(amount)}
                            )
                    )
                    //表二
                    .sheet("明细表", new String[]{
                                    "订单组织代码", "收单商户号",
                                    "业务系统标识",
                                    "应收单号",
                                    "商户订单号",
                                    "渠道流水号",
                                    "收支方向",
                                    "金额",
                                    "交易时间",
                                    "原交易日期",
                                    "企业收银方式",
                                    "备注",
                                    "对方户名",
                                    "对方账号",
                                    "对方银行",
                                    "用途",
                                    "结果通知地址",
                                    "发货类订单标识",
                                    "确认收货时间",
                                    "扩展信息"},
                            data.stream()
                                    .map(d -> new String[]{
                                            d.getOrderOrgCode(),
                                            d.getAcquiringMerchantNo(),
                                            d.getBizSystemCode(),
                                            d.getReceivableOrderNo(),
                                            d.getMerchantOrderNo(),
                                            d.getChannelSerialNo(),
                                            d.getIncomeExpenseFlag(),
                                            d.getAmount(),
                                            (d.getTradeTime() != null ? sdt.format(d.getTradeTime()) : null),
                                            (d.getOriginalTradeTime() != null ? sdt.format(d.getOriginalTradeTime()) : null),
                                            d.getEnterpriseCashieType(),
                                            d.getRemark(),
                                            d.getCounterpartyName(),
                                            d.getCounterpartyAccount(),
                                            d.getCounterpartyBank(),
                                            d.getPurpose(),
                                            d.getAuditNotifyUrl(),
                                            d.getDeliveryOrderFlag(),
                                            (d.getConfirmReceiveTime() != null ? sdt.format(d.getConfirmReceiveTime()) : null),
                                            d.getExtendInfo(),
                                            d.getRecordId()})
                                    .collect(Collectors.toList())
                    );
            try {
                soleID = excelGenerator.calcultateContentHash();
                excelGenerator.save(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        //取绝对路径
        File excels = new File(ExcelPath);
        String ExcelsPath = excels.getAbsolutePath();
//       压缩并加密               Tran_企业编号_业务系统标识_交易日期_唯一编号.zip
        zipPath = zip.zipEncrypt(ExcelsPath, zipPath, constants.ZIP_PASSWORD, zipName, soleID);

//         上传文件（FTP）
//    try (FileInputStream input = new FileInputStream(new File(zipPath))) {
//
//        String fileName = zipPath.substring(zipPath.lastIndexOf('/') + 1);
//        FtpUtil.upload(FTP_HOST, FTP_PORT, FTP_USER, FTP_PASS, FTP_PATH, fileName, input);
//        // 成功上传后-通知模块
//        notice.noticeAudit(BASE_PATH, API_PATH, interfaceVersion, transSeqNo, type, FTP_PATH, zipName);
//
//    } catch (FileNotFoundException e) {
//        throw new RuntimeException("压缩文件找不到 " + zipPath, e);
//    } catch (IOException e) {
//        throw new RuntimeException("读取出问题 " + e.getMessage(), e);
//    } catch (Exception e) {
//        throw new RuntimeException("FTP出问题 " + e.getMessage(), e);
//    }
        //上传文件（SFTP）
            try (FileInputStream input = new FileInputStream(new File(zipPath))){

                String fileName = zipPath.substring(zipPath.lastIndexOf('/') + 1);
                sftp.upload(constants.SFTP_HOST, constants.SFTP_PORT, constants.SFTP_USER, constants.SFTP_PASS, constants.SFTP_PATH_321, fileName, input);
                //成功上传后-通知模块
                notice.noticeAudit(constants.BASE_PATH, constants.API_PATH,interfaceVersion,transSeqNo,type, constants.SFTP_PATH_321,fileName);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("压缩文件找不到 "+zipPath,e);
            } catch (IOException e) {
                throw new RuntimeException("读取出问题 "+e.getMessage(),e);
            } catch (Exception e) {
                throw new RuntimeException("SFTP出问题 "+e.getMessage());
            }
        }
}
