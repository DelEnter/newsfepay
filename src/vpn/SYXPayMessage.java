package vpn;

import java.util.HashMap;
import java.util.Map;

public class SYXPayMessage {
		// 返回属性
		private String v_mid;
		private String v_oid;
		private String v_rcvname;
		private String v_rcvaddr;
		private String v_rcvtel;
		private String v_rcvpost;
		private String v_amount;
		private String v_ymd;
		private String v_orderstatus;
		private String v_ordername;
		private String v_moneytype;
		private String v_url;
		private String v_md5info;
		private String v_pmode;
		private String v_card_holder;
		private String v_card_no;
		private String v_expire_m;
		private String v_expire_y;
		private String v_card_cvv2;
		private String v_ordip;
		private String v_billstreet;
		private String v_billcity;
		private String v_billstate;
		private String v_billpost;
		private String v_billcountry;
		private String v_billphone;	
		private String v_billemail;		
		
		//private String billNo;
		private String v_status;
		//private String amount;
		private String v_desc;
		private String v_pstatus;
		private String v_pstring;
	
		public String getV_mid() {
			return v_mid;
		}

		public void setV_mid(String v_mid) {
			this.v_mid = v_mid;
		}

		public String getV_oid() {
			return v_oid;
		}

		public void setV_oid(String v_oid) {
			this.v_oid = v_oid;
		}

		public String getV_rcvname() {
			return v_rcvname;
		}

		public void setV_rcvname(String v_rcvname) {
			this.v_rcvname = v_rcvname;
		}

		public String getV_rcvaddr() {
			return v_rcvaddr;
		}

		public void setV_rcvaddr(String v_rcvaddr) {
			this.v_rcvaddr = v_rcvaddr;
		}

		public String getV_rcvtel() {
			return v_rcvtel;
		}

		public void setV_rcvtel(String v_rcvtel) {
			this.v_rcvtel = v_rcvtel;
		}

		public String getV_rcvpost() {
			return v_rcvpost;
		}

		public void setV_rcvpost(String v_rcvpost) {
			this.v_rcvpost = v_rcvpost;
		}

		public String getV_amount() {
			return v_amount;
		}

		public void setV_amount(String v_amount) {
			this.v_amount = v_amount;
		}

		public String getV_ymd() {
			return v_ymd;
		}

		public void setV_ymd(String v_ymd) {
			this.v_ymd = v_ymd;
		}

		public String getV_orderstatus() {
			return v_orderstatus;
		}

		public void setV_orderstatus(String v_orderstatus) {
			this.v_orderstatus = v_orderstatus;
		}

		public String getV_ordername() {
			return v_ordername;
		}

		public void setV_ordername(String v_ordername) {
			this.v_ordername = v_ordername;
		}

		public String getV_moneytype() {
			return v_moneytype;
		}

		public void setV_moneytype(String v_moneytype) {
			this.v_moneytype = v_moneytype;
		}

		public String getV_url() {
			return v_url;
		}

		public void setV_url(String v_url) {
			this.v_url = v_url;
		}

		public String getV_md5info() {
			return v_md5info;
		}

		public void setV_md5info(String v_md5info) {
			this.v_md5info = v_md5info;
		}

		public String getV_pmode() {
			return v_pmode;
		}

		public void setV_pmode(String v_pmode) {
			this.v_pmode = v_pmode;
		}

		public String getV_card_holder() {
			return v_card_holder;
		}

		public void setV_card_holder(String v_card_holder) {
			this.v_card_holder = v_card_holder;
		}

		public String getV_card_no() {
			return v_card_no;
		}

		public void setV_card_no(String v_card_no) {
			this.v_card_no = v_card_no;
		}

		public String getV_expire_m() {
			return v_expire_m;
		}

		public void setV_expire_m(String v_expire_m) {
			this.v_expire_m = v_expire_m;
		}

		public String getV_expire_y() {
			return v_expire_y;
		}

		public void setV_expire_y(String v_expire_y) {
			this.v_expire_y = v_expire_y;
		}

		public String getV_card_cvv2() {
			return v_card_cvv2;
		}

		public void setV_card_cvv2(String v_card_cvv2) {
			this.v_card_cvv2 = v_card_cvv2;
		}

		public String getV_ordip() {
			return v_ordip;
		}

		public void setV_ordip(String v_ordip) {
			this.v_ordip = v_ordip;
		}

		public String getV_billstreet() {
			return v_billstreet;
		}

		public void setV_billstreet(String v_billstreet) {
			this.v_billstreet = v_billstreet;
		}

		public String getV_billcity() {
			return v_billcity;
		}

		public void setV_billcity(String v_billcity) {
			this.v_billcity = v_billcity;
		}

		public String getV_billpost() {
			return v_billpost;
		}

		public void setV_billpost(String v_billpost) {
			this.v_billpost = v_billpost;
		}

		public String getV_billcountry() {
			return v_billcountry;
		}

		public void setV_billcountry(String billcountry) {
			//this.v_billcountry = v_billcountry;
			Map<String,String> Country = new HashMap<String,String>();
			Country.put("004", "AF");
			Country.put("008", "AL");
			Country.put("020", "AD");
			Country.put("024", "AI");
            Country.put("051", "AM");
            Country.put("533", "AW");
            Country.put("036", "AU");
            Country.put("682", "AE");
            Country.put("032", "AR");
            Country.put("028", "AG");
            Country.put("040", "AT");

            Country.put("031", "AZ");
            Country.put("530", "AN");
			Country.put("052", "BB");
			Country.put("050", "BD");
			Country.put("056", "BE");
			Country.put("084", "BZ");
			Country.put("204", "BJ");
			Country.put("064", "BT");
			Country.put("068", "BO");
			Country.put("070", "BA");

			Country.put("072", "BW");
			Country.put("096", "BN");
			Country.put("100", "BG");
			Country.put("048", "BH");
			
			Country.put("060", "BM");
			Country.put("076", "BR");
			Country.put("044", "BS");
			Country.put("854", "BF");
			Country.put("108", "BI");
			Country.put("120", "CM");
			
			Country.put("124", "CA");
			Country.put("132", "CV");
			Country.put("140", "CF");
			Country.put("174", "KM");
			Country.put("178", "CG");
			
			
			Country.put("756", "CH");
			Country.put("152", "CL");
			Country.put("170", "CO");
			Country.put("188", "CR");
			Country.put("196", "CY");
			
			Country.put("203", "CZ");
			Country.put("276", "DE");
			Country.put("208", "DK");
			Country.put("262", "DJ");
			Country.put("012", "DZ");

			Country.put("214", "DO");
			Country.put("218", "EC");
			Country.put("818", "EG");
			Country.put("232", "ER");
			Country.put("233", "EE");
			
			Country.put("231", "ET");
			Country.put("724", "ES");
			Country.put("732", "EH");
			Country.put("242", "FJ");
			Country.put("250", "FR");

			Country.put("246", "FI");
			Country.put("254", "GF");
			Country.put("266", "GA");
			Country.put("270", "GM");
			Country.put("226", "GN");
			
			Country.put("268", "GE");
			Country.put("288", "GH");
			Country.put("292", "GI");
			Country.put("308", "GD");
			Country.put("300", "GR");

			Country.put("312", "GP");
			Country.put("320", "GT");
			Country.put("254", "GY");
			Country.put("624", "GW");
			Country.put("826", "GB");
			
			Country.put("332", "HT");
			Country.put("340", "HN");
			Country.put("344", "HK");
			Country.put("348", "HU");
			Country.put("360", "ID");

			Country.put("372", "IE");
			Country.put("376", "IL");
			Country.put("356", "IN");
			Country.put("352", "IS");
			Country.put("380", "IT");
			
			Country.put("388", "JM");
			Country.put("392", "JP");
			Country.put("400", "JO");
			Country.put("398", "KZ");
			Country.put("404", "KE");

			Country.put("417", "KG");
			Country.put("410", "KR");
			Country.put("414", "KW");
			Country.put("659", "KN");
			Country.put("422", "LB");
			
			Country.put("434", "LY");
			Country.put("438", "LI");
			Country.put("144", "LK");
			Country.put("440", "LT");
			Country.put("442", "LU");

			Country.put("428", "LV");
			Country.put("662", "LC");
			Country.put("492", "MC");
			Country.put("446", "MO");
			Country.put("450", "MG");
			
			Country.put("454", "MW");
			Country.put("462", "MV");
			Country.put("466", "ML");
			Country.put("470", "MT");
			Country.put("474", "MQ");

			Country.put("478", "MR");
			Country.put("480", "MU");
			Country.put("484", "MX");
			Country.put("458", "MY");
			Country.put("498", "MD");
			
			Country.put("496", "MN");
			Country.put("504", "MA");
			Country.put("508", "MZ");
			Country.put("516", "NA");
			Country.put("524", "NP");

			Country.put("528", "NL");
			Country.put("558", "NI");
			Country.put("562", "NE");
			Country.put("566", "NG");
			Country.put("578", "NO");
			
			Country.put("554", "NZ");
			Country.put("512", "OM");
			Country.put("586", "PK");
			Country.put("591", "PA");
			Country.put("598", "PG");

			Country.put("600", "PY");
			Country.put("604", "PE");
			Country.put("608", "PH");
			Country.put("616", "PL");
			Country.put("620", "PT");
			
			Country.put("634", "QA");
			Country.put("642", "RO");
			Country.put("643", "RU");
			Country.put("646", "RW");
			Country.put("674", "SM");

			Country.put("678", "ST");
			Country.put("682", "SA");
			Country.put("686", "SN");
			Country.put("748", "SZ");
			Country.put("690", "SC");
			
			Country.put("694", "SL");
			Country.put("706", "SO");
			Country.put("740", "SR");
			Country.put("752", "SE");
			Country.put("702", "SG");
			
			Country.put("703", "SK");
			Country.put("705", "SI");
			Country.put("222", "SV");
			
			Country.put("760", "SY");
			Country.put("762", "TJ");
			Country.put("834", "TZ");
			
			Country.put("764", "TH");
			Country.put("768", "TG");
			Country.put("788", "TN");
			
			
			Country.put("792", "TR");
			Country.put("780", "TT");
			Country.put("158", "TW");

			Country.put("795", "TM");
			Country.put("796", "TC");
			Country.put("800", "UG");
			Country.put("804", "UA");
			Country.put("840", "US");
			Country.put("858", "UY");
			Country.put("860", "UZ");
			Country.put("862", "VE");
			Country.put("670", "VC");
			Country.put("704", "VN");
			Country.put("887", "YE");
			Country.put("894", "ZM");
			Country.put("710", "ZA");
            
            for(String key : Country.keySet()){ 
                String value = Country.get(key); 
                if(billcountry.equals(value)){
                	v_billcountry = key;
                	break;
                }else{
                	v_billcountry = billcountry;
                }
            } 
		}

		public String getV_billphone() {
			return v_billphone;
		}

		public void setV_billphone(String v_billphone) {
			this.v_billphone = v_billphone;
		}

		public String getV_billemail() {
			return v_billemail;
		}

		public void setV_billemail(String v_billemail) {
			this.v_billemail = v_billemail;
		}

		public String getV_status() {
			return v_status;
		}

		public void setV_status(String v_status) {
			this.v_status = v_status;
		}

		public String getV_desc() {
			return v_desc;
		}

		public void setV_desc(String v_desc) {
			this.v_desc = v_desc;
		}

		public String getV_pstatus() {
			return v_pstatus;
		}

		public void setV_pstatus(String v_pstatus) {
			this.v_pstatus = v_pstatus;
		}

		public String getV_pstring() {
			return v_pstring;
		}

		public void setV_pstring(String v_pstring) {
			this.v_pstring = v_pstring;
		}

		public String getV_billstate() {
			return v_billstate;
		}

		public void setV_billstate(String billingstate) {
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
                if(billingstate.equals(value)){
                	v_billstate = key;
                	break;
                }else{
                	v_billstate = billingstate;
                }
            }           
		}
}
