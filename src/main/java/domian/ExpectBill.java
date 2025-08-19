package domian;

import java.util.Date;
//3.2.7 对账单
public class ExpectBill {
    // 组织
    private String orgCode;

    // 商户号
    private String merchantNo;

    // 商户订单号
    private String merchantOrderNo;

    // 渠道流水号
    private String channelSerialNo;

    // 保融交易流水号
    private String baoRongSerialNo;

    // 第三方交易流水号
    private String thirdPartySerialNo;

    // 原保融交易流水号
    private String originalBaoRongSerialNo;

    // 渠道名称
    private String channelName;

    // 交易方向
    private String tradeDirection;

    // 交易日期
    private Date tradeDate;

    // 清算日期
    private Date settleDate;

    // 交易金额
    private String tradeAmount;

    // 手续费
    private String fee;

    // 支付方式
    private String payMethod;

    // 终端号
    private String terminalId;

    // 分账标记
    private String splitFlag;

    // 分账订单组织
    private String splitOrgCode;

    // 分账入账方商户号
    private String splitMerchantNo;

    // 分账明细流水号
    private String splitDetailSerialNo;

    // 分账金额
    private String splitAmount;

    // 分账子单手续费
    private String splitFee;

    // 商家优惠金额
    private String merchantDiscount;

    // 平台优惠金额
    private String platformDiscount;

    // 渠道优惠金额
    private String channelDiscount;

    // 对方账号
    private String counterpartyAccountNew; // 注意：与上面的 counterpartyAccount 冗余，可合并或重命名

    // 对方户名
    private String counterpartyNameNew; // 同上，注意字段重复

    // 回调通知地址
    private String callbackUrl;

    //用于唯一的找到该条数据
    private String recordId;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getBaoRongSerialNo() {
        return baoRongSerialNo;
    }

    public void setBaoRongSerialNo(String baoRongSerialNo) {
        this.baoRongSerialNo = baoRongSerialNo;
    }

    public String getOriginalBaoRongSerialNo() {
        return originalBaoRongSerialNo;
    }

    public void setOriginalBaoRongSerialNo(String originalBaoRongSerialNo) {
        this.originalBaoRongSerialNo = originalBaoRongSerialNo;
    }

    public String getThirdPartySerialNo() {
        return thirdPartySerialNo;
    }

    public void setThirdPartySerialNo(String thirdPartySerialNo) {
        this.thirdPartySerialNo = thirdPartySerialNo;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTradeDirection() {
        return tradeDirection;
    }

    public void setTradeDirection(String tradeDirection) {
        this.tradeDirection = tradeDirection;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getSplitFlag() {
        return splitFlag;
    }

    public void setSplitFlag(String splitFlag) {
        this.splitFlag = splitFlag;
    }

    public String getSplitOrgCode() {
        return splitOrgCode;
    }

    public void setSplitOrgCode(String splitOrgCode) {
        this.splitOrgCode = splitOrgCode;
    }

    public String getSplitMerchantNo() {
        return splitMerchantNo;
    }

    public void setSplitMerchantNo(String splitMerchantNo) {
        this.splitMerchantNo = splitMerchantNo;
    }

    public String getSplitDetailSerialNo() {
        return splitDetailSerialNo;
    }

    public void setSplitDetailSerialNo(String splitDetailSerialNo) {
        this.splitDetailSerialNo = splitDetailSerialNo;
    }

    public String getSplitAmount() {
        return splitAmount;
    }

    public void setSplitAmount(String splitAmount) {
        this.splitAmount = splitAmount;
    }

    public String getSplitFee() {
        return splitFee;
    }

    public void setSplitFee(String splitFee) {
        this.splitFee = splitFee;
    }

    public String getMerchantDiscount() {
        return merchantDiscount;
    }

    public void setMerchantDiscount(String merchantDiscount) {
        this.merchantDiscount = merchantDiscount;
    }

    public String getPlatformDiscount() {
        return platformDiscount;
    }

    public void setPlatformDiscount(String platformDiscount) {
        this.platformDiscount = platformDiscount;
    }

    public String getChannelDiscount() {
        return channelDiscount;
    }

    public void setChannelDiscount(String channelDiscount) {
        this.channelDiscount = channelDiscount;
    }

    public String getCounterpartyAccountNew() {
        return counterpartyAccountNew;
    }

    public void setCounterpartyAccountNew(String counterpartyAccountNew) {
        this.counterpartyAccountNew = counterpartyAccountNew;
    }

    public String getCounterpartyNameNew() {
        return counterpartyNameNew;
    }

    public void setCounterpartyNameNew(String counterpartyNameNew) {
        this.counterpartyNameNew = counterpartyNameNew;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public void setMerchantOrderNo(String merchantOrderNo) {
        this.merchantOrderNo = merchantOrderNo;
    }

    public String getChannelSerialNo() {
        return channelSerialNo;
    }

    public void setChannelSerialNo(String channelSerialNo) {
        this.channelSerialNo = channelSerialNo;
    }
}
