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

/*
     应收账单上传 3.2.1
*/

public class ReconUpload {
    public static void reconBillUpload() throws IOException, NoSuchAlgorithmException {
        //日期格式
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //TODO 以下数据根据具体生产信息输入
        //上送文件命名
        String fileType = "Tran";
        String entCode = "ZN330301";
        String tranTime = sdf.format(new Date());
        String tranType = "转账";
        String opCode = constants.OPERATE_ADD;//操作码 ADD UPDATE DEL NOTICE
        String businessTag = "CRM";

        //本地保存Excel文件和Zip文件的路径
        String zipPath = "./src/";
        String excelPath = "./src/main/audit321/";

        // 通知信息
        String transSeqNo = "NoABC";
        String interfaceVersion = "1.0";//接口版本

        //上传的每个Excel的行数
        int excelSize = 1;

        //TODO  测试数据生成 (已解决)
        List<ReconBill> infos= TestGenerator.reconBillsGeneretor();

        //TODO 以下为系统功能逻辑

        //名字组装
        String excelName = fileType + "_" + entCode + "_" + tranTime + "_" + tranType + "_" + opCode;
        String zipName = fileType + "_" + entCode + "_" + businessTag + "_" + tranTime;
        //zip文件的唯一后缀
        String zipSoleID = "";
        //收集每个Excel文件的soleID
        List<String> soleIDs = new ArrayList<>();

        //TODO 空指针 (已解决)
        LinkedHashMap<String, List<ReconBill>> infoMap = ExcelUtil.PartitionExcel(infos,constants.EXCEL_SIZE);
        if (infoMap == null) {
            return;
        }
        for (Map.Entry<String, List<ReconBill>> entry : infoMap.entrySet()) {
            String filePath = excelPath + excelName + entry.getKey();
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
                                            ("666"),
//                                            (d.getTradeTime() != null ? sdt.format(d.getTradeTime()) : null),
                                            ("666"),
//                                            (d.getOriginalTradeTime() != null ? sdt.format(d.getOriginalTradeTime()) : null),
                                            d.getEnterpriseCashieType(),
                                            d.getRemark(),
                                            d.getCounterpartyName(),
                                            d.getCounterpartyAccount(),
                                            d.getCounterpartyBank(),
                                            d.getPurpose(),
                                            d.getAuditNotifyUrl(),
                                            d.getDeliveryOrderFlag(),
                                            ("666"),
//                                            (d.getConfirmReceiveTime() != null ? sdt.format(d.getConfirmReceiveTime()) : null),
                                            d.getExtendInfo(),
                                            d.getRecordId()})
                                    .collect(Collectors.toList())
                    );
            try {
                soleIDs.add(excelGenerator.calcultateContentHash());
                excelGenerator.save(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        zipSoleID =ZipUtil.zipHash(soleIDs);
        //取绝对路径
        File excels = new File(excelPath);
        String excelsPath = excels.getAbsolutePath();
        //压缩加密
        //zip文件名模板： Tran_企业编号_业务系统标识_交易日期_唯一编号.zip
        zipPath = ZipUtil.zipEncrypt(excelsPath, zipPath, constants.ZIP_PASSWORD, zipName, zipSoleID);
        //上传文件（SFTP）
            try (FileInputStream input = new FileInputStream(new File(zipPath))){
                String fileName = zipPath.substring(zipPath.lastIndexOf('/') + 1);
                SftpUtil.upload(constants.SFTP_HOST, constants.SFTP_PORT, constants.SFTP_USER, constants.SFTP_PASS, constants.SFTP_PATH_321, fileName, input);
                //成功上传后-通知模块
                NoticeUtil.noticeAudit(interfaceVersion,transSeqNo,constants.NOTIC_TYPE_RECON, constants.SFTP_PATH_321,fileName);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("压缩文件找不到 "+zipPath,e);
            } catch (IOException e) {
                throw new RuntimeException("读取出问题 "+e.getMessage(),e);
            } catch (Exception e) {
                throw new RuntimeException("SFTP出问题 "+e.getMessage());
            }
        }
}
