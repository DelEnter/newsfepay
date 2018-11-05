package vpn;

import java.util.HashMap;
import java.util.Map;

public class QuanQiuPayMessage {
		// 返回属性
		private String Mode;
		private String ApplicationId;
		private String OrderId;
		private String Email;
		//private String Source;
		private String IPAddress;
		private String Currency;
		private String Amount;
		private String Signature;
		
		private String ProductSku1;
		private String ProductName1;
		private String ProductPrice1;
		private String ProductQuantity1;
		
		private String ShippingFirstName;
		private String ShippingLastName;
		private String ShippingCountry;
		private String ShippingState;
		private String ShippingCity;
		private String ShippingAddress1;		
		private String ShippingZipcode;
		private String ShippingTelephone;
		
		private String BillingFirstName;
		private String BillingLastName;
		private String BillingCountry;
		private String BillingState;
		private String BillingCity;
		private String BillingAddress1;
		private String BillingZipcode;
		private String BillingTelephone;
		
		private String CreditCardName;
		private String CreditCardNumber;
		private String CreditCardExpire;
		private String CreditCardCsc2;
		
		
		private String TransactionId;
		private String Status;
		private String Reason;
		private String ExtraData;
		private String Url;
		private String TransactionCurrency;
		private String TransactionAmount;
		
		public String getMode() {
			return Mode;
		}
		public void setMode(String mode) {
			Mode = mode;
		}
		
		public String getApplicationId() {
			return ApplicationId;
		}
		public void setApplicationId(String applicationId) {
			ApplicationId = applicationId;
		}
		public String getOrderId() {
			return OrderId;
		}
		public void setOrderId(String orderId) {
			OrderId = orderId;
		}
		public String getTransactionCurrency() {
			return TransactionCurrency;
		}
		public void setTransactionCurrency(String transactionCurrency) {
			TransactionCurrency = transactionCurrency;
		}
		public String getTransactionAmount() {
			return TransactionAmount;
		}
		public void setTransactionAmount(String transactionAmount) {
			TransactionAmount = transactionAmount;
		}
		public String getEmail() {
			return Email;
		}
		public void setEmail(String email) {
			Email = email;
		}
		public String getIPAddress() {
			return IPAddress;
		}
		public void setIPAddress(String iPAddress) {
			IPAddress = iPAddress;
		}
		public String getCurrency() {
			return Currency;
		}
		public void setCurrency(String currency) {
			Currency = currency;
		}
		public String getAmount() {
			return Amount;
		}
		public void setAmount(String amount) {
			Amount = amount;
		}
		public String getSignature() {
			return Signature;
		}
		public void setSignature(String signature) {
			Signature = signature;
		}
		public String getProductSku1() {
			return ProductSku1;
		}
		public void setProductSku1(String productSku1) {
			ProductSku1 = productSku1;
		}
		public String getProductName1() {
			return ProductName1;
		}
		public void setProductName1(String productName1) {
			ProductName1 = productName1;
		}
		public String getProductPrice1() {
			return ProductPrice1;
		}
		public void setProductPrice1(String productPrice1) {
			ProductPrice1 = productPrice1;
		}
		public String getProductQuantity1() {
			return ProductQuantity1;
		}
		public void setProductQuantity1(String productQuantity1) {
			ProductQuantity1 = productQuantity1;
		}
		public String getShippingFirstName() {
			return ShippingFirstName;
		}
		public void setShippingFirstName(String shippingFirstName) {
			ShippingFirstName = shippingFirstName;
		}
		public String getShippingLastName() {
			return ShippingLastName;
		}
		public void setShippingLastName(String shippingLastName) {
			ShippingLastName = shippingLastName;
		}
		public String getShippingCountry() {
			return ShippingCountry;
		}
		public void setShippingCountry(String shippingCountry) {
			ShippingCountry = shippingCountry;
		}
		public String getShippingState() {
			return ShippingState;
		}
		public void setShippingState(String shippingState) {
			Map<String,String> USStates = new HashMap<String,String>();
			USStates.put("AL", "Alabama");
            USStates.put("AK", "Alaska");
            USStates.put("AS", "American Samoa");
            USStates.put("AZ", "Arizona");
            USStates.put("AR", "Arkansas");
            USStates.put("AF", "Armed Forces Africa");
            USStates.put("AA", "Armed Forces Americas");
            USStates.put("AC", "Armed Forces Canada");
            USStates.put("AE", "Armed Forces Europe");
            USStates.put("AM", "Armed Forces Middle East");
            USStates.put("AP", "Armed Forces Pacific");
            USStates.put("CA", "California");
            USStates.put("CO", "Colorado");
            USStates.put("CT", "Connecticut");
            USStates.put("DE", "Delaware");
            USStates.put("DC", "District of Columbia");
            USStates.put("FM", "Federated States of Micronesia");
            USStates.put("FL", "Florida");
            USStates.put("GA", "Georgia");
            USStates.put("GU", "Guam");
            USStates.put("HI", "Hawaii");
            USStates.put("ID", "Idaho");
            USStates.put("IL", "Illinois");
            USStates.put("IN", "Indiana");
            USStates.put("IA", "Iowa");
            USStates.put("KS", "Kansas");
            USStates.put("KY", "Kentucky");
            USStates.put("LA", "Louisiana");
            USStates.put("ME", "Maine");
            USStates.put("MH", "Marshall Islands");
            USStates.put("MD", "Maryland");
            USStates.put("MA", "Massachusetts");
            USStates.put("MI", "Michigan");
            USStates.put("MN", "Minnesota");
            USStates.put("MS", "Mississippi");
            USStates.put("MO", "Missouri");
            USStates.put("MT", "Montana");
            USStates.put("NE", "Nebraska");
            USStates.put("NV", "Nevada");
            USStates.put("NH", "New Hampshire");
            USStates.put("NJ", "New Jersey");
            USStates.put("NM", "New Mexico");
            USStates.put("NY", "New York");
            USStates.put("NC", "North Carolina");
            USStates.put("ND", "North Dakota");
            USStates.put("MP", "Northern Mariana Islands");
            USStates.put("OH", "Ohio");
            USStates.put("OK", "Oklahoma");
            USStates.put("OR", "Oregon");
            USStates.put("PW", "Palau");
            USStates.put("PA", "Pennsylvania");
            USStates.put("PR", "Puerto Rico");
            USStates.put("RI", "Rhode Island");
            USStates.put("SC", "South Carolina");
            USStates.put("SD", "South Dakota");
            USStates.put("TN", "Tennessee");
            USStates.put("TX", "Texas");
            USStates.put("UT", "Utah");
            USStates.put("VT", "Vermont");
            USStates.put("VI", "Virgin Islands");
            USStates.put("VA", "Virginia");
            USStates.put("WA", "Washington");
            USStates.put("WV", "West Virginia");
            USStates.put("WI", "Wisconsin");
            USStates.put("WY", "Wyoming");
            USStates.put("AB", "Alberta");
            USStates.put("BC", "British Columbia");
            USStates.put("MB", "Manitoba");
            USStates.put("NB", "New Brunswick");
            USStates.put("NL", "Newfoundland and Labrador");
            USStates.put("NS", "Nova Scotia");
            USStates.put("ON", "Ontario");
            USStates.put("PE", "Prince Edward Island");
            USStates.put("QC", "Quebec");
            USStates.put("SK", "Saskatchewan");
            USStates.put("NT", "Northwest Territories");
            USStates.put("NU", "Nunavut");
            USStates.put("YT", "Yukon Territory");
            for(String key : USStates.keySet()){ 
                String value = USStates.get(key); 
                if(shippingState.equals(value)){
                	ShippingState = key;
                	break;
                }else{
                	ShippingState = shippingState;
                }
            }           
		}
		public String getShippingCity() {
			return ShippingCity;
		}
		public void setShippingCity(String shippingCity) {
			ShippingCity = shippingCity;
		}
		public String getShippingAddress1() {
			return ShippingAddress1;
		}
		public void setShippingAddress1(String shippingAddress1) {
			ShippingAddress1 = shippingAddress1;
		}
		public String getShippingTelephone() {
			return ShippingTelephone;
		}
		public void setShippingTelephone(String shippingTelephone) {
			ShippingTelephone = shippingTelephone;
		}
		public String getBillingFirstName() {
			return BillingFirstName;
		}
		public void setBillingFirstName(String billingFirstName) {
			BillingFirstName = billingFirstName;
		}
		public String getBillingLastName() {
			return BillingLastName;
		}
		public void setBillingLastName(String billingLastName) {
			BillingLastName = billingLastName;
		}
		public String getBillingCountry() {
			return BillingCountry;
		}
		public void setBillingCountry(String billingCountry) {
			BillingCountry = billingCountry;
		}
		public String getBillingState() {
			return BillingState;
		}
		public void setBillingState(String billingState) {
			Map<String,String> USStates = new HashMap<String,String>();
			USStates.put("AL", "Alabama");
            USStates.put("AK", "Alaska");
            USStates.put("AS", "American Samoa");
            USStates.put("AZ", "Arizona");
            USStates.put("AR", "Arkansas");
            USStates.put("AF", "Armed Forces Africa");
            USStates.put("AA", "Armed Forces Americas");
            USStates.put("AC", "Armed Forces Canada");
            USStates.put("AE", "Armed Forces Europe");
            USStates.put("AM", "Armed Forces Middle East");
            USStates.put("AP", "Armed Forces Pacific");
            USStates.put("CA", "California");
            USStates.put("CO", "Colorado");
            USStates.put("CT", "Connecticut");
            USStates.put("DE", "Delaware");
            USStates.put("DC", "District of Columbia");
            USStates.put("FM", "Federated States of Micronesia");
            USStates.put("FL", "Florida");
            USStates.put("GA", "Georgia");
            USStates.put("GU", "Guam");
            USStates.put("HI", "Hawaii");
            USStates.put("ID", "Idaho");
            USStates.put("IL", "Illinois");
            USStates.put("IN", "Indiana");
            USStates.put("IA", "Iowa");
            USStates.put("KS", "Kansas");
            USStates.put("KY", "Kentucky");
            USStates.put("LA", "Louisiana");
            USStates.put("ME", "Maine");
            USStates.put("MH", "Marshall Islands");
            USStates.put("MD", "Maryland");
            USStates.put("MA", "Massachusetts");
            USStates.put("MI", "Michigan");
            USStates.put("MN", "Minnesota");
            USStates.put("MS", "Mississippi");
            USStates.put("MO", "Missouri");
            USStates.put("MT", "Montana");
            USStates.put("NE", "Nebraska");
            USStates.put("NV", "Nevada");
            USStates.put("NH", "New Hampshire");
            USStates.put("NJ", "New Jersey");
            USStates.put("NM", "New Mexico");
            USStates.put("NY", "New York");
            USStates.put("NC", "North Carolina");
            USStates.put("ND", "North Dakota");
            USStates.put("MP", "Northern Mariana Islands");
            USStates.put("OH", "Ohio");
            USStates.put("OK", "Oklahoma");
            USStates.put("OR", "Oregon");
            USStates.put("PW", "Palau");
            USStates.put("PA", "Pennsylvania");
            USStates.put("PR", "Puerto Rico");
            USStates.put("RI", "Rhode Island");
            USStates.put("SC", "South Carolina");
            USStates.put("SD", "South Dakota");
            USStates.put("TN", "Tennessee");
            USStates.put("TX", "Texas");
            USStates.put("UT", "Utah");
            USStates.put("VT", "Vermont");
            USStates.put("VI", "Virgin Islands");
            USStates.put("VA", "Virginia");
            USStates.put("WA", "Washington");
            USStates.put("WV", "West Virginia");
            USStates.put("WI", "Wisconsin");
            USStates.put("WY", "Wyoming");
            USStates.put("AB", "Alberta");
            USStates.put("BC", "British Columbia");
            USStates.put("MB", "Manitoba");
            USStates.put("NB", "New Brunswick");
            USStates.put("NL", "Newfoundland and Labrador");
            USStates.put("NS", "Nova Scotia");
            USStates.put("ON", "Ontario");
            USStates.put("PE", "Prince Edward Island");
            USStates.put("QC", "Quebec");
            USStates.put("SK", "Saskatchewan");
            USStates.put("NT", "Northwest Territories");
            USStates.put("NU", "Nunavut");
            USStates.put("YT", "Yukon Territory");
            for(String key : USStates.keySet()){ 
                String value = USStates.get(key); 
                if(billingState.equals(value)){
                	BillingState = key;
                	break;
                }else{
                	BillingState = billingState;
                }
            }           			
		}
		public String getBillingCity() {
			return BillingCity;
		}
		public void setBillingCity(String billingCity) {
			BillingCity = billingCity;
		}
		public String getBillingAddress1() {
			return BillingAddress1;
		}
		public void setBillingAddress1(String billingAddress1) {
			BillingAddress1 = billingAddress1;
		}
		public String getBillingTelephone() {
			return BillingTelephone;
		}
		public void setBillingTelephone(String billingTelephone) {
			BillingTelephone = billingTelephone;
		}
		public String getCreditCardName() {
			return CreditCardName;
		}
		public void setCreditCardName(String creditCardName) {
			CreditCardName = creditCardName;
		}
		public String getCreditCardNumber() {
			return CreditCardNumber;
		}
		public void setCreditCardNumber(String creditCardNumber) {
			CreditCardNumber = creditCardNumber;
		}
		public String getCreditCardExpire() {
			return CreditCardExpire;
		}
		public void setCreditCardExpire(String creditCardExpire) {
			CreditCardExpire = creditCardExpire;
		}
		public String getCreditCardCsc2() {
			return CreditCardCsc2;
		}
		public void setCreditCardCsc2(String creditCardCsc2) {
			CreditCardCsc2 = creditCardCsc2;
		}
		public String getTransactionId() {
			return TransactionId;
		}
		public void setTransactionId(String transactionId) {
			TransactionId = transactionId;
		}
		public String getStatus() {
			return Status;
		}
		public void setStatus(String status) {
			Status = status;
		}
		public String getReason() {
			return Reason;
		}
		public void setReason(String reason) {
			Reason = reason;
		}
		public String getExtraData() {
			return ExtraData;
		}
		public void setExtraData(String extraData) {
			ExtraData = extraData;
		}
		public String getUrl() {
			return Url;
		}
		public void setUrl(String url) {
			Url = url;
		}
		public String getShippingZipcode() {
			return ShippingZipcode;
		}
		public void setShippingZipcode(String shippingZipcode) {
			ShippingZipcode = shippingZipcode;
		}
		public String getBillingZipcode() {
			return BillingZipcode;
		}
		public void setBillingZipcode(String billingZipcode) {
			BillingZipcode = billingZipcode;
		}
		
}
