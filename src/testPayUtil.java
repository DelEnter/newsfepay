


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import vpn.VpnUtil_Moto;
import vpn.WRPayMessage;

public class testPayUtil {
	Logger logger = Logger.getLogger(testPayUtil.class.getName());
	private static String postUrl="https://www.sfepay.com/directPayment";
	//private static String postUrl="http://localhost:8090/Gofpay";
	//private static String postUrl="http://www.sfepay.com/listtrade";
	//private static String postUrl="http://localhost:8090/listtrade";
	public void get(WRPayMessage trade){
		//MerNo +BillNo +Currency+Amount +Language+ReturnURL+MD5key
	 NameValuePair MerNo  =new BasicNameValuePair("MerNo","3604");
	 NameValuePair newcardtype  = new BasicNameValuePair("newcardtype","5");
	 NameValuePair cardnum  = new BasicNameValuePair("cardnum","NDExMTExMTExMTExMTExMQ==");//NTExMTExMTExMTExMTExOA==  5111111111111111118  NDExMTExMTExMTExMTExMQ==  41111111111111111
	 NameValuePair cvv2  = new BasicNameValuePair("cvv2","MTIz");
	 NameValuePair month  = new BasicNameValuePair("month","MDY=");
	 NameValuePair year  = new BasicNameValuePair("year","MjE=");
	 NameValuePair cardbank  = new BasicNameValuePair("cardbank","cardbankbank");
	 NameValuePair BillNo  = new BasicNameValuePair("BillNo","TEST1212122");
	 NameValuePair Amount  = new BasicNameValuePair("Amount","80.46");
	 NameValuePair Currency  = new BasicNameValuePair("Currency","1");
	 NameValuePair Language  = new BasicNameValuePair("Language","EN");
	 NameValuePair MD5info  = new BasicNameValuePair("MD5info","3BD41D79C75BA5BDB903B2ADE12CAD58");
	 NameValuePair ReturnURL  = new BasicNameValuePair("ReturnURL","https://www.sfepay.com/payresult.jsp");
	 NameValuePair shippingFirstName  = new BasicNameValuePair("shippingFirstName","CCC");
	 NameValuePair shippingLastName  = new BasicNameValuePair("shippingLastName","ZZZ");
	 NameValuePair shippingEmail  = new BasicNameValuePair("shippingEmail","index@gmail.com");
	 NameValuePair shippingPhone  = new BasicNameValuePair("shippingPhone","15574873272");
	 NameValuePair shippingZipcode  = new BasicNameValuePair("shippingZipcode","94000");
	 NameValuePair shippingAddress  = new BasicNameValuePair("shippingAddress","newyork");
	 NameValuePair shippingCity  = new BasicNameValuePair("shippingCity","newyork");
	 NameValuePair shippingSstate  = new BasicNameValuePair("shippingSstate","AL");
	 NameValuePair shippingCountry  = new BasicNameValuePair("shippingCountry","USAUS");
	 NameValuePair products  = new BasicNameValuePair("products","nike max");
	 NameValuePair firstname  = new BasicNameValuePair("firstname","CCC");
	 NameValuePair lastname  = new BasicNameValuePair("lastname","ZZZ");
	 NameValuePair email  = new BasicNameValuePair("email","index@gmail.com");
	 NameValuePair phone  = new BasicNameValuePair("phone","15574873272");
	 NameValuePair zipcode  = new BasicNameValuePair("zipcode","94000");
	 NameValuePair address  = new BasicNameValuePair("address","newyork");
	 NameValuePair city  = new BasicNameValuePair("expMonth","12");
	 NameValuePair state  = new BasicNameValuePair("state","AL");
	 NameValuePair country  = new BasicNameValuePair("country","USAUS");
	 NameValuePair addIp  = new BasicNameValuePair("addIp","172.58.15.24");
	 NameValuePair sfeVersion  = new BasicNameValuePair("sfeVersion","ZKF1.1.1");
		/*NameValuePair MerNo  =new BasicNameValuePair("merNo","4110");
		NameValuePair starttime  =new BasicNameValuePair("starttime","2018-01-24");
		NameValuePair endtime  =new BasicNameValuePair("endtime","2018-01-25");//{"merNo":"3604","orders":[{"orderNo":"36041651","expressNo":"15615sdf","amount":"36.54"}]}
		
		List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();
		nvps1.add(MerNo);
		nvps1.add(starttime);
		nvps1.add(endtime);
		List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
		nvps2.add(MerNo);
		nvps2.add(starttime);
		nvps2.add(endtime);*/
		
		/*JSONObject json = new JSONObject();
		
		JSONArray arry = new JSONArray();
		
		Map<String, Object> map2 = new HashedMap();
		map2.put("orderNo", "36041651");
		map2.put("expressNo", "15615sdf");
		map2.put("amount", "36.54");
		arry.add(map2);
		json.put("orders", arry);
		NameValuePair MerNo  =new BasicNameValuePair("merNo","3604");
		NameValuePair orders  =new BasicNameValuePair("orders","");
		
		List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();
		nvps1.add(MerNo);
		
		List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
		nvps2.add(MerNo);*/
		
		List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();
		nvps1.add(MerNo);
		nvps1.add(newcardtype);
		nvps1.add(cardnum);
		nvps1.add(cvv2);
		nvps1.add(month);
		nvps1.add(year);
		nvps1.add(cardbank);
		nvps1.add(BillNo);
		nvps1.add(Amount);
		nvps1.add(Currency);
		nvps1.add(Language);
		nvps1.add(MD5info);
		nvps1.add(ReturnURL);
		nvps1.add(shippingFirstName);
		nvps1.add(shippingLastName);
		nvps1.add(shippingEmail);
		nvps1.add(shippingPhone);
		nvps1.add(shippingZipcode);
		nvps1.add(shippingAddress);
		nvps1.add(shippingCity);
		nvps1.add(shippingSstate);
		nvps1.add(shippingCountry);
		nvps1.add(products);
		nvps1.add(firstname);
		nvps1.add(lastname);
		nvps1.add(email);
		nvps1.add(phone);
		nvps1.add(zipcode);
		nvps1.add(address);
		nvps1.add(city);
		nvps1.add(state);
		nvps1.add(country);
		nvps1.add(addIp);
		nvps1.add(sfeVersion);		
		List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
		nvps2.add(MerNo);
		nvps2.add(newcardtype);
		nvps2.add(cardbank);
		nvps2.add(BillNo);
		nvps2.add(Amount);
		nvps2.add(Currency);
		nvps2.add(Language);
		nvps2.add(MD5info);
		nvps2.add(ReturnURL);
		nvps2.add(shippingFirstName);
		nvps2.add(shippingLastName);
		nvps2.add(shippingEmail);
		nvps2.add(shippingPhone);
		nvps2.add(shippingZipcode);
		nvps2.add(shippingAddress);
		nvps2.add(shippingCity);
		nvps2.add(shippingSstate);
		nvps2.add(shippingCountry);
		nvps2.add(products);
		nvps2.add(firstname);
		nvps2.add(lastname);
		nvps2.add(email);
		nvps2.add(phone);
		nvps2.add(zipcode);
		nvps2.add(address);
		nvps2.add(city);
		nvps2.add(state);
		nvps2.add(country);
		nvps2.add(addIp);
		nvps2.add(sfeVersion);	
		try{
			testPayUtil h = new testPayUtil();
			logger.info("提交数据:"+nvps2.toString());
			String result = h.httpPost(nvps1,postUrl);
			logger.info("返回数据："+result);
			JSONObject jasonObject = JSONObject.fromObject(result);
            Map<String, Object> map= (Map) jasonObject;
            trade.setRespCode(map.get("respCode")+"");
            trade.setTradeNo(map.get("tradeNo")+"");
            trade.setRespMsg(map.get("respMsg")+"");
            trade.setAcquirer(map.get("acquirer")+"");
		} catch (Exception e) {
			logger.info("通道请求超时，3秒钟后重新发送。。。");
			/*try{
				Thread.sleep(3*1000);
				testPayUtil h = new testPayUtil();
				logger.info("提交数据:"+nvps2.toString());
				String result = h.httpPost(nvps1,postUrl);
				logger.info("返回数据："+result);
				JSONObject jasonObject = JSONObject.fromObject(result);
	            Map<String, Object> map= (Map) jasonObject;
	            trade.setRespCode(map.get("respCode")+"");
	            trade.setTradeNo(map.get("tradeNo")+"");
	            trade.setRespMsg(map.get("respMsg")+"");
	            trade.setAcquirer(map.get("acquirer")+"");
			} catch (Exception ex) {
				ex.printStackTrace();
				trade.setRespCode("");
				trade.setRespMsg("");
			}*/
			
		}finally{
			if(StringUtils.isBlank(String.valueOf(trade.getRespCode()))){
				trade.setRespCode("sfe01");
				trade.setRespMsg("timeout!");
			}
		}
	}
	public String httpPost(List<NameValuePair> nvps, String url)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		String result = "";
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 125*1000);
		HttpConnectionParams.setSoTimeout(httpclient.getParams(), 125*2000);
		if(url.toLowerCase().startsWith("https")){
			httpclient = getInstance(httpclient);
		}
		HttpResponse response = httpclient.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			result = EntityUtils.toString(entity);
		}
		if(statusCode==301||statusCode==302){
			Header[] headers = response.getHeaders("Location");
			for(Header header:headers){
				if(header.getName().equals("Location")){
					String res = this.httpPost(nvps, header.getValue());
					return res;
				}
			}
		}
		return result;
	}
	
	private static X509TrustManager trustManager = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] xcs, String string)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] xcs, String string)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	public static HttpClient getInstance(HttpClient client)
			throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx,
				SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("https", 443, ssf));
		ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(
				registry);
		return new DefaultHttpClient(mgr, client.getParams());
	}
	
}
