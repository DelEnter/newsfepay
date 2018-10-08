package com.ecpss.action.refund;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.action.filemanager.FileUpLoadAction;
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
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.iservice.RefundService;
import com.ecpss.tools.TableExport;
import com.ecpss.tools.TableExportFactory;
import com.ecpss.util.DateUtil;
import com.ecpss.util.DownloadUtils;
import com.ecpss.util.UploadUtils;
import com.ecpss.vo.ImportRefund;

public class BatchRefundAction_beifen extends BaseAction{

	@Autowired
	@Qualifier("refundService")
	private RefundService refundService;
	
	private List<FileManager> fileList;
	/**
	 * �����˿�
	 */
	private Long fileId;
	private String inputPath; // ָ��Ҫ�����ص��ļ�·�� 
	private InputStream inputStream;
	private String refunddownFileName;
	/**
	 * ���佻������
	 */
	private Long tradeId;
	private Object[] o;
	private String dccCurrency;
	private Double dccAmount;
	
	
	/**
	 * ���������˿��ļ�
	 * ��ʱ���ɻ����˹�
	 */
	public String batchRefundDispose(){
		System.out.println("��ʼ�����˿��ļ�");
		String batch = refundService.batchRefund();
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
		ti.setDCCTradeAmount(dccAmount);
		ti.setDCCTradeCurrency(dccCurrency);
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
				rowRule.addCellRule(new CellRuleImpl("F", "tradeAmountRMB",
						new NumberCellValueConvertor()));
				rowRule.addCellRule(new CellRuleImpl("G", "refundAmountRMB",
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
							"and ti.orderNo='"+in.getCardNo().trim()+"' ");
					//sb.append("  order by ti.tradetime desc ");
					System.out.println(sb.toString());
					List<Object[]> objList  = this.commonService.list(sb.toString());
						for(Object[] obj:objList){
							try{
								
							
							StringBuffer writeRefund = new StringBuffer();
							ti = (InternationalTradeinfo) obj[0];
							ci = (InternationalCardholdersInfo) obj[1];
							System.out.println(in.getCardNo());
							
							String aaaa = "select rm.id from InternationalRefundManager rm where rm.tradeId="+ti.getId();
							List list222 = this.commonService.list(aaaa);
							if(list222.size()>0){
								writeRefund.append("\n"+ti.getOrderNo()+"refundding\n");
							}else{
								if(ti.getVIPTerminalNo().startsWith("7777") || ti.getVIPTerminalNo().startsWith("7669")){
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
									writeRefund.append(ci.getCardNo().trim());
									//����λ  3���ո�
									writeRefund.append("   ");
									//�ڰ�λ ��Ч��(4λ)  YYMM
									writeRefund.append(ci.getExpiryDate().substring(2, 4)+ci.getExpiryDate().substring(0, 2));
									//�ھ�λ  1���ո�
									writeRefund.append(" ");
									//��ʮλ ����/�˿� ��������ҽ��Decimal place(2)    
									writeRefund.append(return12Amount(in.getRefundAmountRMB().toString()));
									//��ʮһλ DCC���۽�����ҽ��Decimal place(2)  �˿����ҽ��׽��
									if(ti.getVIPTerminalNo().startsWith("7777")){
										writeRefund.append(return12Amount(in.getTradeAmountRMB().toString()));
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
									writeRefund.append(time.format(ti.getTradeTime()));
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
									writeRefund.append(time3.format(ti.getTradeTime()));
									
									//һ�ʽ�����������Ժ���
									writeRefund.append("\n");
								}else{
									writeRefund.append("\n "+ti.getOrderNo()+"  3d refund   \n");
								}
							}
							//д�뵽�˿��ļ��иı��˿�״̬
							write.append(writeRefund.toString());
							
							
						}catch (Exception e) {
							e.printStackTrace();
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
	
}
