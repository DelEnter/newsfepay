package com.ecpss.action.tradedispose;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import jxl.Workbook;

import org.apache.commons.lang.StringUtils;

import com.ecpss.action.BaseAction;
import com.ecpss.excel.builder.RowResult;
import com.ecpss.excel.builder.jexcel.JExcelRowObjectBuilder;
import com.ecpss.excel.rule.impl.CellRuleImpl;
import com.ecpss.excel.rule.impl.RowRuleImpl;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.util.AES;
import com.ecpss.util.DateUtil;
import com.ecpss.util.StatusUtil;
import com.ecpss.util.StringUtil;
import com.ecpss.vo.ImportCheck;

public class CheckTradeAction extends BaseAction{
	
	private List<ImportCheck> lostTradeList = new ArrayList<ImportCheck>(); //��������---> ������,����û��
	private List<ImportCheck> someTradeList = new ArrayList<ImportCheck>(); //�ظ�����---> 
	private List<ImportCheck> exceptionTradeList = new ArrayList<ImportCheck>(); //�쳣����---> ��Ȩ��+����  ��ѯ������...�жϽ���Ƿ�һ��
	private List<ImportCheck> noCheckTradeList = new ArrayList<ImportCheck>(); //�ѹ��ҽ���,�ٴ��ϴ�δ����---> �ѹ���״̬�Ľ���,�ٴ��ϴ�Ϊ
	
	/**
	 * �ϴ��ļ���
	 */
	private File VIPFileName;  
	
	private File nanyangFillName;
	
	/**
	 * VIP�ϴ�����ҳ��
	 * @return
	 */
	public String toVIPCheckPage(){
		return SUCCESS;
	}
	
	/**
	 * vipͨ������
	 * @return
	 */
	public String VIPCheck(){
		try {
			if(VIPFileName!=null){
				Long begin=System.currentTimeMillis();
				Workbook workBook=Workbook.getWorkbook(VIPFileName);
				RowRuleImpl rowRule = new RowRuleImpl();
				rowRule.addCellRule(new CellRuleImpl("A", "authorizationNo"));
				rowRule.addCellRule(new CellRuleImpl("B", "cardNo"));
				rowRule.addCellRule(new CellRuleImpl("D", "amount"));
				rowRule.addCellRule(new CellRuleImpl("E", "terminalNo"));
				JExcelRowObjectBuilder reveBuilder = new JExcelRowObjectBuilder();
				reveBuilder.setSheet(workBook.getSheet(0));
				reveBuilder.setTargetClass(ImportCheck.class);
				reveBuilder.setRule(0, workBook.getSheet(0).getRows(), rowRule);
				
				RowResult<ImportCheck>[] cons = reveBuilder.parseExcel();
				ImportCheck ic;
				int count = 0;  //�ɹ�����
				int gouduiguo = 0; //���ҹ���
				int exceptionc = 0;
				String query = "";
				//InternationalTradeinfo ti;
				List<InternationalTradeinfo> tiList;
				List<String> someTrade = new ArrayList<String>();
				for (RowResult<ImportCheck> rowResult : cons) {
					ic = rowResult.getRowObject();
					if(StringUtils.isNotBlank(ic.getAmount())){
						ic.setAmount(StringUtil.deleteAll(ic.getAmount(), ','));
						ic.setTradeAmount(Double.valueOf(ic.getAmount()));
					}
					query="select ti from InternationalTradeinfo ti,InternationalCardholdersInfo ci " +
							"where ci.tradeId=ti.id " +
							"and ti.VIPAuthorizationNo='"+ic.getAuthorizationNo().trim()+"' " +
									"and ci.cardNo='"+AES.setCarNo(ic.getCardNo().trim())+"'";
					tiList = this.commonService.list(query);
					if(tiList.size()>0){
						for (InternationalTradeinfo ti : tiList) {
							//������null..������֤
							if(ti!=null){
								ic.setOrderNo(ti.getOrderNo());
								ic.setTradeDate(ti.getTradeTime());
								//δ����,�ɹ�����--->�޸�״̬Ϊ�ѹ���
								if(ti.getTradeState().substring(4, 5).equals("0") && ti.getTradeState().substring(0, 1).equals("1")){
									int[] indexArray = reduplicateIndex(someTrade, ic.getOrderNo());
						    		if(indexArray.length>0){
						    			this.someTradeList.add(ic);
						    		}
						    		someTrade.add(ic.getOrderNo());
									//�����ͬ
									if(ti.getRmbAmount()==ic.getTradeAmount().doubleValue() || CheckTradeAction.validateAmount(ti.getRmbAmount(), ic.getTradeAmount())){
										//��������
										ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 5, "1"));
										ti.setGouduiTime(new Date());
										this.commonService.update(ti);
										count++;
										continue;
									}
									exceptionTradeList.add(ic);
									exceptionc++;
								}
								//��ѯ������,�����ѹ���
								else if(ti.getTradeState().substring(4, 5).equals("1")){
									noCheckTradeList.add(ic);
									gouduiguo++;
								}
								//��̨ʧ��  ���гɹ�
								else if(ti.getTradeState().substring(0, 1).equals("0")){
									exceptionTradeList.add(ic);
									exceptionc++;
								}
							}else{
								//����null�����ڵ���
								lostTradeList.add(ic);
							}
						}
					}
				}
				Long end=System.currentTimeMillis() - begin;
				this.messageAction="�ϴ����,��ʱ:"+end+"����.���ҳɹ�"+count+"��.�Ѿ����ҹ�����δ����"+gouduiguo+"��.�쳣��"+exceptionc+"��";
			}
			return SUCCESS;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return INPUT;
		}
	}
	
	/**
	 * ������ҵ���н��׹���
	 * �ն˺���:0788��ͷ
	 * @return
	 */
	public String nanyangBankCheck(){
		try{
			if(nanyangFillName!=null){
				Long begin=System.currentTimeMillis();
				InputStreamReader is = new InputStreamReader( new FileInputStream(nanyangFillName));
				BufferedReader in = new BufferedReader(is);
				String line = in.readLine();
				line = in.readLine();       // ��ȡ��һ��
				String query="";
				int count = 0;  //�ɹ�����
				int gouduiguo = 0; //���ҹ���
				int exceptionc = 0;
				InternationalTradeinfo ti = null;
				List<InternationalTradeinfo> tiList;
				List<String> someTrade = new ArrayList<String>();
		        while (line != null) {          // ��� line Ϊ��˵��������
		        	if(line.trim().startsWith("07")){
		        		//����ÿһ�н�������	
			        	StringTokenizer st = null;
			    		st = new StringTokenizer(line , " ");
			    		String str []  = new String[st.countTokens()];
			    		for (int i = 0; i < str.length; i++) {
			    			str[i]=st.nextToken();
			    		}
			    		String posid=str[0]; //�ն˺�
			    		String tardedate=str[3]; //�ն˺�
			    		String ordernum=str[4]; //����
			    		String orderamountby=str[5]; //���ҽ��
			    		String authno=str[7]; //��Ȩ��
			    		
			    		ImportCheck ic = new ImportCheck();
			    		ic.setTradeAmount(Double.valueOf(orderamountby));
			    		
			    		ic.setAuthorizationNo(authno);
			    		ic.setTerminalNo(posid);
			    		ic.setTradeDate(DateUtil.toDate(tardedate));
			    		ic.setCardNo(ordernum);
			    		ic.setAmount(orderamountby);
			    		
			    		query = "select ti from InternationalTradeinfo ti where ti.VIPTerminalNo='"+posid.trim()+"'" +
			    				"  and ti.VIPAuthorizationNo='"+ic.getAuthorizationNo()+"'";
			    		tiList = this.commonService.list(query);
			    		
			    		
			    		// // ��������ظ�Ԫ�ص�����
			    		if(tiList.size()>0){
			    			for (int i = 0; i < tiList.size(); i++) {
			    				ti = tiList.get(i);
			    				//��֤�Ƿ��ظ�
				    			ic.setOrderNo(tiList.get(0).getOrderNo());
					    		int[] indexArray = reduplicateIndex(someTrade, ic.getOrderNo());
					    		if(indexArray.length>0){
					    			this.someTradeList.add(ic);
					    		}
					    		someTrade.add(ic.getOrderNo());
				    			//���гɹ��Ļ�
					    		if(ti.getTradeState().substring(4, 5).equals("0") && ti.getTradeState().substring(0, 1).equals("1")){
					    			if(ti.getRmbAmount()!=ic.getTradeAmount().doubleValue()){
				    					//����ͬ����Ϊ�쳣��
				    					this.exceptionTradeList.add(ic);
				    				}else{
				    					//����Ҳ�ɹ��ľ͹���
					    				if(ti.getTradeState().substring(0, 1).equals("1")){
					    					//��������
											ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 5, "1"));
											ti.setGouduiTime(new Date());
											this.commonService.update(ti);
											System.out.println(ti.getOrderNo()+"----�ѹ���");
											count++;
					    				}else{
					    					//�����Ǻ�̨�����ݹ��ҳɳɹ�״̬
					    					ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 1, "1"));
					    					ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 5, "1"));
											ti.setGouduiTime(new Date());
											this.commonService.update(ti);
					    				}
				    				}
					    		}
					    		//��ѯ������,�����ѹ���
								else if(ti.getTradeState().substring(4, 5).equals("1")){
									noCheckTradeList.add(ic);
									gouduiguo++;
								}//��̨ʧ��  ���гɹ�
								else if(ti.getTradeState().substring(0, 1).equals("0")){
									exceptionTradeList.add(ic);
									exceptionc++;
								}
			    				
							}
			    		}else{
			    			//����null�����ڵ���
							lostTradeList.add(ic);
			    		}
						
		        	}
		        	line = in.readLine();   // ��ȡ��һ��
		        }
		        Long end=System.currentTimeMillis() - begin;
				this.messageAction="�ϴ����,��ʱ:"+end+"����.���ҳɹ�"+count+"��.�Ѿ����ҹ�����δ����"+gouduiguo+"��.�쳣��"+exceptionc+"��";
		        is.close();
		        in.close();
			}
			return SUCCESS;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return INPUT;
		}
	}
	
	private File bocTCFile ;
	
	/**
	 * ����3150�ն˺Ź���
	 * @return
	 */
	public String BocTCCheck(){
		try{
			if(bocTCFile!=null){
				Long begin=System.currentTimeMillis();
				InputStreamReader is = new InputStreamReader( new FileInputStream(bocTCFile));
				BufferedReader in = new BufferedReader(is);
				String line = in.readLine();
				line = in.readLine();       // ��ȡ��һ��
				int count = 0;
				int gouduiguo = 0; //���ҹ���
				String query="";
				InternationalTradeinfo ti = null;
				List<InternationalTradeinfo> tiList;
				List<String> someTrade = new ArrayList<String>();
		        while (line != null) {          // ��� line Ϊ��˵��������
		        	if(line.trim().startsWith("31")){
		        		//����ÿһ�н�������	
			        	StringTokenizer st = null;
			    		st = new StringTokenizer(line , " ");
			    		String str []  = new String[st.countTokens()];
			    		for (int i = 0; i < str.length; i++) {
			    			str[i]=st.nextToken();
			    		}
			    		String posid=str[0]; //�ն˺�
			    		String tardedate=str[3].substring(4, 8); //��������
			    		String tardetime=str[4]; //����ʱ��
			    		String ordernum=str[2]; //����
			    		String orderamountby=str[5]; //���ҽ��
			    		String authno=str[8]; //��Ȩ��
			    		String rrn=str[12];  //rrn
			    		
			    		ImportCheck ic = new ImportCheck();
			    		ic.setTradeAmount(Double.valueOf(orderamountby.replace(",","")));
			    		ic.setAmount(orderamountby);
			    		ic.setCardNo(ordernum);
			    		ic.setAuthorizationNo(authno);
			    		ic.setTerminalNo(posid);
			    		ic.setTradeDate(DateUtil.toDate(tardedate));
			    		
			    		
			    		query = "select ti from InternationalTradeinfo ti where ti.VIPTerminalNo='"+posid.trim()+"'" +
			    				"  and ti.VIPAuthorizationNo='"+ic.getAuthorizationNo()+"' and ti.boc_rrn='"+rrn.trim()+"' " +
			    						"and ti.boc_date='"+tardedate+"' and ti.boc_time='"+tardetime+"' ";
			    		tiList = this.commonService.list(query);
			    		
			    		
			    		// // ��������ظ�Ԫ�ص�����
			    		if(tiList.size()>0){
			    			for (int i = 0; i < tiList.size(); i++) {
			    				ti = tiList.get(i);
			    				//��֤�Ƿ��ظ�
				    			ic.setOrderNo(tiList.get(0).getOrderNo());
					    		int[] indexArray = reduplicateIndex(someTrade, ic.getOrderNo());
					    		if(indexArray.length>0){
					    			this.someTradeList.add(ic);
					    		}
					    		someTrade.add(ic.getOrderNo());
					    		if(ti.getTradeState().substring(4, 5).equals("0") && ti.getTradeState().substring(0, 1).equals("1")){
					    			//���гɹ��Ļ�
				    				if(ti.getRmbAmount()!=ic.getTradeAmount().doubleValue()){
				    					//����ͬ����Ϊ�쳣��
				    					this.exceptionTradeList.add(ic);
				    				}else{
				    					//����Ҳ�ɹ��ľ͹���
					    				if(ti.getTradeState().substring(0, 1).equals("1")){
					    					//��������
											ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 5, "1"));
											ti.setGouduiTime(new Date());
											this.commonService.update(ti);
											System.out.println(ti.getOrderNo()+"----�ѹ���");
											count++;
					    				}else{
					    					//�����Ǻ�̨�����ݹ��ҳɳɹ�״̬
					    					ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 1, "1"));
					    					ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 5, "1"));
											ti.setGouduiTime(new Date());
											this.commonService.update(ti);
					    				}
				    				}
					    		}
					    		//��ѯ������,�����ѹ���
								else if(ti.getTradeState().substring(4, 5).equals("1")){
									noCheckTradeList.add(ic);
									gouduiguo++;
								}//��̨ʧ��  ���гɹ�
								else if(ti.getTradeState().substring(0, 1).equals("0")){
									exceptionTradeList.add(ic);
								}
							}
			    		}else{
			    			//����null�����ڵ���
			    			
							lostTradeList.add(ic);
			    		}
						
		        	}
		        	line = in.readLine();   // ��ȡ��һ��
		            
		        }
		        Long end=System.currentTimeMillis() - begin;
				this.messageAction="�ϴ����,��ʱ:"+end+"����.���ҳɹ�"+count+"��.�Ѿ����ҹ�����δ����"+gouduiguo+"��";
		        System.out.println(messageAction);
		        is.close();
		        in.close();
			}
			  
			return SUCCESS;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return INPUT;
		}
	}
	
	
	/**
	 * ��3D�ϴ�����ҳ��
	 * @return
	 */
	public String toF3DCheckPage(){
		return SUCCESS;
	}
	/**
	 * ��3Dͨ������
	 * @return
	 */
	public String F3DCheck(){
		try {
			InputStreamReader is = new InputStreamReader( new FileInputStream(VIPFileName));
			BufferedReader in = new BufferedReader(is);
			String line = in.readLine();
			line = in.readLine();       // ��ȡ��һ��
			int count = 0;
			String query="";
			InternationalTradeinfo ti = null;
			List<String> someTrade = new ArrayList<String>();
	        while (line != null) {          // ��� line Ϊ��˵��������
	            //����ÿһ�н�������	
	        	StringTokenizer st = null;
	    		st = new StringTokenizer(line , ",");
	    		String str []  = new String[st.countTokens()];
	    		for (int i = 0; i < 14; i++) {
	    			str[i]=st.nextToken();
	    		}
	    		ImportCheck ic = new ImportCheck();
	    		
	    		String tradetimenoby=str[1]; //���ҽ�������
	    		String ordernoby=str[3]; //������ˮ��
	    		String orderamountby=str[9]; //���ҽ��
	    		String resultby=str[11]; //����״̬
	    		query = "select ti from InternationalTradeinfo ti where ti.orderNo='"+ordernoby.trim()+"' ";
	    		ti = (InternationalTradeinfo) this.commonService.uniqueResult(query);
	    		
	    		ic.setTradeAmount(Double.valueOf(orderamountby.substring(1, orderamountby.length())));
	    		ic.setOrderNo(ordernoby);
	    		ic.setTradeDate(DateUtil.toDate(tradetimenoby));
	    		
//	    		int[] indexArray = reduplicateIndex(someTrade, ic.getOrderNo());
//	    		if(indexArray.length>0){
//	    			this.someTradeList.add(ic);
//	    		}
	    		//someTrade.add(ic.getOrderNo());
	    		// // ��������ظ�Ԫ�ص�����
	    		if(ti!=null){
	    			//���гɹ��Ļ�
	    			if(resultby.startsWith("0")){
	    				if(ti.getRmbAmount()!=ic.getTradeAmount().doubleValue()){
	    					//����ͬ����Ϊ�쳣��
	    					this.exceptionTradeList.add(ic);
	    				}else{
	    					//����Ҳ�ɹ��ľ͹���
		    				if(ti.getTradeState().substring(0, 1).equals("1")){
		    					//��������
								ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 5, "1"));
								ti.setGouduiTime(new Date());
								this.commonService.update(ti);
								count++;
		    				}else{
		    					//�����Ǻ�̨�����ݹ��ҳɳɹ�״̬
		    					ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 1, "1"));
		    					ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 5, "1"));
								ti.setGouduiTime(new Date());
								this.commonService.update(ti);
		    				}
	    				}
	    			}else{
	    				//��������
	    				ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 1, "0"));
						ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 5, "1"));
						ti.setGouduiTime(new Date());
						this.commonService.update(ti);
	    			}
	    		}else{
	    			//����null�����ڵ���
					lostTradeList.add(ic);
	    		}
				
	            line = in.readLine();   // ��ȡ��һ��
	        }
	        is.close();
	        in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	
	public static <T> int[] reduplicateIndex(List<T> list, T str) throws Exception {
		List<T> tmp = new ArrayList<T>(list);
		int[] index = new int[Collections.frequency(list, str)];
		int start = tmp.indexOf(str);
		int end = tmp.lastIndexOf(str);
		int i = 0;
		if (start > 0) {
		
		index[i] = start;
		while (start != end) {
			index[++i] = end;
			tmp = tmp.subList(0, end);
			end = tmp.lastIndexOf(str);
			Arrays.sort(index);
			return index;
		}
		}
		return index;
	}
	
	/**
	 * ��֤��������Ƿ����0.01
	 * @param amount
	 * @param vamount
	 * @return
	 */
	public static boolean validateAmount(Double amount,Double vamount){
		if((amount+0.01)==vamount.doubleValue()){
			return true;
		}else if((amount-0.01)==vamount.doubleValue()){
			return true;
		}else{
			return false;
		}
	}
	public List<ImportCheck> getLostTradeList() {
		return lostTradeList;
	}

	public void setLostTradeList(List<ImportCheck> lostTradeList) {
		this.lostTradeList = lostTradeList;
	}

	public List<ImportCheck> getSomeTradeList() {
		return someTradeList;
	}

	public void setSomeTradeList(List<ImportCheck> someTradeList) {
		this.someTradeList = someTradeList;
	}

	public List<ImportCheck> getExceptionTradeList() {
		return exceptionTradeList;
	}

	public void setExceptionTradeList(List<ImportCheck> exceptionTradeList) {
		this.exceptionTradeList = exceptionTradeList;
	}

	public List<ImportCheck> getNoCheckTradeList() {
		return noCheckTradeList;
	}

	public void setNoCheckTradeList(List<ImportCheck> noCheckTradeList) {
		this.noCheckTradeList = noCheckTradeList;
	}

	public File getVIPFileName() {
		return VIPFileName;
	}

	public void setVIPFileName(File fileName) {
		VIPFileName = fileName;
	}

	public File getNanyangFillName() {
		return nanyangFillName;
	}

	public void setNanyangFillName(File nanyangFillName) {
		this.nanyangFillName = nanyangFillName;
	}

	public File getBocTCFile() {
		return bocTCFile;
	}

	public void setBocTCFile(File bocTCFile) {
		this.bocTCFile = bocTCFile;
	}
}
