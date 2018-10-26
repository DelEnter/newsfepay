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

public class SYXPayUtil{
	Logger logger = Logger.getLogger(SYXPayUtil.class.getName());
//	private static String postUrl="https://pay.yizhifubj.com/customer/i18n/i18n_pay_direct.jsp";
	private static String postUrl="https://paytest.yizhifubj.com/customer/i18n/i18n_pay_direct.jsp";
	public void get(SYXPayMessage trade){
	 NameValuePair v_mid  =new BasicNameValuePair("v_mid",trade.getV_mid());
	 NameValuePair v_oid  = new BasicNameValuePair("v_oid",trade.getV_oid());
	 NameValuePair v_rcvname  = new BasicNameValuePair("v_rcvname",trade.getV_rcvname());
	 NameValuePair v_rcvaddr  = new BasicNameValuePair("v_rcvaddr",trade.getV_rcvaddr());
	 NameValuePair v_rcvtel  = new BasicNameValuePair("v_rcvtel",trade.getV_rcvtel());
	 NameValuePair v_rcvpost  = new BasicNameValuePair("v_rcvpost",trade.getV_rcvpost());
	 NameValuePair v_amount  = new BasicNameValuePair("v_amount",trade.getV_amount());
	 NameValuePair v_ymd  = new BasicNameValuePair("v_ymd",trade.getV_ymd());
	 //NameValuePair Amount  = new BasicNameValuePair("locale",trade.getAmount());//非必填
	 //NameValuePair Signature  = new BasicNameValuePair("ET_RECEIVING_ADD",trade.getSignature());//非必填
	 NameValuePair v_orderstatus  = new BasicNameValuePair("v_orderstatus",trade.getV_orderstatus());
	 NameValuePair v_ordername  = new BasicNameValuePair("v_ordername",trade.getV_ordername());
	 NameValuePair v_moneytype  = new BasicNameValuePair("v_moneytype",trade.getV_moneytype());
	 NameValuePair v_url  = new BasicNameValuePair("v_url",trade.getV_url());
	 NameValuePair v_md5info  = new BasicNameValuePair("v_md5info",trade.getV_md5info());
	 NameValuePair v_pmode  = new BasicNameValuePair("v_pmode",trade.getV_pmode());
	 NameValuePair v_card_holder  = new BasicNameValuePair("v_card_holder",trade.getV_card_holder());
	 NameValuePair v_card_no  = new BasicNameValuePair("v_card_no",trade.getV_card_no());
	 NameValuePair v_expire_m  = new BasicNameValuePair("v_expire_m",trade.getV_expire_m());
	 NameValuePair v_expire_y  = new BasicNameValuePair("v_expire_y",trade.getV_expire_y());
	 NameValuePair v_card_cvv2  = new BasicNameValuePair("v_card_cvv2",trade.getV_card_cvv2());
	 NameValuePair v_ordip  = new BasicNameValuePair("v_ordip",trade.getV_oid());
	 NameValuePair v_billstreet  = new BasicNameValuePair("v_billstreet",trade.getV_billstreet());
	 NameValuePair v_billcity  = new BasicNameValuePair("v_billcity",trade.getV_billcity());
	 NameValuePair v_billstate  = new BasicNameValuePair("v_billstate",trade.getV_billstate());
	 NameValuePair v_billpost  = new BasicNameValuePair("v_billpost",trade.getV_billpost());
	 NameValuePair v_billcountry  = new BasicNameValuePair("v_billcountry",trade.getV_billcountry());
	 NameValuePair v_billphone  = new BasicNameValuePair("v_billphone",trade.getV_billphone());
	 NameValuePair v_billemail  = new BasicNameValuePair("v_billemail",trade.getV_billemail());
	
		List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();
		nvps1.add(v_mid);
		nvps1.add(v_oid);
		nvps1.add(v_rcvname);//70227403
		nvps1.add(v_rcvaddr);
		nvps1.add(v_rcvtel);
		nvps1.add(v_rcvpost);
		nvps1.add(v_amount);
		nvps1.add(v_ymd);
		nvps1.add(v_orderstatus);
		nvps1.add(v_ordername);

		nvps1.add(v_moneytype);
		nvps1.add(v_url);
		nvps1.add(v_md5info);
		nvps1.add(v_pmode);
		nvps1.add(v_card_holder);

		//nvps1.add(v_card_no);
		//nvps1.add(v_expire_m);
		//nvps1.add(v_expire_y);
		//nvps1.add(v_card_cvv2);
		nvps1.add(v_ordip);
		nvps1.add(v_billstreet);
		nvps1.add(v_billcity);
		nvps1.add(v_billstate);
		nvps1.add(v_billpost);
		nvps1.add(v_billcountry);
		nvps1.add(v_billphone);
		nvps1.add(v_billemail);
				
		List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();

		nvps2.add(v_mid);
		nvps2.add(v_oid);
		nvps2.add(v_rcvname);//70227403
		nvps2.add(v_rcvaddr);
		nvps2.add(v_rcvtel);
		nvps2.add(v_rcvpost);
		nvps2.add(v_amount);
		nvps2.add(v_ymd);
		nvps2.add(v_orderstatus);
		nvps2.add(v_ordername);

		nvps2.add(v_moneytype);
		nvps2.add(v_url);
		nvps2.add(v_md5info);
		nvps2.add(v_pmode);
		nvps2.add(v_card_holder);

		nvps2.add(v_card_no);
		nvps2.add(v_expire_m);
		nvps2.add(v_expire_y);
		nvps2.add(v_card_cvv2);
		nvps2.add(v_ordip);
		nvps2.add(v_billstreet);
		nvps2.add(v_billcity);
		nvps2.add(v_billstate);
		nvps2.add(v_billpost);
		nvps2.add(v_billcountry);
		nvps2.add(v_billphone);
		nvps2.add(v_billemail);
		
		try{
			PayClubUtil h = new PayClubUtil();
			logger.info("提交数据:"+nvps2.toString());
			String result = h.httpPost(nvps1,postUrl);
			logger.info("返回数据："+result);
			JSONObject jasonObject = JSONObject.fromObject(result);
            Map<String, Object> map= (Map) jasonObject;
            
            trade.setV_status(map.get("v_status")+"");
        	trade.setV_desc(map.get("v_desc")+"");
        	trade.setV_pstatus(map.get("v_pstatus")+"");
        	trade.setV_pstring(map.get("v_pstring")+"");
           
		} catch (Exception e) {
			logger.info("通道请求超时，3秒钟后重新发送。。。");
			try{
				Thread.sleep(3*1000);
				PayClubUtil h = new PayClubUtil();
				logger.info("提交数据:"+nvps2.toString());
				String result = h.httpPost(nvps1,postUrl);
				logger.info("返回数据："+result);
				JSONObject jasonObject = JSONObject.fromObject(result);
	            Map<String, Object> map= (Map) jasonObject;
	            trade.setV_status(map.get("v_status")+"");
	        	trade.setV_desc(map.get("v_desc")+"");
	        	trade.setV_pstatus(map.get("v_pstatus")+"");
	        	trade.setV_pstring(map.get("v_pstring")+"");
			} catch (Exception ex) {
				ex.printStackTrace();
            	trade.setV_status("");
            	trade.setV_desc("");
            	trade.setV_pstatus("");
            	trade.setV_pstring("");
			}
			
		}finally{
			if(StringUtils.isBlank(String.valueOf(trade.getV_status()))){
            	trade.setV_status("sfe01");
            	trade.setV_desc("timeout");
            	trade.setV_pstring("");
            	trade.setV_pstatus("");
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
