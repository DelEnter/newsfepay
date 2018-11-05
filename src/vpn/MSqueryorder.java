package vpn;


public class MSqueryorder {
		// 返回属性
		private String version="1.0";
		private String merchantId="801128553113051";
		private String charset="utf-8";
		
		private String language="en";
		private String signType="SHA256";
		private String queryType="1";
		
		private String masapayOrderNo;
		private String merchantOrderNo;
		private String startTime;
		
		private String endTime;
		private String signMsg;
		
		
		private String errCode;
		private String errMsg;
		private String recordCount;
		//private String merchantOrderNo;
		
		private String orderAmount;
		private String orderTime;
		private String dealTime;
		
		private String status;
		private String payMode;
		private String orgCode;
		
		private String paidAmount;
		private String payCurrencyCode;
		private String rates;
		private String riskLevel;
		
		private String OrdersignMsg;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getMerchantId() {
			return merchantId;
		}

		public void setMerchantId(String merchantId) {
			this.merchantId = merchantId;
		}

		public String getCharset() {
			return charset;
		}

		public void setCharset(String charset) {
			this.charset = charset;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getSignType() {
			return signType;
		}

		public void setSignType(String signType) {
			this.signType = signType;
		}

		public String getQueryType() {
			return queryType;
		}

		public void setQueryType(String queryType) {
			this.queryType = queryType;
		}

		public String getMasapayOrderNo() {
			return masapayOrderNo;
		}

		public void setMasapayOrderNo(String masapayOrderNo) {
			this.masapayOrderNo = masapayOrderNo;
		}

		public String getMerchantOrderNo() {
			return merchantOrderNo;
		}

		public void setMerchantOrderNo(String merchantOrderNo) {
			this.merchantOrderNo = merchantOrderNo;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public String getSignMsg() {
			return signMsg;
		}

		public void setSignMsg(String signMsg) {
			this.signMsg = signMsg;
		}

		public String getErrCode() {
			return errCode;
		}

		public void setErrCode(String errCode) {
			this.errCode = errCode;
		}

		public String getErrMsg() {
			return errMsg;
		}

		public void setErrMsg(String errMsg) {
			this.errMsg = errMsg;
		}

		public String getRecordCount() {
			return recordCount;
		}

		public void setRecordCount(String recordCount) {
			this.recordCount = recordCount;
		}

		public String getOrderAmount() {
			return orderAmount;
		}

		public void setOrderAmount(String orderAmount) {
			this.orderAmount = orderAmount;
		}

		public String getOrderTime() {
			return orderTime;
		}

		public void setOrderTime(String orderTime) {
			this.orderTime = orderTime;
		}

		public String getDealTime() {
			return dealTime;
		}

		public void setDealTime(String dealTime) {
			this.dealTime = dealTime;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getPayMode() {
			return payMode;
		}

		public void setPayMode(String payMode) {
			this.payMode = payMode;
		}

		public String getOrgCode() {
			return orgCode;
		}

		public void setOrgCode(String orgCode) {
			this.orgCode = orgCode;
		}

		public String getPaidAmount() {
			return paidAmount;
		}

		public void setPaidAmount(String paidAmount) {
			this.paidAmount = paidAmount;
		}

		public String getPayCurrencyCode() {
			return payCurrencyCode;
		}

		public void setPayCurrencyCode(String payCurrencyCode) {
			this.payCurrencyCode = payCurrencyCode;
		}

		public String getRates() {
			return rates;
		}

		public void setRates(String rates) {
			this.rates = rates;
		}

		public String getRiskLevel() {
			return riskLevel;
		}

		public void setRiskLevel(String riskLevel) {
			this.riskLevel = riskLevel;
		}

		public String getOrdersignMsg() {
			return OrdersignMsg;
		}

		public void setOrdersignMsg(String ordersignMsg) {
			OrdersignMsg = ordersignMsg;
		}

}
