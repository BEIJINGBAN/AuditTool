import config.constants;
import domian.ExpectBill;
import Util.*;
import domian.ReconBill;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/*
3.2.7对账单
*/

public class ExpectUpload {
    public static void expectBillUpload(int excelSize) throws FileNotFoundException, IOException, NoSuchAlgorithmException {


        //规范日期格式
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //TODO 以下数据根据具体生产信息输入
        //文件命名数据
        String billName = "收账单对账数据";
        String tranTime = sdf.format(new Date());

        //文件地址
        String zipPath = "./src/";
        String ExcelPath = "./src/main/audit327/";

        //通知信息
        String interfaceVersion = "1.0";
        String transSeqNo = "NoABC";
        String type = "2";

        //TODO 以下为生成的Excel测试数据，实际生产请用真实数据代替
        List<ExpectBill> infos = TestGenerator.expectBillsGenerator();

        //TODO 以下为系统功能逻辑
        //应收上传
        String filePath = "";
        String excelName = billName + "_" + tranTime;
        String zipName = billName + "_" + tranTime;
        String soleId = "";

        List<String> soleIDs = new ArrayList<>();
        //生成回溯ID忽略的字段
        String[] ignoreFiellds ={"tradeTime", "confirmReceiveTime","recordId"};
        //TODO 唯一值生成不规范 (已解决）
        for (ExpectBill data : infos) {
            String recordId = ExcelUtil.recordIdGenerate(data, ignoreFiellds);
            data.setRecordId(recordId);
        }
        //TODO 空指针 (已解决)
        LinkedHashMap<String, List<ExpectBill>> infoMap = ExcelUtil.PartitionExcel(infos, excelSize,excelName);
        if (infoMap == null) {
            return;
        }
        for (Map.Entry<String, List<ExpectBill>> entry : infoMap.entrySet()) {
            filePath = ExcelPath + entry.getKey();
            List<ExpectBill> data = entry.getValue();
            try {
                ExcelUtil.ExcelGenerator excelGenerator = ExcelUtil.ExcelGenerator.create()
                        //表一
                        .sheet("收单对账数据",
                                Arrays.asList(
                                        new String[]{
                                        "订单组织代码",
                                        "收单商户号",
                                        "商户订单号",
                                        "保融交易流水号",
                                        "渠道流水号",
                                        "第三方交易流水号",
                                        "原保融交易流水号",
                                        "渠道名称",
                                        "交易方向",
                                        "交易日期",
                                        "清算日期",
                                        "交易金额",
                                        "手续费",
                                        "支付方式",
                                        "终端号",
                                        "分账标记",
                                        "分账订单组织",
                                        "分账入账方商户号",
                                        "分账明细流水号",
                                        "分账金额",
                                        "分账子单手续费",
                                        "商家优惠金额",
                                        "平台优惠金额",
                                        "渠道优惠金额",
                                        "对方账号",
                                        "对方户名",
                                        "回调通知地址"
                                },
                                    new String[]{
                                        "必填，填写组织编号",
                                            "必填",
                                            "必填" ,
                                            "必填， 如果非保融渠道交易，填写收单渠道返回的流水号" ,
                                            "必填,指收单渠道返回的流水号" ,
                                            "非必填，指支付宝、微信的流水号" ,
                                            "退款时必填，指原收款保融交易流水号" ,
                                            "必填，详见<渠道名称>sheet",
                                            "必填，收入/支出" ,
                                            "必填，格式为yyyy-mm-dd，例如2023-11-16" ,
                                            "必填，格式为yyyy-mm-dd，例如2023-11-16" ,
                                            "必填，单位元，保留2位小数" ,
                                            "必填，单位元，保留2位小数" ,
                                            "必填，支付宝/微信/银联/数字人民币/网关支付/银行卡" ,
                                            "非必填" ,
                                            "必填，不分账/支付时分账/支付后分账" ,
                                            "支付时分账交易必填，填写组织编号" ,
                                            "支付时分账交易必填" ,
                                            "支付时分账交易必填" ,
                                            "支付时分账交易必填" ,
                                            "支付时分账交易必填" ,
                                            "非必填" ,
                                            "非必填" ,
                                            "非必填" ,
                                            "非必填" ,
                                            "非必填" ,
                                            "必填，如果不传，数据有问题时结果没法回传，数据消息会丢失。长度256"
                                    }
                                ),
                                data.stream()
                                        .map(b -> new String[]{
                                                b.getOrgCode(),
                                                b.getMerchantNo(),
                                                b.getMerchantOrderNo(),
                                                b.getBaoRongSerialNo(),
                                                b.getChannelSerialNo(),
                                                b.getThirdPartySerialNo(),
                                                b.getOriginalBaoRongSerialNo(),
                                                b.getChannelName(),
                                                b.getTradeDirection(),
                                                (b.getTradeDate() != null ? sdt.format(b.getTradeDate()) : ""),
                                                (b.getSettleDate() != null ? sdt.format(b.getSettleDate()) : ""),
                                                b.getTradeAmount(),
                                                b.getFee(),
                                                b.getPayMethod(),
                                                b.getTerminalId(),
                                                b.getSplitFlag(),
                                                b.getSplitOrgCode(),
                                                b.getSplitMerchantNo(),
                                                b.getSplitDetailSerialNo(),
                                                b.getSplitAmount(),
                                                b.getSplitFee(),
                                                b.getMerchantDiscount(),
                                                b.getPlatformDiscount(),
                                                b.getChannelDiscount(),
                                                b.getCounterpartyAccountNew(),
                                                b.getCounterpartyNameNew(),
                                                b.getCallbackUrl(),
                                                b.getRecordId()
                                        })
                                        .collect(Collectors.toList())
                        )
                        //表二
                        .sheet("渠道名称",
                                //标题
                                new String[]{
                                        "渠道名称"},
                                Arrays.asList(
                                        new String[]{"工行"},
                                        new String[]{"农行"},
                                        new String[]{"建行"},
                                        new String[]{"中信银行"},
                                        new String[]{"平安"},
                                        new String[]{"招行"},
                                        new String[]{"建行-惠市宝"},
                                        new String[]{"拉卡拉"},
                                        new String[]{"北京银商"},
                                        new String[]{"银商总部"},
                                        new String[]{"富友"},
                                        new String[]{"支付宝"},
                                        new String[]{"微信"},
                                        new String[]{"中信-POS"},
                                        new String[]{"兴业"},
                                        new String[]{"快钱"},
                                        new String[]{"杭州银行"},
                                        new String[]{"通联"},
                                        new String[]{"国通星驿"},
                                        new String[]{"汇付天下"},
                                        new String[]{"易生"}
                                )
                        );
                excelGenerator.save(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (FileInputStream input = new FileInputStream(new File(filePath))){

            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            SftpUtil.upload(constants.SFTP_HOST, constants.SFTP_PORT, constants.SFTP_USER, constants.SFTP_PASS, constants.SFTP_PATH_327, fileName, input);
            //成功上传后-通知模块
            NoticeUtil.noticeAudit(constants.BASE_PATH, constants.API_PATH,interfaceVersion,transSeqNo,type, constants.SFTP_PATH_327,fileName);
        } catch (
                FileNotFoundException e) {
            throw new RuntimeException("压缩文件找不到 "+zipPath,e);
        } catch (
                IOException e) {
            throw new RuntimeException("读取出问题 "+e.getMessage(),e);
        } catch (Exception e) {
            throw new RuntimeException("SFTP出问题 "+e.getMessage());
        }
        }
    }
