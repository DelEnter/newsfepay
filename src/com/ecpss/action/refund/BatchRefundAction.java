package com.ecpss.action.refund;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.action.filemanager.FileUpLoadAction;
import com.ecpss.action.pay.tc.XMLGetMessage;
import com.ecpss.excel.builder.RowResult;
import com.ecpss.excel.builder.jexcel.JExcelRowObjectBuilder;
import com.ecpss.excel.cell.jexcel.NumberCellValueConvertor;
import com.ecpss.excel.rule.impl.CellRuleImpl;
import com.ecpss.excel.rule.impl.RowRuleImpl;
import com.ecpss.model.FileManager;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.refund.InternationalRefundHistory;
import com.ecpss.model.refund.InternationalRefundManager;
import com.ecpss.service.iservice.RefundService;
import com.ecpss.service.iservice.SystemManagerService;
import com.ecpss.tools.TableExport;
import com.ecpss.tools.TableExportFactory;
import com.ecpss.util.AES;
import com.ecpss.util.DateUtil;
import com.ecpss.util.DownloadUtils;
import com.ecpss.util.GetBatchNo;
import com.ecpss.util.UploadUtils;
import com.ecpss.vo.ImportRefund;
import com.ecpss.web.PageInfo;

public class BatchRefundAction extends BaseAction{

	@Autowired
	@Qualifier("refundService")
	private RefundService refundService;
	@Autowired
	@Qualifier("systemManagerService")
	private SystemManagerService systemManagerService;
	
	private List<FileManager> fileList;
	/**
	 * �����˿�
	 */
	private Long fileId;
	private String inputPath; // ָ��Ҫ�����ص��ļ�·�� 
	private InputStream inputStream;
	private String refunddownFileName;
	
	private PageInfo info = new PageInfo();
	private Long refundid;
	/**
	 * ���佻������
	 */
	private Long tradeId;
	private Object[] o;
	private String dccCurrency;
	private Double dccAmount;
	private String posNUM;
	private String beatchNUM;
	private String voidNUM;
	private String refundType;
	/**
	 * ���������˿��ļ�
	 * ��ʱ���ɻ����˹�
	 */
	public String batchRefundDispose(){
		System.out.println("��ʼ�����˿��ļ�");
		String batch = null ;
		if(refundType.equals("VIP")){
			batch = refundService.batchRefund();
		}
		if(refundType.equals("MIGS")){
			batch = refundService.batchRefundByVC();
		}
		if(StringUtils.isNotBlank(batch)){
			this.messageAction = "���ɳɹ�,�����ļ��Ƿ���ȷ";
		}else{
			this.messageAction="����ʧ��,���������Ƿ���ȷ";
		}
		String hql = "select f from FileManager f where f.usetype='0' order by f.filedate desc ";
		fileList = this.commonService.list(hql);
		return SUCCESS;
		
	}
	

	
	
	
	/**
	 * ���������˿��ļ�
	 * @return
	 */
	public String todownloadBatchRefundByTxt(){
		String hql = "select f from FileManager f where f.usetype='0' order by f.filedate desc ";
		fileList = this.commonService.list(hql);
		return SUCCESS;
	}
	
	/**
	 * �����˿��ļ�
	 * @return
	 * @throws IOException 
	 */
	public String downbatchrefund() throws IOException{
		FileManager c = (FileManager) this.commonService.load(FileManager.class, fileId);
		InputStream stream = FileUpLoadAction.class.getClassLoader().getResourceAsStream("/ecpss.properties");
		Properties p = new Properties();
		p.load(stream);
		inputPath=p.getProperty("refund_dir")+"/"+c.getFileroute();
		String orgName = FilenameUtils.getBaseName(c.getFilename()+"."+FilenameUtils.getExtension(c.getFileroute()));
		refunddownFileName = new String(orgName.getBytes("GBK"),"iso-8859-1");
		if(c.getDownloadcount()==null){
			c.setDownloadcount(0L);
		}
		c.setDownloadcount(c.getDownloadcount()+1L);
		c.setDownloadDate(new Date());
		this.commonService.update(c);
		return SUCCESS;
	}
	public InputStream getInputStream() throws Exception {
		inputStream = new FileInputStream(inputPath);
		// ͨ�� ServletContext��Ҳ����application ����ȡ���� 
		return inputStream ; 
	}

	
	/**
	 * ���佻������
	 * @return
	 */
	public String toinputFulInfos(){
		String hql = "select card.cardNo, card.expiryDate, card.cvv2, t.orderNo, t.rmbAmount, chann.channelName, t.id,t.VIPTerminalNo," +
				"t.VIPBatchNo,t.VIPAuthorizationNo" +
		" FROM InternationalTradeinfo t, InternationalCardholdersInfo card, InternationalMerchantChannels merchan, " +
		"InternationalChannels chann" +
		" WHERE t.id=card.tradeId " +
		"AND t.tradeChannel = merchan.id " +
		"and merchan.channelId=chann.id " +
		" AND t.id="+tradeId+"";
		o = (Object[])commonService.uniqueResult(hql);
		return SUCCESS;
	}
	
	/**
	 * �ύ������Ϣ
	 * @return
	 */
	public String tijiaoinputfull(){
		InternationalTradeinfo ti = (InternationalTradeinfo) this.commonService.load(InternationalTradeinfo.class, tradeId);
		if(this.beatchNUM!=null){
		ti.setVIPBatchNo(this.beatchNUM);
		}
		if(this.posNUM!=null){
		ti.setVIPTerminalNo(this.posNUM);
		}
		if(this.voidNUM!=null){
		ti.setVIPAuthorizationNo(this.voidNUM);
		}
		this.commonService.update(ti);
		this.messageAction="�ύ�ɹ�";
		return SUCCESS;
	}
	
	
	

	private File fileName;   //�ϴ����ļ�
	
	/**
	 * �ϴ�excel�ļ�����txt�ļ�
	 * @return
	 */
	public String uploadExcel(){
		try{
			if(fileName!=null){
				Workbook workBook=Workbook.getWorkbook(fileName);
				RowRuleImpl rowRule = new RowRuleImpl();
				rowRule.addCellRule(new CellRuleImpl("A","cardNo"));
				rowRule.addCellRule(new CellRuleImpl("B", "tradeAmountRMB",
						new NumberCellValueConvertor()));
				rowRule.addCellRule(new CellRuleImpl("C", "refundAmountRMB",
						new NumberCellValueConvertor()));
				rowRule.addCellRule(new CellRuleImpl("E","terminalNo"));
				rowRule.addCellRule(new CellRuleImpl("F","authNO"));
				JExcelRowObjectBuilder reveBuilder = new JExcelRowObjectBuilder();
				reveBuilder.setSheet(workBook.getSheet(0));
				reveBuilder.setTargetClass(ImportRefund.class);
				reveBuilder.setRule(1, workBook.getSheet(0).getRows(), rowRule);
				
				RowResult<ImportRefund>[] cons = reveBuilder.parseExcel();
				ImportRefund in;
				InternationalTradeinfo ti;
				InternationalCardholdersInfo ci;
				String merchantidby = null;
				Boolean flag=true;
				String merchantId9 = null;
				StringBuffer write = new StringBuffer();
				for (RowResult<ImportRefund> rowResult : cons) {
					in = rowResult.getRowObject();
					StringBuffer sb = new StringBuffer();
					sb.append("select ti,ci from InternationalTradeinfo ti,InternationalCardholdersInfo ci  " +
							"WHERE ci.tradeId=ti.id " +
							"and substr(ti.tradeState,1,1)=1 " +
							"and ci.cardNo='"+AES.setCarNo(in.getCardNo().trim())+"' " +
									" and ti.rmbAmount="+in.getTradeAmountRMB() +
									" and ti.VIPAuthorizationNo='"+in.getAuthNO()+"' ");
					//sb.append("  order by ti.tradetime desc ");
					System.out.println(sb.toString());
					List<Object[]> objList  = this.commonService.list(sb.toString());
						for(Object[] obj:objList){
							try{
								
							
							StringBuffer writeRefund = new StringBuffer();
							ti = (InternationalTradeinfo) obj[0];
							ci = (InternationalCardholdersInfo) obj[1];
							System.out.println(in.getTerminalNo());
							String merchantId = (String) this.commonService.list("select tm.merchantNo from InternationalTerminalManager tm where tm.terminalNo='"+ti.getVIPTerminalNo()+"' ").get(0);
							if(flag){
								merchantId9 = merchantId.substring(0, 9);
								flag = false;
							}
							if(StringUtils.isNotBlank(merchantidby)){
								if(!merchantidby.equals(merchantId)){
									//***********************ͷ��Ϣ************************************
									//��һλ ��1��ͷ (1λ)
									writeRefund.append("1");
									//�ڶ�λ ����(YYYYMMDD)(8λ)
									SimpleDateFormat timeh=new SimpleDateFormat("yyyyMMdd"); 
									writeRefund.append(timeh.format(new Date()));
									//����λ M =Merchan    B =Bank
									writeRefund.append("M");
									//����λ ���Unit Type=B ��д�������ţ�������дSpace(7)
									writeRefund.append("       ");
									//����λ �̻���
									writeRefund.append(merchantId);
									writeRefund.append("\n");
								}
							}else{
								//***********************ͷ��Ϣ************************************
								//��һλ ��1��ͷ (1λ)
								writeRefund.append("1");
								//�ڶ�λ ����(YYYYMMDD)(8λ)
								SimpleDateFormat timeh=new SimpleDateFormat("yyyyMMdd"); 
								writeRefund.append(timeh.format(new Date()));
								//����λ M =Merchan    B =Bank
								writeRefund.append("M");
								//����λ ���Unit Type=B ��д�������ţ�������дSpace(7)
								writeRefund.append("       ");
								//����λ �̻���
								writeRefund.append(merchantId);
								writeRefund.append("\n");
								merchantidby = merchantId;
								
							}
							
							
							//****************************************�˿����Ϣ********************************
							//��һλ��2��ͷ(1λ)
							writeRefund.append("2");
							//�ڶ�λ�Ƿ�DCC,����EDC(3λ)
							if(ti.getVIPTerminalNo().startsWith("7777")){
								writeRefund.append("DCC");
							}else{
								writeRefund.append("EDC");
							}
							//����λ�ն˺Ŷ�Ӧ���̻���(15λ)
							writeRefund.append(merchantId);
							//����λ�ն˺�(8λ)
							writeRefund.append(ti.getVIPTerminalNo().trim());
							//����λ   1���ո�
							writeRefund.append(" ");
							//����λ  ����(16λ)
							writeRefund.append(AES.getCarNo(ci.getCardNo().trim()));
							//����λ  3���ո�
							writeRefund.append("   ");
							//�ڰ�λ ��Ч��(4λ)  YYMM
							writeRefund.append(ci.getExpiryDate().substring(2, 4)+ci.getExpiryDate().substring(0, 2));
							//�ھ�λ  1���ո�
							writeRefund.append(" ");
							//��ʮλ ����/�˿� ��������ҽ��Decimal place(2)    
							writeRefund.append(return12Amount(ti.getRmbAmount().toString()));
							//��ʮһλ DCC���۽�����ҽ��Decimal place(2)  �˿����ҽ��׽��
							if(ti.getVIPTerminalNo().startsWith("7777")){
								writeRefund.append(return12Amount(ti.getTradeAmount().toString()));
							}else{
								writeRefund.append("000000000000");
							}
							//��ʮ��λ DCC������һ��Ҵ���  (����д)  (3λ)
							writeRefund.append("   ");
							//��ʮ��λ 1���ո�
							writeRefund.append(" ");
							//��ʮ��λ ԭ��������ҽ��Decimal place(2)  (12λ)
							writeRefund.append(return12Amount(ti.getRmbAmount().toString()));
							//��ʮ��λ ��Ȩ���� (6λ)
							writeRefund.append(ti.getVIPAuthorizationNo());
							//��ʮ��λ ԭ��������YYMMDD  (6λ)
							SimpleDateFormat time=new SimpleDateFormat("yyMMdd"); 
							if(ti.getVIPDisposeDate()!=null){
								writeRefund.append(time.format(ti.getVIPDisposeDate()));
							}else{
								writeRefund.append(time.format(ti.getTradeTime()));
							}
							//��ʮ��λ ԭ����Ʊ�ݺ� (6λ)
							writeRefund.append(ti.getVIPBatchNo());
							//��ʮ��λ ��������  (7λ)
							writeRefund.append("       ");
							//��ʮ��λ  ��C��������Sales�� / ��D�����˿�Refund��
							writeRefund.append("D ");
							//�ڶ�ʮλ   1���ո�  (1λ)
							writeRefund.append(" ");
							//�ڶ���һλ  ���д����� (2λ)Space(2)  ����˾������û�Ӧ��
							//00���ɹ�����
							///01��	�˿������ԭʼ���׽��
							//02��	�޷�ƥ��ԭʼ����
							//03��	ƥ����ԭʼ����
							//04��	�˿�����ڿ��˽��
							writeRefund.append("  ");
							//�ڶ�ʮ��λ   1���ո�  (1λ)
							writeRefund.append(" ");
							//�ڶ�ʮ��λ   ����Ԥ���ֶ� (20λ)  ECPSSϵͳ��ˮ���� 16887201041520162623425
							writeRefund.append(getOrderNoByRefund(ti.getOrderNo()));
							//�ڶ�ʮ��λ   1���ո�  (1λ)
							writeRefund.append(" ");
							//�ڶ�ʮ��λ   �˿��������ʱ��    YYMMDDHHMMSS
							SimpleDateFormat time3=new SimpleDateFormat("yyMMddHHmmss"); 
							if(ti.getVIPDisposeDate()!=null){
								writeRefund.append(time3.format(ti.getVIPDisposeDate()));
							}else{
								writeRefund.append(time3.format(ti.getTradeTime()));
							}
							
							//һ�ʽ�����������Ժ���
							writeRefund.append("\n");
							
							//д�뵽�˿��ļ��иı��˿�״̬
							write.append(writeRefund.toString());
							
						}catch (Exception e) {
							continue;
						}
					}
				}
				//��β��9���   
				write.append("9000000            000000");
				
				//����txt�ļ�
				try{   
					SimpleDateFormat time1=new SimpleDateFormat("yyyyMMddHHmmss");
					//�ļ���
					String imageFileName = "REFUND_"+merchantId9+"_"+time1.format(new Date())+"(excel���ɵ�).txt";
					InputStream stream = FileUpLoadAction.class.getClassLoader().getResourceAsStream("/ecpss.properties");
					Properties p = new Properties();
					p.load(stream);
					File refund=new File(UploadUtils.getRefundUploadDir());
					if(!refund.exists()){
						refund.mkdir();				
					}
					File da = new File(refund+"/"+DateUtil.getDate());
					if(!da.exists()){
						da.mkdir();
					}
					File imageFile = new File(UploadUtils.getRefundUploadDir()+"/"+ UploadUtils.getUploadFileOpPath(imageFileName));
					FileOutputStream   fos=new   FileOutputStream(imageFile);
					DataOutputStream   out=new   DataOutputStream(fos);   
					out.writeBytes(write.toString()); 
					
					//�����ļ�   ����ʹ��
					FileManager filemanager = new FileManager();
					filemanager.setFilename(imageFileName);
					filemanager.setFileroute(UploadUtils.getUploadFileOpPath(imageFileName));
					filemanager.setFilesize(imageFile.length());
					filemanager.setFiledate(new Date());
					filemanager.setUsetype("0");
					filemanager.setDownloadcount(0L);
					this.commonService.save(filemanager);
					out.close();
				}catch(Exception e) {
					System.out.println("ʧ��");
					e.printStackTrace();
				}
			}
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			return SUCCESS;
		}
				
	}
	
	/**
	 * ������ˮ���Ž�ȡ��20λ
	 * @param Orderno
	 * @return
	 */
	public String getOrderNoByRefund(String orderno){
		String orderNoByRefund = "                    ";
		if(orderno.length()>20){
			orderNoByRefund = orderno.substring(orderno.length()-20,orderno.length());
		}else{
			for (int i = 0; i < 20-orderno.length(); i++) {
				orderNoByRefund+="0";
			}
			orderNoByRefund = orderNoByRefund.trim() + orderno;
		}
		return orderNoByRefund;
	}
	
	
	public String return12Amount(String refundRMBMoney){
		String refundRMB = "000000000000";
		String zero_12 = "000000000000";
		DecimalFormat decimalFormat = new DecimalFormat("##############0.00");
		if(StringUtils.isNotBlank(refundRMBMoney) && refundRMBMoney.replace(".", "").matches("\\d+")){		
				String refundRMBStr = Double.valueOf((decimalFormat.format(Double.valueOf(refundRMBMoney))))*100+"";
				String refundRMB_0 = zero_12 + refundRMBStr.substring(0, refundRMBStr.indexOf("."));
				refundRMB = refundRMB_0.substring(refundRMB_0.length()-12, refundRMB_0.length());
		}
		return refundRMB;
	}
	
	
	private File fileNameBytxt;

	/**
	 * ����txt�ļ�����һ���˿������ļ�
	 * @return
	 * @throws Exception
	 */
	public String createExcelApplyRefund() throws Exception{
		//��ȡ.txt�˿��ļ�
		InputStreamReader is = new InputStreamReader( new FileInputStream(fileNameBytxt));
		BufferedReader in = new BufferedReader(is);
		String line = in.readLine();
		line = in.readLine();       // ��ȡ��һ��
		List<InternationalRefundHistory> rhList = new ArrayList<InternationalRefundHistory>();
		while (line != null) {          // ��� line Ϊ��˵��������
            if(line.length()>40){
            	//line.substring(28, 44)  //����
            	//line.substring(52, 64)  //���׽��
            	//line.substring(80, 92)  //�˿���
            	//line.substring(98, 104)  //��������
            	//line.substring(19, 27) //�ն˺�
            	//line.substring(92, 98)  //��Ȩ��
            	//line.substring(4,19)
				InternationalRefundHistory r = new InternationalRefundHistory();
				r.setCardNo(line.substring(28, 44));
				r.setAuthorizationNo(line.substring(92, 98));
				r.setRefundAmount(Double.valueOf(line.substring(80, 92).trim())/100);
				r.setTerminalNo(line.substring(19, 27));
				r.setTradeTime(line.substring(98, 104));
				r.setTradeAmount(Double.valueOf(line.substring(52, 64).trim())/100);
				r.setCheckInman(line.substring(4,19));
				rhList.add(r);
            }
            line = in.readLine();   // ��ȡ��һ��
        }
        is.close();
        in.close();
        
        
        TableExport export = TableExportFactory.createExcelTableExport();
		export.addTitle(new String[]{"�̻���","����","���׽��","�˿���","��������",	"�ն˺�","��Ȩ����"});
		export.setTableName("card");
		for (InternationalRefundHistory rh : rhList) {
			String date = rh.getTradeTime().substring(0,2)+"-"+rh.getTradeTime().substring(2,4)+"-"+rh.getTradeTime().substring(4,6);
			export.addRow(new Object[]{rh.getCheckInman(),rh.getCardNo(),
					rh.getTradeAmount(),rh.getRefundAmount(),
					DateUtil.formatDate(DateUtil.toDateTime("20"+date+" 00:00:00")),
					rh.getTerminalNo(),rh.getAuthorizationNo()});	
		}
		OutputStream os = DownloadUtils.getResponseOutput("�����˿�.xls");
		export.export(os);
		DownloadUtils.closeResponseOutput();
		return SUCCESS;
	}
	
	
	/**
	 * �ϴ�����,��Ȩ�Ż�ȡ������Ϣ
	 * @return
	 */
	public void tradeinfobycardno(){
		Workbook workBook;
		try {
			workBook = Workbook.getWorkbook(fileName);
			RowRuleImpl rowRule = new RowRuleImpl();
			rowRule.addCellRule(new CellRuleImpl("A","cardNo"));
			rowRule.addCellRule(new CellRuleImpl("B", "authNO"));
			JExcelRowObjectBuilder reveBuilder = new JExcelRowObjectBuilder();
			reveBuilder.setSheet(workBook.getSheet(0));
			reveBuilder.setTargetClass(ImportRefund.class);
			reveBuilder.setRule(0, workBook.getSheet(0).getRows(), rowRule);
			
			RowResult<ImportRefund>[] cons = reveBuilder.parseExcel();
			ImportRefund in;
			InternationalTradeinfo ti;
			InternationalCardholdersInfo ci;
			
			List<ImportRefund> imList = new ArrayList<ImportRefund>();
			for (RowResult<ImportRefund> rowResult : cons) {
				in = rowResult.getRowObject();
				StringBuffer sb = new StringBuffer();
				sb.append(" select ti,ci from InternationalTradeinfo ti,InternationalCardholdersInfo ci  " +
						" WHERE ci.tradeId=ti.id " +
						" and substr(ti.tradeState,1,1)=1 " +
						" and ci.cardNo='"+AES.setCarNo(in.getCardNo().trim())+"' " +
						" and ti.VIPAuthorizationNo='"+in.getAuthNO()+"' ");
				//sb.append("  order by ti.tradetime desc ");
				List<Object[]> objList = this.commonService.list(sb.toString());
				if(objList.size()>0){
					for(Object[] obj:objList){
						try{
							ti = (InternationalTradeinfo) obj[0];
							ci = (InternationalCardholdersInfo) obj[1];
							ImportRefund ir = new ImportRefund();
							ir.setAuthNO(ti.getVIPAuthorizationNo());
							ir.setCardNo(ci.getCardNo());
							ir.setOrderNo(ti.getOrderNo());
							ir.setRefundAmountRMB(ti.getBackCount());
							ir.setTrackingNo(ti.getIsTrackNo());
							ir.setTradeAmountRMB(ti.getRmbAmount());
							ir.setTradeState(ti.getTradeState());
							ir.setTradeDate(ti.getTradeTime());
							imList.add(ir);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if(imList.size()>0){
				TableExport export = TableExportFactory.createExcelTableExport();
				export.addTitle(new String[] { "����", "��Ȩ��", "��ˮ��","��������","RMB���", "�Ƿ��˿�", "�˿���","�Ƿ�ܸ�",
						"���ٵ���"});
				export.setTableName("trade");
				for (ImportRefund irf : imList) {
					export
							.addRow(new Object[] { irf.getCardNo(), irf.getAuthNO(),
									irf.getOrderNo(),irf.getTradeDate(),irf.getTradeAmountRMB(),
									getStates().getStateName(irf.getTradeState(), 2),irf.getRefundAmountRMB(),getStates().getStateName(irf.getTradeState(), 3),
									irf.getTrackingNo()});
				}
				
				OutputStream os = DownloadUtils.getResponseOutput("cbtrade.xls");
				export.export(os);
				DownloadUtils.closeResponseOutput();
			}
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//return SUCCESS;
	}
	
	/**
	 * boc���˿��б�
	 * @return
	 */
	public String bocrefundlist(){
//		StringBuffer sb = new StringBuffer();
//		sb.append("select rm,ti,c " +
//				"from InternationalRefundManager rm," +
//				"InternationalTradeinfo ti," +
//				"InternationalCardholdersInfo c " +
//				"where rm.tradeId=ti.id " +
//				"and c.tradeId=ti.id " +
//				" and " +
//				"and ti.VIPTerminalNo like '31%'");  //���ύ�ſ������
//		
//		sb.append(" order by rm.refundState ,ti.tradeTime ");
//		info = this.commonService.listQueryResultByHql(sb.toString(), info);
//		
//		
		StringBuffer sb = new StringBuffer();
		sb.append("select rm,ti,c " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti," +
				"InternationalMerchant m," +
				"InternationalCardholdersInfo c " +
				"where rm.tradeId=ti.id " +
				"and ti.merchantId=m.id " +
				"and c.tradeId=ti.id " +
				"and rm.refundState in (2,3) and ti.VIPTerminalNo like '31%' ");  //���ύ�ſ������
		
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and ti.orderNo='"+orderNo.trim()+"' ");
		}
		sb.append(" order by ti.VIPTerminalNo desc,ti.tradeTime ");
		info = this.commonService.listQueryResultByHql(sb.toString(), info);
		
		return SUCCESS;
	}
	/**
	 * tcͨ���˿�
	 * @return
	 */
	public String bocrefund(){
		StringBuffer sb = new StringBuffer();
		sb.append("select rm,ti,c " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti," +
				"InternationalCardholdersInfo c " +
				"where rm.tradeId=ti.id " +
				"and c.tradeId=ti.id " +
				"and rm.id="+refundid);  //���ύ�ſ������
		
		List list = this.commonService.list(sb.toString());
		if(list.size()>0){
			Object[] o = (Object[]) list.get(0);
			InternationalTradeinfo tradeinfo = (InternationalTradeinfo) o[1];
			InternationalRefundManager rm = (InternationalRefundManager) o[0];
			InternationalCardholdersInfo cardinfo = (InternationalCardholdersInfo) o[2];
			if(tradeinfo.getTradeState().substring(2,3).equals("1")){
				//���׾ܸ�ֱ��ɾ���˿�
				this.commonService.delete(rm);
				this.messageAction="�����Ѿܸ�,ȡ���˿�";
			}else{
				XMLGetMessage xgm = new XMLGetMessage();
				String orderno = String.format("%06d", Math.round(Math.random() * 999999));
				xgm =systemManagerService.BocRefundTrade(tradeinfo, cardinfo, rm.getRefundRMBAmount(),orderno);
				if(xgm!=null){
					if(StringUtils.isNotBlank(xgm.getRespcode())){
						if(xgm.getRespcode().equals("00")){
							//�˿��ύ�ɹ�
							GetBatchNo g = new GetBatchNo();
							String batch = g.getBatchNo();
							Long [] refundIds=new Long[1];
							refundIds[0] = rm.getId();
							this.refundService.createRefund(refundIds,batch);
							this.messageAction = "�˿��ύ�����ɹ�";
						}else{
							this.messageAction = "���д������."+xgm.getRespcode();
						}
					}else{
						this.messageAction="�ύ���д���,�������Ӳ���";
					}
					
				}else{
					this.messageAction = "���д�����ִ���."+xgm.getRespcode();
				}
			}
		}
		return this.OPERATE_SUCCESS;
	}
	
	private String orderNo;
	private Long orderId;
	
	/**
	 * TCͨ��ȡ�������б�
	 * @return
	 */
	public String tcCancelTradeQuery(){
		StringBuffer sf = new	StringBuffer();
		sf.append("select ti,ci from InternationalTradeinfo ti,InternationalCardholdersInfo ci " +
				"where ci.tradeId=ti.id " +
				"and ti.VIPTerminalNo like '31%' " +
				"and substr(ti.tradeState,1,1)=1 " +
				"and to_char(ti.tradeTime,'yyyy-MM-dd')=to_char(sysdate,'yyyy-MM-dd')");
		if(StringUtils.isNotBlank(orderNo)){
			sf.append(" and ti.orderNo='"+orderNo.trim()+"' ");
		}
		sf.append(" order by ti.tradeTime");
				
		info = this.commonService.listQueryResultByHql(sf.toString(), info);
		return SUCCESS;
	}
	/**
	 * TCͨ��ȡ������
	 * @return
	 */
	public String tcCancelTrade(){
		String sql ="select ti,ci from InternationalTradeinfo ti,InternationalCardholdersInfo ci " +
				"where ci.tradeId=ti.id " +
				"and ti.id="+orderId;
		List list = this.commonService.list(sql);
		if(list.size()>0){
			Object[] o = (Object[]) list.get(0);
			InternationalTradeinfo ti = (InternationalTradeinfo) o[0];
			InternationalCardholdersInfo ci = (InternationalCardholdersInfo) o[1];
			XMLGetMessage xgm = new XMLGetMessage();
			xgm = this.systemManagerService.BocCancelTrade(ti, ci);
			if(xgm!=null){
				if(StringUtils.isNotBlank(xgm.getRespcode())){
					if(xgm.getRespcode().equals("00")){
						String message="cancel";
						this.commonService
						.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
								+ message
								+ "' where t.id='"
								+ ti.getId() + "'");
						this.messageAction = "�����ɹ�";
					}else{
						this.messageAction = "���д������."+xgm.getRespcode();
					}
				}else{
					this.messageAction="�ύ���д���,�������Ӳ���";
				}
			}else{
				this.messageAction="�ύ���д���,�������Ӳ���";
			}
		}
		return SUCCESS;
	}
	
	public List<FileManager> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileManager> fileList) {
		this.fileList = fileList;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getRefunddownFileName() {
		return refunddownFileName;
	}

	public void setRefunddownFileName(String refunddownFileName) {
		this.refunddownFileName = refunddownFileName;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public Object[] getO() {
		return o;
	}

	public void setO(Object[] o) {
		this.o = o;
	}

	public String getDccCurrency() {
		return dccCurrency;
	}

	public void setDccCurrency(String dccCurrency) {
		this.dccCurrency = dccCurrency;
	}

	public Double getDccAmount() {
		return dccAmount;
	}

	public void setDccAmount(Double dccAmount) {
		this.dccAmount = dccAmount;
	}

	public File getFileName() {
		return fileName;
	}

	public void setFileName(File fileName) {
		this.fileName = fileName;
	}

	public File getFileNameBytxt() {
		return fileNameBytxt;
	}

	public void setFileNameBytxt(File fileNameBytxt) {
		this.fileNameBytxt = fileNameBytxt;
	}

	public String getPosNUM() {
		return posNUM;
	}

	public void setPosNUM(String posNUM) {
		this.posNUM = posNUM;
	}

	public String getBeatchNUM() {
		return beatchNUM;
	}

	public void setBeatchNUM(String beatchNUM) {
		this.beatchNUM = beatchNUM;
	}

	public String getVoidNUM() {
		return voidNUM;
	}

	public void setVoidNUM(String voidNUM) {
		this.voidNUM = voidNUM;
	}





	public String getRefundType() {
		return refundType;
	}





	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}





	public PageInfo getInfo() {
		return info;
	}





	public void setInfo(PageInfo info) {
		this.info = info;
	}





	public Long getRefundid() {
		return refundid;
	}





	public void setRefundid(Long refundid) {
		this.refundid = refundid;
	}





	public String getOrderNo() {
		return orderNo;
	}





	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}





	public Long getOrderId() {
		return orderId;
	}





	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}


	
}
