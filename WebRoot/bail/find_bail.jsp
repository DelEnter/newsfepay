<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="../include/dialog.jsp"%>
<html>
  <head>
    
    <title>保证金划款</title>
    <%@ include file="../util/checkAll.jsp" %>
    <script language="JavaScript" src="../js/util.js"></script>
    <%@ taglib prefix="pages" uri="/xs-pages"%>
	<LINK href="../css/head.css" type=text/css rel=stylesheet>
	
	<SCRIPT language=JavaScript>
    <!-- 检验是否选上需要处理的选项 -->
	function check(){
		var workorderObjectNos = document.getElementsByName('disposeId');
		var gets = new Array();
		var k = 0;
		var result = 0;
		for(var i=0; i<workorderObjectNos.length; i++){
			if(workorderObjectNos[i].checked){
			    gets[k] = workorderObjectNos[i].value;
			    result =  gets[k];
   				k++;
			}
 		}
 		if(result==0){
			alert("请选上划款的选项！");
		}else{
			form1.action = "bailAuditing.action";
			form1.submit();
		}
	}
</SCRIPT>
  </head>
  
  <body>
    <center>
    	<h3>保证金划款</h3>
    	<s:form action="findNoHuakuanBail" name="form1" method="post" >
    	
    	<table width="500" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td>商户号:<s:select name="merchant.merno" list="merchantNoList" /><s:submit value="查询"/></td>
 	 		<td>结束日期</td>
 	 		<td>
 		<input type="text" id="end_time" name="endDate" ondblclick="cleanDate(this.id)" size="15" value="<s:property value='endDate'/>"/>
 		</td>   
  </tr>
</table>
    	<div>
    	
    	</div>
    		
    		<hr size="1" />
	    	<TABLE cellSpacing=0 cellPadding=0 width="900" align=center bgColor=#ffffff border=1>
				<tr align="center">
					<td>
	  					<input type="checkbox" onclick='chkall("form1",this)' name=chk />
	  				</td>
		    		<TD bgColor=#cccccc>
				    	商户号
				    </TD>
		    		<TD bgColor=#cccccc>
				    	流水订单号 
				    </TD>
				    <TD bgColor=#cccccc>
				    	商户订单号
				    </TD>
				     <TD bgColor=#cccccc>
				   		交易日期 
				    </TD>
				    <TD bgColor=#cccccc>
				   		交易金额 
				    </TD>
				    
				    <TD bgColor=#cccccc>
				    	支付状态
				    </TD>
				    <TD bgColor=#cccccc>
				    	是否退款
				    </TD>
				    <TD bgColor=#cccccc>
				    	是否拒付
				    </TD>   
				    <TD bgColor=#cccccc>
				    	是否冻结 
				    </TD>
				     <TD bgColor=#cccccc>
				    	是否勾兑 
				    </TD>
				    <TD bgColor=#cccccc>
				    	是否保证金划款 
				    </TD>   
				    <TD bgColor=#cccccc>
				    	是否传号
				    </TD>   
				    <TD bgColor=#cccccc>
				    	备注
				    </TD>   
			    </tr>
		    
			    <s:iterator id="it" value="traderesult">
			    	<tr align="center">
			    		<td>
							<input type="checkbox" name="disposeId" id="disposeId" value="<s:property value="#it[0].id"/>">
		   				</td>
			    		<td>
			    			<s:property value="#it[1].merno"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="#it[0].orderNo"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="#it[0].merchantOrderNo"/>&nbsp;
			    		</td>
			    		
			    		<td>
			    			<s:property value="#it[0].tradeTime"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="#it[0].tradeAmount"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="states.getStateName(#it[0].tradeState,1)" escape="false"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="#it[0].backCount" escape="false"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="states.getStateName(#it[0].tradeState,3)" escape="false"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="states.getStateName(#it[0].tradeState,4)" escape="false"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="states.getStateName(#it[0].tradeState,5)" escape="false"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:property value="states.getStateName(#it[0].tradeState,8)" escape="false"/>&nbsp;
			    		</td>
			    		<td>
			    			<s:if test="#it[0].isTrackNo==null">
			    				未上传跟踪单号
			    			</s:if>
			    			<s:else>
			    				<s:property value="#it[0].isTrackNo" />
			    			</s:else>
			    		</td>
			    		<td>
			    			<s:property value="#it[0].remark"/>&nbsp;
			    		</td>
			    	</tr>
			    </s:iterator>
			 	
	    	</table>
	    	<table>
	    		<tr>
			 		<td> 
			 			<input type="button" value="审核保证金划款表" onclick="check()">
			 		</td>
			 	</tr>
	    	</table>
<!-- 下面这段script代码必须放在form体的最后  
             loadcalendar方法的五个参数分别解释如下：
             1、日期显示文本框的ID号
             2、触发日历控件显示的控件ID号
             3、要显示的日期格式，%Y表示年，%m表示月，%d表示日
             4、是否带周显示，默认是不带
             5、是否带时间显示，默认是不带
             6、日历显示文字的语言，默认是中文 -->
        <script language="javascript" type="text/javascript">

            loadcalendar('end_time', 'end_time', '%Y-%m-%d', false, true, "cn");
            
            function cleanDate(vid){
            	document.getElementById(vid).value="";
            }
        </script>
        <!-- 上面这段script代码必须放在form体的最后 --> 	    	
    	</s:form>
   	
    </center>
  </body>
</html>
