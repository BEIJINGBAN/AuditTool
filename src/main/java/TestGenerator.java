import domian.ExpectBill;
import domian.ReconBill;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestGenerator {
    public static List<ExpectBill> expectBillsGenerator() {
        List<ExpectBill> expectBills = new ArrayList<ExpectBill>();

        ExpectBill splitPayment = new ExpectBill();
        splitPayment.setOrgCode("120251010224000");
        splitPayment.setMerchantNo("12");
        //120251010224000
        splitPayment.setMerchantOrderNo("20240522120251010224037001786");
        splitPayment.setChannelSerialNo("03120241430610345044015");
        splitPayment.setOriginalBaoRongSerialNo("03120241430610345044015");
        splitPayment.setBaoRongSerialNo("03120241430610345044015");
        splitPayment.setChannelName("工行");
        splitPayment.setTradeDirection("收入");
        splitPayment.setTradeDate(new Date());
        splitPayment.setSettleDate(new Date());
        splitPayment.setSplitMerchantNo("12");
        splitPayment.setTradeAmount("200.00");
        splitPayment.setFee("1.20");
        splitPayment.setPayMethod("银行卡");
        splitPayment.setTerminalId("120251010224037");
        splitPayment.setSplitFlag("不分账");
        splitPayment.setCallbackUrl("http://10.60.45.65:9091/fileUpload/callback");//TODO 全局常量

        // 添加到列表
        expectBills.add(splitPayment);
        return expectBills;
    }

    public static List<ReconBill> reconBillsGeneretor(){
        List<ReconBill> reconBills= new ArrayList<ReconBill>();

        //TODO 一个生成类
        // 测试数据
        ReconBill test1 = new ReconBill();
        ReconBill test2 = new ReconBill();

        test1.setOrderOrgCode("S111");
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
        test1.setExtendInfo("{更新了 之后 之后 之后 之后}");

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
        test2.setConfirmReceiveTime(new Date());
        test2.setExtendInfo("{ 更新了 之后 之后 之后 之后}");

        reconBills.add(test1);
        reconBills.add(test2);

        return reconBills;
    }
}
