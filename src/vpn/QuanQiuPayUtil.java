package vpn;


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

import net.sf.json.JSONObject;

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

public class QuanQiuPayUtil {
	Logger logger = Logger.getLogger(QuanQiuPayUtil.class.getName());
	private static String postUrl="https://cashier.quanqiutech.com/v1/gateway";
	public void get(QuanQiuPayMessage trade){
	 NameValuePair Mode  =new BasicNameValuePair("Mode",trade.getMode());
	 NameValuePair ApplicationId  = new BasicNameValuePair("ApplicationId",trade.getApplicationId());
	 //NameValuePair AppId  = new BasicNameValuePair("AppId",trade.getAppId());
	 NameValuePair OrderId  = new BasicNameValuePair("OrderId",trade.getOrderId());
	 NameValuePair Source  = new BasicNameValuePair("Source",trade.getSource());
	 NameValuePair Email  = new BasicNameValuePair("Email",trade.getEmail());
	 NameValuePair IPAddress  = new BasicNameValuePair("IPAddress",trade.getIPAddress());
	 NameValuePair Currency  = new BasicNameValuePair("Currency",trade.getCurrency());
	 NameValuePair Amount  = new BasicNameValuePair("Amount",trade.getAmount());
	 NameValuePair Signature  = new BasicNameValuePair("Signature",trade.getSignature());
	 NameValuePair ProductSku1  = new BasicNameValuePair("ProductSku1",trade.getProductSku1());
	 NameValuePair ProductName1  = new BasicNameValuePair("ProductName1",trade.getProductName1());
	 NameValuePair ProductPrice1  = new BasicNameValuePair("ProductPrice1",trade.getProductPrice1());
	 NameValuePair ProductQuantity1  = new BasicNameValuePair("ProductQuantity1",trade.getProductQuantity1());
	 NameValuePair ShippingFirstName  = new BasicNameValuePair("ShippingFirstName",trade.getShippingFirstName());
	 NameValuePair ShippingLastName  = new BasicNameValuePair("ShippingLastName",trade.getShippingLastName());
	 NameValuePair ShippingCountry  = new BasicNameValuePair("ShippingCountry",trade.getShippingCountry());
	 NameValuePair ShippingState  = new BasicNameValuePair("ShippingState",trade.getShippingState());
	 NameValuePair ShippingCity  = new BasicNameValuePair("ShippingCity",trade.getShippingCity());
	 NameValuePair ShippingAddress1  = new BasicNameValuePair("ShippingAddress1",trade.getShippingAddress1());
	 NameValuePair ShippingTelephone  = new BasicNameValuePair("ShippingTelephone",trade.getShippingTelephone());
	 NameValuePair BillingFirstName  = new BasicNameValuePair("BillingFirstName",trade.getBillingFirstName());
	 NameValuePair BillingLastName  = new BasicNameValuePair("BillingLastName",trade.getBillingLastName());
	 NameValuePair BillingCountry  = new BasicNameValuePair("BillingCountry",trade.getBillingCountry());
	 NameValuePair BillingState  = new BasicNameValuePair("BillingState",trade.getBillingState());
	 NameValuePair BillingCity  = new BasicNameValuePair("BillingCity",trade.getBillingCity());
	 NameValuePair BillingAddress1  = new BasicNameValuePair("BillingAddress1",trade.getBillingAddress1());
	 NameValuePair BillingTelephone  = new BasicNameValuePair("BillingTelephone",trade.getBillingTelephone());
	 NameValuePair CreditCardName  = new BasicNameValuePair("CreditCardName",trade.getCreditCardName());
	 NameValuePair CreditCardNumber  = new BasicNameValuePair("CreditCardNumber",trade.getCreditCardNumber());
	 NameValuePair CreditCardExpire  = new BasicNameValuePair("CreditCardExpire",trade.getCreditCardExpire());
	 NameValuePair CreditCardCsc2  = new BasicNameValuePair("CreditCardCsc2",trade.getCreditCardCsc2());
	 NameValuePair ShippingZipcode  = new BasicNameValuePair("ShippingZipcode",trade.getShippingZipcode());
	 NameValuePair BillingZipcode  = new BasicNameValuePair("BillingZipcode",trade.getBillingZipcode());
	
		List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();
		nvps1.add(Mode);
		//nvps1.add(Version);
		//nvps1.add(AppId);//70227403
		nvps1.add(ApplicationId);
		nvps1.add(OrderId);
		nvps1.add(Source);
		nvps1.add(Email);
		nvps1.add(IPAddress);
		nvps1.add(Currency);
		nvps1.add(Amount);
		nvps1.add(Signature);
		nvps1.add(ProductSku1);
		nvps1.add(ProductName1);
		nvps1.add(ProductPrice1);
		nvps1.add(ProductQuantity1);
		nvps1.add(ShippingFirstName);
		nvps1.add(ShippingLastName);
		nvps1.add(ShippingCountry);
		nvps1.add(ShippingState);
		nvps1.add(ShippingCity);
		nvps1.add(ShippingAddress1);
		nvps1.add(ShippingTelephone);
		nvps1.add(BillingFirstName);
		nvps1.add(BillingLastName);
		nvps1.add(BillingCountry);
		nvps1.add(BillingState);
		nvps1.add(BillingCity);
		nvps1.add(BillingAddress1);
		nvps1.add(BillingTelephone);
		nvps1.add(CreditCardName);
		nvps1.add(CreditCardNumber);
		nvps1.add(CreditCardExpire);
		nvps1.add(CreditCardCsc2);
		nvps1.add(ShippingZipcode);
		nvps1.add(BillingZipcode);
		
		List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();

		nvps2.add(Mode);
		//nvps2.add(Version);
		nvps1.add(ApplicationId);
		//nvps2.add(AppId);//70227403
		nvps2.add(OrderId);
		nvps2.add(Source);
		nvps2.add(Email);
		nvps2.add(IPAddress);
		nvps2.add(Currency);
		nvps2.add(Amount);
		nvps2.add(Signature);
		nvps2.add(ProductSku1);
		nvps2.add(ProductName1);
		nvps2.add(ProductPrice1);
		nvps2.add(ProductQuantity1);
		nvps2.add(ShippingFirstName);
		nvps2.add(ShippingLastName);
		nvps2.add(ShippingCountry);
		nvps2.add(ShippingState);
		nvps2.add(ShippingCity);
		nvps2.add(ShippingAddress1);
		nvps2.add(ShippingTelephone);
		nvps2.add(BillingFirstName);
		nvps2.add(BillingLastName);
		nvps2.add(BillingCountry);
		nvps2.add(BillingState);
		nvps2.add(BillingCity);
		nvps2.add(BillingAddress1);
		nvps2.add(BillingTelephone);
		nvps2.add(ShippingZipcode);
		nvps2.add(BillingZipcode);
		
		try{
			QuanQiuPayUtil h = new QuanQiuPayUtil();
			logger.info("提交数据:"+nvps2.toString());
			String result = h.httpPost(nvps1,postUrl);
			logger.info("返回数据："+result);
			String resMsg[]=result.split("&");
	 		for(int i=0;i<resMsg.length;i++){
	 			String resPram[]=resMsg[i].split("=");
	 			if(resPram.length>1){
	 				if("status".equals(resPram[0]+"")){
	 					trade.setStatus(resPram[1]);
	 				}
	 				if("reason".equals(resPram[0]+"")){
	 					trade.setReason(resPram[1]);
	 				}
	 				if("transactionid".equals(resPram[0]+"")){
	 					trade.setTransactionId(resPram[1]);
	 				}
	 			}
	 		}
		} catch (Exception e) {
			logger.info("通道请求超时，3秒钟后重新发送。。。");
			try{
				Thread.sleep(3*1000);
				QuanQiuPayUtil h = new QuanQiuPayUtil();
				logger.info("提交数据:"+nvps2.toString());
				String result = h.httpPost(nvps1,postUrl);
				logger.info("返回数据："+result);
				String resMsg[]=result.split("&");
		 		for(int i=0;i<resMsg.length;i++){
		 			String resPram[]=resMsg[i].split("=");
		 			if(resPram.length>1){
		 				if("status".equals(resPram[0]+"")){
		 					trade.setStatus(resPram[1]);
		 				}
		 				if("reason".equals(resPram[0]+"")){
		 					trade.setReason(resPram[1]);
		 				}
		 				if("transactionid".equals(resPram[0]+"")){
		 					trade.setTransactionId(resPram[1]);
		 				}
		 			}
		 		}
			} catch (Exception ex) {
				ex.printStackTrace();
	            trade.setStatus("");
	            trade.setReason("");
			}
			
		}finally{
			if(StringUtils.isBlank(String.valueOf(trade.getStatus()))){
	            trade.setStatus("sfe01");
	            trade.setReason("timeout!");
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
		SSLContext ctx = SSLContext.getInstance("TLSv1.2");
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
