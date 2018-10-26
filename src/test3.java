import com.ecpss.util.MerchantX509Cert;
import org.apache.commons.codec.binary.Hex;
import vpn.SYXPayMessage;
import vpn.SYXPayUtil;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @version 1.0 功能 ：釆用3DES标准以模式为ECB、填充方式为PKCS7加密数据
 */
public class test3
{
	public static void main(String[] args) throws Exception {
		/*Calendar calendarUrl3 = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendarUrl3.add(Calendar.DATE,-1); // 得到一天前
		String onedate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(calendarUrl3.getTime());
		//String cardNo = "NDExMTExMTExMTExMTExMQ==";
		String cardNo = "4111111111111111";
		String sql = "select t.orderNo from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and (f.cardNo='"
						+ AES.setCarNo(cardNo)+"') and substr(t.tradestate,1,1)='0' and t.tradeTime>to_date('"
			+ onedate + "','yyyy-MM-dd hh24:mi:ss')";
		System.out.println(sql);*/
	 	/*IPassPayTemporMessage trade = new IPassPayTemporMessage();
	 	IpassPayTemporary tt = new IpassPayTemporary();
	 	trade.setOrderNo("00000");
	 	trade.setRes_orderStatus("4");
	 	trade.setRemark("Payment Success!");
	 	tt.get(trade);*/
		SYXPayMessage syx=new SYXPayMessage();
		SYXPayUtil syxp=new SYXPayUtil();
		syx.setV_mid("20051");

		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateString = formatter.format(currentTime);

		syx.setV_oid(dateString+"-"+syx.getV_mid()+"-"+"123");
		syx.setV_rcvname("123");
		syx.setV_rcvaddr("1851 Kooter Lane");
		syx.setV_rcvtel("704-429-6436");
		syx.setV_rcvpost("28262");
		syx.setV_amount("66.06");
		syx.setV_ymd(dateString);
		syx.setV_orderstatus("1");
		syx.setV_ordername("cczz");
		syx.setV_moneytype("1");
		syx.setV_url("");
		String pfxFile = "F:/cfca_private_7606.pfx";//商户私钥文件路径
		String pfxPassword = "lh121121";
		String aliasPassword = "lh121121";
		String aliasName = "{3dfcbb2d-bb3d-4350-9010-33de8cdb076c}";

		String certPath = "F:/payease_cfca.cer";
		String src = syx.getV_moneytype() + syx.getV_ymd() + syx.getV_amount() + syx.getV_rcvname() + syx.getV_oid() + syx.getV_mid() + syx.getV_url();
		String sign= MerchantX509Cert.sign(src, pfxFile, aliasName, pfxPassword, aliasPassword);
		syx.setV_pmode("17");
		syx.setV_card_holder("cczz");
		syx.setV_card_no("4111111111111111");
		syx.setV_expire_m("09");
		syx.setV_expire_y("21");
		syx.setV_card_cvv2("123");
		syx.setV_ordip("94.23.35.125");
		syx.setV_billstreet("1851 Kooter Lane");
		syx.setV_billcity("Charlotte");
		syx.setV_billstate("");
		syx.setV_billcountry("");
		syx.setV_billpost("");
		syx.setV_billphone("");
		syx.setV_billemail("");

		syxp.get(syx);
	 /*TemporarySynThread ts=new TemporarySynThread("https://api.mch.weixin.qq.com/pay/queryexchagerate","320986", "1","Payment Success!");
	 ts.start();*/
	 	/*PayClubMessage msg=new PayClubMessage();
		PayClubUtil yu=new PayClubUtil();
		msg.setP_mid("81129");
		msg.setP_account_num("40000310");
		msg.setP_mid("81094");
		msg.setP_account_num("40000148");
		msg.setP_transaction_type("SALE");
		msg.setP_order_num("12345678974156");
		msg.setP_currency("USD");
		msg.setP_amount("66.66");
		msg.setP_card_num("5222170010725408");
		msg.setP_card_expmonth("06");
		msg.setP_card_expyear("2021");
		msg.setP_card_csc("035");
		msg.setP_card_issuingbank("cardbank");
		msg.setP_firstname("ccc");
		msg.setP_lastname("zzz");
		msg.setP_user_email("index2@gmail.com");
		msg.setP_user_phone("704-429-6436");
		msg.setP_user_ipaddress("94.23.35.125");
		msg.setP_trans_url("https://www.sfepay.com");
		msg.setP_return_url("https://www.sfepay.com");
		msg.setP_bill_country("US");
		msg.setP_bill_state("Alabama");
		msg.setP_bill_city("Charlotte");
		msg.setP_bill_address("1851 Kooter Lane");
		msg.setP_bill_zip("28262");
		msg.setP_ship_firstname("lll");
		msg.setP_ship_lastname("xxx");
		msg.setP_ship_country("US");
		msg.setP_ship_state("Alabama");
		msg.setP_ship_city("Charlotte");
		msg.setP_ship_address("1851 Kooter Lane");
		msg.setP_ship_zip("28262");
		msg.setP_product_name("phone");
		msg.setP_product_num("1");
		msg.setP_product_desc("iphone XS");


		//'mid','site_id','order_id','order_amount','order_currency','api_key'.
		String key = "R2066dBx40lbbzj";
//		String key = "vf6nljz0f0x08N4";
		String sign=msg.getP_mid().trim()+msg.getP_account_num().trim()+msg.getP_order_num().trim()+msg.getP_currency().trim()+msg.getP_amount().trim()+key.trim();
		String strDes = getSha256(sign.trim());
		msg.setP_signmsg(strDes.toUpperCase());
		//msg.setBillingstate("Alabama");

		yu.get(msg);*/
		/*HttpSPostCust httpsPost=new HttpSPostCust();
		Map map=new HashMap();
		map.put("123", "321312");
		String getData="32131";
		String url="https://int1.payclub.com/payment_test.jsp";
		httpsPost.http_Post(map, url,getData);*/
/*	 TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php","320981", "1","Payment Success!");
	 ts.start();*/
/*	 testPayUtil tt = new testPayUtil();
	 WRPayMessage trade = new WRPayMessage();
	 tt.get(trade);*/
/*	 	IPassPayMessage msg=new IPassPayMessage();
		IPassPayUtil yu=new IPassPayUtil();
		msg.setMid("20331");
		msg.setOid("1234567891011");
		msg.setSite_id("370");
		msg.setOrder_amount("80.68");

		msg.setOrder_currency("USD");
		//'mid','site_id','order_id','order_amount','order_currency','api_key'.
		String sign=msg.getMid().trim()+msg.getSite_id().trim()+msg.getOid().trim()+msg.getOrder_amount().trim()+msg.getOrder_currency().trim()+"B4ecJuvYKrlQseap57BzqOYbh";
		String strDes = getSha256(sign);
		msg.setHash_info(strDes);

		msg.setCard_no("4111111111111111");
		msg.setCard_ex_month("03");
		msg.setCard_ex_year("25");
		msg.setCard_cvv("123");
		msg.setBill_email("index2@gmail.com");
		msg.setBill_phone("704-429-6436");
		msg.setBill_country("US");
		msg.setBillingstate("Alabama");
		msg.setBill_city("Charlotte");
		msg.setBill_street("1851 Kooter Lane");
		msg.setBill_zip("28262");
		msg.setBill_firstname("ccc");
		msg.setBill_lastname("zzz");
		msg.setSyn_url("www.baidu.com");
		msg.setAsyn_url("www.baidu.com");
		msg.setSource_ip("94.23.35.125");
		msg.setSource_url("www.baidu.com");//商户网站
		msg.setGateway_version("1.0");
		UUID uuid2 = UUID.randomUUID();
		msg.setUuid(uuid2.toString());

		JSONObject json = new JSONObject();
		json.put("name", "nike air");
		json.put("price", "80.68");
		json.put("num", "1");

		//msg.setBillingstate("Alabama");

		yu.get(msg);*/
	 	/*ZMTPayMessage msg=new ZMTPayMessage();
		ZMTPayUtil yu=new ZMTPayUtil();
		msg.setMerNo("1002");
		msg.setBillNo("1234567891011");
		msg.setPayCurrency("USD");
		msg.setAmount("80.68");

		msg.setCurrency("15");
		msg.setWebsite("firstshop.com");
		msg.setIe_language("EN");
		msg.setDrawee("sfepay");
		JSONObject json = new JSONObject();
		json.put("name", "nike air");
		json.put("price", "80.68");
		json.put("num", "1");

		msg.setET_GOODS("["+json.toString()+"]");//json数组

		msg.setRemark("sfepay");

		msg.setCardNo("4111111111111122");
		msg.setCardExpireMonth("09");
		msg.setCardExpireYear("2026");



		msg.setFirstName("ci");
		msg.setLastName("zg");
		msg.setAddress("1851 Kooter Lane");
		msg.setCity("Charlotte");
		msg.setIp("94.23.35.125");

		msg.setCvv2("036");
		msg.setZip("28262");
		msg.setCountry("US");//待定
		msg.setEmail("index2@gmail.com");
		msg.setPhone("704-429-6436");


		msg.setIssuingBank("bank");

		String sign=msg.getMerNo().trim()+msg.getBillNo().trim()+msg.getAmount().trim()+msg.getCurrency().trim()+msg.getCardNo().trim()+msg.getCardExpireMonth().trim()+msg.getCardExpireYear().trim() + msg.getFirstName().trim() + msg.getLastName().trim()+msg.getAddress().trim()+msg.getCity().trim()+msg.getIp().trim()+msg.getCvv2().trim()+msg.getZip().trim()+msg.getCountry().trim()+msg.getEmail().trim()+msg.getPhone().trim()+msg.getIssuingBank().trim()+"SyVIB_wW";
		String strDes = getSha256(sign);
		msg.setSHA256info(strDes);

		msg.setBillingstate("Alabama");

		yu.get(msg);*/


 	 /*MessageDigest md6;
 		BASE64Encoder base64en = new BASE64Encoder();
 		try {
 			md6 = MessageDigest.getInstance("MD5");
 			String passwords = base64en.encode(md6.digest("*728kitty96,.".toString()
 					.getBytes("utf-8")));
 			System.out.println(passwords);
 		} catch (Exception e) {

 			e.printStackTrace();
 		}
 	 MD5 md5=new MD5();

 	 String md5src = "gE{Lq_TD";
 	 System.out.println(md5.getMD5ofStr(md5src));*/
	/* DirectCarderInfoAction aa = new DirectCarderInfoAction();
	 MD5 md5 = new MD5();
	String email = "Mbluege@arcor.de";
	String ip  = "87.189.32.122";
	 HashMap hm = new HashMap();
	String phone = "4917632586197";
	String country = "DEUDE";
	String city ="Hamburg";
	String state = "Hamburg";
	String address = "Wagenfeldstr., 31";
	String backCardnum6 = "490744";
	String cardbank ="";
	long id = 1564189163L;

		// 加密串 license_key : UxQh0mA4aLqw 调试和正式运行时要加上,才会返回分数
		// 上海key: CxsRZ1xPPRbR;
		// 广州key: UxQh0mA4aLqw
		int index = email.indexOf("@");
		String domian = email.substring(index + 1, email.length());
		hm.put("license_key", "9kbrHiIOJ9ZS");
		String ip1[]=ip.split(",");
		hm.put("i", ip1[0]);
		hm.put("domain", domian);
		hm.put("emailMD5", md5.getMD5ofStr(email.toLowerCase()));
		hm.put("custPhone", phone);
		hm.put("country", country.substring(3, 5));
		hm.put("city", city);
		hm.put("region", state);
		hm.put("shipAddr", address);
		hm.put("postal", "22307");
		// hm.put("bin", cardnum);
		hm.put("bin", backCardnum6);
		hm.put("binName", cardbank);

		// standard 低级
		// premium 高级
		// 正式运行的时候要用这个 premium ; standard为调试用的标准
		hm.put("requested_type", "premium");

		Hashtable ht = aa.getmmValue(hm,id);
		String maxmindValue = (String) ht.get("values");
		String bankName = (String) ht.get("bankName");
		String bankCountry = (String) ht.get("bankCountry");
		String bankPhone = (String) ht.get("bankPhone");
		//logger.info("maxmindValue--------------" + maxmindValue);
*/
		/*GQPayMessage msg=new GQPayMessage();
		GQPayUtil yu=new GQPayUtil();
		msg.setMode("Api");
		msg.setVersion("20180208");
		msg.setAppId("70227403");
		msg.setOrderId("12345678");
		msg.setSource("https://www.sfepay.com");
		msg.setEmail("index@gmail.com");
		msg.setIPAddress("73.235.128.136");
		msg.setCurrency("CNY");
		// "70227403" + "123456" + "index@gmail.com" + "CNY" + "80.46" + "0.00" + "0.00" + "0.00" + "VKf0MK02O8iYewkb";

		msg.setAmount("80.66");
		MD5 md5=new MD5();
		String md5Hash = msg.getAppId() + msg.getOrderId() + msg.getEmail() + msg.getCurrency() + msg.getAmount() + "VKf0MK02O8iYewkb";
		msg.setSignature(md5.getMD5ofStr(md5Hash));
		//msg.setSignature("33C650F472B259FB658EDAD495896A49");
		msg.setProductSku1("ProductSku1");
		msg.setProductName1("nike max 7");
		msg.setProductPrice1("80.66");
		msg.setProductQuantity1("1");

		msg.setShippingFirstName("ccc");
		msg.setShippingLastName("zzz");
		msg.setShippingCountry("US");
		msg.setShippingState("American Samoa");//要改
		msg.setShippingCity("newyork");
		msg.setShippingAddress1("nn456");
		msg.setShippingZipcode("84000");
		msg.setShippingTelephone("15574873272");

		msg.setBillingFirstName("ccc");
		msg.setBillingLastName("zzz");
		msg.setBillingCountry("US");
		msg.setBillingState("American Samoa");//要改
		msg.setBillingCity("newyork");
		msg.setBillingAddress1("nn456");
		msg.setBillingZipcode("84000");
		msg.setBillingTelephone("15574873272");

		msg.setCreditCardName("ccczzz");
		msg.setCreditCardNumber("4111111111111111");
		msg.setCreditCardExpire("201806");
		msg.setCreditCardCsc2("035");

		yu.get(msg);*/


	 /* WPPayUtil tt = new WPPayUtil();
	 WPPayMessage t = new WPPayMessage();
	 t.setMerchantMID("4521");
	 t.setNewcardtype("4");
	 t.setCardnum("NDAyNDAyMzgzNTA2MTg3Ng==");
	 t.setCvv2("fQapzxDy6eR+d9bGz6p/VA==");
	 t.setMonth("ZRK9Q9nKpuAsmQsKgmUtyg==");
	 t.setYear("MSNRv/B5iXaQl2YKVjlQZQ==");
	 t.setCardbank("cardbankbank");
	 t.setBillNo("T1");
	 t.setAmount("80.46");
	 t.setCurrency("3");
	 t.setLanguage("en");
	 t.setHASH("61F520EAF4A1BA1848C8EEA960446815");
	 t.setReturnURL("https://www.sfepay.com");
	 t.setShippingLastName("CCC");
	 t.setShippingLastName("ZZZ");
	 t.setShippingEmail("index@gmail.com");
	 t.setShippingPhone("15574873272");
	 t.setShippingZipcode("94000");
	 t.setShippingAddress("newyork");
	 t.setShippingCity("newyork");
	 t.setShippingSstate("newyork");
	 t.setShippingCountry("USAUS");

	 t.setFirstname("CCC");
	 t.setLastname("ZZZ");
	 t.setEmail("index@gmail.com");
	 t.setPhone("15574873272");
	 t.setZipcode("94000");
	 t.setAddress("newyork");
	 t.setCity("newyork");
	 t.setState("newyork");
	 t.setCountry("USAUS");
	 t.setIpAddr("172.58.15.24");
	 t.setProducts("nike max");
	 tt.get(t);*/

	 /* testPayUtil tt = new testPayUtil();
	 WRPayMessage trade = new WRPayMessage();
	 tt.get(trade);*/
	 /*testPayUtil tt = new testPayUtil();
	 WRPayMessage trade = new WRPayMessage();
	 tt.get(trade);*/
 	 /*WRPayMessage wrp = new WRPayMessage();
 	 WRPayUtil wu = new WRPayUtil();
 	 wrp.setTransType("sales");
 	 wrp.setApiType("1");
 	 wrp.setTransModel("M");
 	 wrp.setEncryptionMode("SHA256");
 	 wrp.setCharacterSet("UTF8");
 	 wrp.setMerNo("1000041");
 	 wrp.setTerNo("88816");
 	 wrp.setAmount("72.11");
	 wrp.setCurrencyCode("USD");
	 wrp.setOrderNo("100001");
	 wrp.setGoodsString("products");
	 wrp.setCardCountry("US");
	 wrp.setCardState("California");
	 wrp.setCardCity("Fremont");
 	 wrp.setCardAddress("San Gabriel Blvd");
 	 wrp.setCardZipCode("91000");
 	 wrp.setCardFullName("Zhang san");
 	 wrp.setCardFullPhone("13333333");
 	 wrp.setCardEmail("test@test.com");
 	 wrp.setGrCountry("CN");
	 wrp.setGrState("Guagndong");
	 wrp.setGrCity("shenzhen");
	 wrp.setGrAddress("Nanshan");
	 wrp.setGrZipCode("518000");
	 wrp.setGrphoneNumber("15574873272");
	 wrp.setGrPerName("Li.si");
 	 wrp.setGrEmail("ship@test.com");
 	 //wrp.setCardNO("4111111111111111");
 	 wrp.setCardNO("5264711000319246");
 	 wrp.setExpYear("2021");
 	 wrp.setExpMonth("11");
 	 wrp.setCvv("123");
 	 wrp.setPayIP("73.235.128.136");
	 wrp.setMerMgrURL("www.sfepay.com");
	 wrp.setReturnURL("www.sfepay.com");
	 wrp.setNotifyURL("www.sfepay.com");
	 wrp.setWebInfo("Mozilla5.0 (Windows NT 6.3; WOW64; rv:40.0) Gecko20100101 Firefox40.0");
	 wrp.setLanguage("en");
 	 wrp.setMerremark("Test remark");
 	 wu.get(wrp);*/
 /*	 
 	MasaPayMessage masaM=new MasaPayMessage();
 	 MasaPayUtil masaU=new MasaPayUtil();
 	 masaM.setVersion("1.6");
 	 masaM.setMerchantId("801128553113003");
 	 masaM.setCharset("utf-8");
 	 masaM.setLanguage("en");
 	 masaM.setSignType("SHA256");
 	 masaM.setMerchantOrderNo("1230020457154");
 	 masaM.setGoodsName("shoes");
 	 masaM.setGoodsDesc("shoes 1");
 	 masaM.setOrderExchange("2");
 	 masaM.setCurrencyCode("CNY");
 	 masaM.setOrderAmount("180010");
 	 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
 	 Calendar calendarUrl = Calendar.getInstance();
 	 calendarUrl.add(Calendar.MINUTE,2); 
 	 masaM.setSubmitTime(sdf.format(new Date()));
 	 masaM.setExpiryTime(sdf.format(calendarUrl.getTime()));
 	 masaM.setBgUrl("http://www.sfepay.com/masapay");
 	 masaM.setPayMode("10");
 	 masaM.setOrgCode("MASTER");	
 	 masaM.setCardNumber("5111111111111118");
 	 masaM.setCardHolderLastName("hehe");
 	 masaM.setCardHolderFirstName("haha");
 	 masaM.setCardExpirationMonth("08");
 	 masaM.setCardExpirationYear("2019");
 	 masaM.setSecurityCode("211");
 	 masaM.setCardHolderEmail("12345@126.com");
 	 masaM.setCardHolderPhoneNumber("12345678");
 	 masaM.setBillName("haha hehe");
 	 masaM.setBillAddress("shanghai");
 	 masaM.setBillPostalCode("20012");
 	 masaM.setBillCountry("US");
 	 masaM.setBillState("Mississippi");//Mississippi
 	 masaM.setBillCity("shanghai");
 	 masaM.setBillEmail("12345@126.com");
 	 masaM.setBillPhoneNumber("12345678");
 	 masaM.setShippingName("haha hehe");
 	 masaM.setShippingAddress("shanghai");
 	 masaM.setShippingPostalCode("20012");
 	 masaM.setShippingCountry("US");
 	 masaM.setShippingState("Mississippi");
 	 masaM.setShippingCity("shanghai");
 	 masaM.setShippingEmail("12345@126.com");
 	 masaM.setShippingPhoneNumber("12345678");
 	 masaM.setDeviceFingerprintID("m"+masaM.getMerchantId()+"_"+UUID.randomUUID().toString());
 	 masaM.setRegisterUserEmail("12345@126.com");
 	 masaM.setRegisterTime(sdf.format(new Date()));
 	 masaM.setRegisterIp("202.96.209.16");
 	 masaM.setRegisterTerminal("00");
 	 masaM.setOrderIp("202.96.209.16");
 	 masaM.setOrderTerminal("00");
 	 masaM.setMd5key("K_iTBOu~");
 	 masaU.get(masaM);
 	 System.out.println(masaM.getRes_errCode());
 	 System.out.println(masaM.getRes_errMsg());
 	 System.out.println(masaM.getRes_masapayOrderNo());
 	 System.out.println(masaM.getRes_resultCode());
	 */
	 
 	/* HttpClient client = new HttpClient(); 
 	 client.setConnectionTimeout((int)20*1000);
 	 client.setTimeout((int)20*1000);
 	 PostMethod method = new PostMethod("https: open-sandbox.masapay.com/masapi/receiveMerchantOrder.htm"); 
 		try {
              NameValuePair[] query_data = new NameValuePair[6];
              query_data[0]=new NameValuePair("version","1.6");
              query_data[1]=new NameValuePair("merchantId","801124556213001");
              query_data[2]=new NameValuePair("charset","utf-8");
              query_data[3]=new NameValuePair("language","en");
              query_data[4]=new NameValuePair("signType","MD5");
              query_data[5]=new NameValuePair("merchantOrdrNo","1000001");
              method.setRequestBody(query_data);
              client.executeMethod(method);
              String content = method.getResponseBodyAsString();
              System.out.println(content);
              method.releaseConnection();
 		} catch (Exception e) {
 			  
 			e.printStackTrace();
 			method.releaseConnection();
 		}
 	 
 	 PrintWriter out = null;
      BufferedReader in = null;
      String result = "";
      
      String param= "";
      try {
          URL realUrl = new URL("https: open-sandbox.masapay.com/masapi/receiveMerchantOrder.htm");
           // 打开和URL之间的连接
          URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
*/          //conn.setRequestProperty("accept", "*/*");
         /* conn.setRequestProperty("connection", "Keep-Alive");
          conn.setRequestProperty("user-agent",
                  "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //发送POST请求必须设置如下两行
          conn.setDoOutput(true);
          conn.setDoInput(true);
            //获取URLConnection对象对应的输出流
          out = new PrintWriter(conn.getOutputStream());
           // 发送请求参数
          out.print(param);
            //flush输出流的缓冲
          out.flush();
           //定义BufferedReader输入流来读取URL的响应
          in = new BufferedReader(
                  new InputStreamReader(conn.getInputStream()));
          String line;
          while ((line = in.readLine()) != null) {
              result += line;
          }
      } catch (Exception e) {
          System.out.println("发送 POST 请求出现异常！"+e);
          e.printStackTrace();
      }
       //使用finally块来关闭输出流、输入流
      finally{
          try{
              if(out!=null){
                  out.close();
              }
              if(in!=null){
                  in.close();
              }
          }
          catch(IOException ex){
              ex.printStackTrace();
          }
      }*/
		// return result;
	}

	public static String getSha256(String strData) {
		String output = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(strData.getBytes("UTF-8"));
			output = Hex.encodeHexString(hash);
			System.out.println(output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}


}


//}  