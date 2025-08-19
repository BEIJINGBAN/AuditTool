package domian;

import java.util.Date;
//3.2.1 应收账单
public class ReconBill {

//	订单组织代码	String(32)	Y	订单所属组织
    private String orderOrgCode;
//	收单商户号	String(32)	C	用于收单的商户号，线上支付时必填
    private String acquiringMerchantNo;
//	业务系统标识	String(2)	Y	用于识别是企业方哪个业务系统推送过来的，由稽核系统分配
    private String bizSystemCode;
//	详见：业务系统标识字典
//	应收单号	String(64)	Y	企业业务系统里的业务订单号
    private String receivableOrderNo;
//	商户订单号	String(64)	Y	商户订单号，在企业系统里就是请求渠道的单号也就是支付单号，退款时为退款单号
    private String merchantOrderNo;
//	渠道流水号	String(32)	N	走保融渠道的为易企收流水号，非保融渠道的为渠道侧返回的流水号
    private String channelSerialNo;
//	收支方向	String(1)	Y	1-支出（退款） ；2-收入（收款）
    private String incomeExpenseFlag;
//	金额	String	Y	以分为单位，只支持传正值
    private String amount;
//	交易时间	Date	Y	向支付渠道下单成功的时间，比如微信支付回调通知支付成功的时间，时间格式：yyyy-MM-dd HH:mm:ss
    private Date tradeTime;
//	原交易日期	Date	C	日期格式：yyyy-MM-dd
    private Date originalTradeTime;
//	主要针对现金上缴转线上的场景，需要传递原始现金收款单据的交易日期

//	企业收银方式	String(32)	Y	企业方的收银方式，需要企业先提供给易企收
    private String enterpriseCashieType;
//	备注	String(256)	N	可填写本次交易的描述信息，比如：对方信息、业务信息等内容
    private String remark;
//	对方户名	String(128)	N	当单据为线下转账应收时，用于和网银流水之间进行到账核对
    private String counterpartyName;
//	当单据为银行托收时，用于和渠道账单之间进行到账核对

//	对方账号	String(64)	N	当单据为线下转账应收时，用于和网银流水之间进行到账核对
    private String counterpartyAccount;
//	当单据为银行托收时，用于和渠道账单之间进行到账核对

//	对方银行	String(64)	N	当单据为线下转账应收时，用于和网银流水之间进行到账核对
    private String counterpartyBank;
//	当单据为银行托收时，用于和渠道账单之间进行到账核对

//	用途	String(256)	N	稽核辅助字段
    private String purpose;
//	稽核结果通知地址	String(256)	N	稽核完成后的通知地址
    private String auditNotifyUrl;
//	发货类订单标识	String(1)	Y	1-是；0-否，文件上传时传1或0
    private String deliveryOrderFlag;
//	确认收货时间	Date	C	当发货类订单标识=1时必传，时间格式：yyyy-MM-dd HH:mm:ss
    private Date confirmReceiveTime;
//	扩展信息	json	N	企业自定义的扩展信息，json格式，根据稽核业务场景需要而补充的额外信息，比如客户名称、房间信息等等，最多支持20个扩展字段。所有字段均按原值展示，如果存在金额类型的字段，统一按【元】为单位传值。示例：{	"extend1": "商品名称",	"extend2": "购买数量",	"extend3": "运费",	"extend4": "单位金额",	"extend5": "购买人名称",	"extend6": "客户ID",	"extend20": "其他自定义字段，最多支持20个"}
    private String extendInfo;
    //未来用于唯一找到一条记录的方法ID
    private String recordId;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getOrderOrgCode() {
        return orderOrgCode;
    }

    public void setOrderOrgCode(String orderOrgCode) {
        this.orderOrgCode = orderOrgCode;
    }

    public String getAcquiringMerchantNo() {
        return acquiringMerchantNo;
    }

    public void setAcquiringMerchantNo(String acquiringMerchantNo) {
        this.acquiringMerchantNo = acquiringMerchantNo;
    }

    public String getBizSystemCode() {
        return bizSystemCode;
    }

    public void setBizSystemCode(String bizSystemCode) {
        this.bizSystemCode = bizSystemCode;
    }

    public String getReceivableOrderNo() {
        return receivableOrderNo;
    }

    public void setReceivableOrderNo(String receivableOrderNo) {
        this.receivableOrderNo = receivableOrderNo;
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

    public String getIncomeExpenseFlag() {
        return incomeExpenseFlag;
    }

    public void setIncomeExpenseFlag(String incomeExpenseFlag) {
        this.incomeExpenseFlag = incomeExpenseFlag;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Date getOriginalTradeTime() {
        return originalTradeTime;
    }

    public void setOriginalTradeTime(Date originalTradeTime) {
        this.originalTradeTime = originalTradeTime;
    }

    public String getEnterpriseCashieType() {
        return enterpriseCashieType;
    }

    public void setEnterpriseCashieType(String enterpriseCashieType) {
        this.enterpriseCashieType = enterpriseCashieType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public String getCounterpartyAccount() {
        return counterpartyAccount;
    }

    public void setCounterpartyAccount(String counterpartyAccount) {
        this.counterpartyAccount = counterpartyAccount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCounterpartyBank() {
        return counterpartyBank;
    }

    public void setCounterpartyBank(String counterpartyBank) {
        this.counterpartyBank = counterpartyBank;
    }

    public String getAuditNotifyUrl() {
        return auditNotifyUrl;
    }

    public void setAuditNotifyUrl(String auditNotifyUrl) {
        this.auditNotifyUrl = auditNotifyUrl;
    }

    public String getDeliveryOrderFlag() {
        return deliveryOrderFlag;
    }

    public void setDeliveryOrderFlag(String deliveryOrderFlag) {
        this.deliveryOrderFlag = deliveryOrderFlag;
    }

    public Date getConfirmReceiveTime() {
        return confirmReceiveTime;
    }

    public void setConfirmReceiveTime(Date confirmReceiveTime) {
        this.confirmReceiveTime = confirmReceiveTime;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }
}

