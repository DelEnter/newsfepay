<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="pages" uri="/xs-pages" %>
 <%@ include file="../include/dialog.jsp"%>
<script language="JavaScript" src="../js/util.js"></script>
<style>
body,table,tr,td{ font-size:12px;}
</style>
<head>
	<title>Apply Refund</title>
</head>
<script language="JavaScript" type="text/JavaScript">
	
	function checkSelect(){
		var select = document.formu.checkAll;
		if(select.checked==true){
			selectAll();
		}else{
			noselectAll();
		}
	}
	function selectAll() { 
		var o=document.formu.refundIds;
		//单条记录
		if(o.value!=null){
			o.checked=true;
		}
		//多条记录
	    for(var i=0;i<o.length;i++){
			if(o[i].name == "refundIds") {
				o[i].checked=true;
		    }
		}
	}
	function noselectAll() {
	    var o=document.formu.refundIds;
	    if(o.value!=null){
			o.checked=false;
		}
		 for(var i=0;i<o.length;i++){
				if(o[i].name == "refundIds")
			    {
   					o[i].checked=false;
			    }
			 }
	}
	
	function submitAuditing(){
	   var flag=0;
	   var chck=document.getElementsByName("refundIds");
	   for(i=0;i<chck.length;i++){
	       if(chck[i].checked==true){
	           flag++;
	       }
	   }
	   if(flag>0){
	   		document.getElementById("formu").action="auditingRefund";
			document.getElementById("formu").submit();
       }else{
           alert("请选择一个!");
       }
	}
</script>
<div align="center">
	<h3>PRE退款记录</h3>
</div>
<s:form name="formu" id="formu" action="toRefundAud" method="post" theme="simple">
<table align="center">
<tr class=TR_Title>
		 		<td>商户号</td>
		 		<td>
		 			<input type="input" name="merchantNo" value="<s:property value='merchantNo'/>"/>
		 		</td>
		 		<td>终端号</td>
		 		<td>
		 			<s:select name="terminalNo" list="terminalNoList"  headerKey="" headerValue="----" />
		 		</td>
	 		</tr>
	 		 <tr class=TR_Title>
	 		 	<td>交易流水订单号</td>
		 		<td>
		 			<input type="input" name="orderNo" value="<s:property value='orderNo'/>"/>
		 		</td>
		 		<td>卡号</td>
		 		<td>
		 			<input type="input" name="cardNo" value="<s:property value="cardNo"/>"/>
	 			</td>
		 		<td></td>
		 		<td>
	 			</td>
	 		</tr>
	 		<tr class=font-align>
				<td colSpan="8" align="center"><br>
					<input type="submit" value="查询" >
				</td>
				
			</tr>
	</table>
<TABLE cellSpacing="0" cellPadding="0" align="center" border=1 width="100%">
	<tr>
		<td><input type="checkbox" name="checkAll" onclick="checkSelect();" /></td>
		<td>序列</td>
		<td>流水号</td>
		<td>商户订单号</td>
		<td>卡号</td>
		<td>授权号</td>
		<td>交易日期</td>
		<td>处理日期</td>
		<td>交易金额</td>
		<td>退款金额(外币)</td>
		<td>退款金额(RMB)</td>
		<td>交易状态</td>
		<td>退款状态</td>
		<td>终端号</td>
	</tr>
	<s:iterator id="p" value="info.result" status="s">
	<tr onMouseOver="this.bgColor='#BBBBFF'; "  onMouseOut="this.bgColor='#FFFFFF';">
		<td><input type="checkbox" name="refundIds" value="<s:property value="#p[0].id" />" /></td>
		<td><s:property value="#s.index+1" /></td>
		<td><s:property value="#p[1].orderNo" /></td>
		<td><s:property value="#p[1].merchantOrderNo" /></td>
		<td><s:property value="#p[2].cardNo" /></td>
		<td><s:property value="#p[1].VIPAuthorizationNo" /></td>
		<td><s:property value="#p[1].tradeTime" /></td>
		<td><s:property value="#p[0].applyDate" /></td>
		<td><s:property value="#p[1].tradeAmount" /></td>
		<td><s:property value="#p[0].refundAmount" /></td>
		<td><font color="red"><s:property value="#p[3].refundAmount" /></font></td>
		<td><s:property value="states.getStateName(#p[1].tradeState,1)" escape="false" /></td>
		<td align="center"><s:property value="#p[3].operatorStatus" /></td>
		<td align="center"><s:property value="#p[1].VIPTerminalNo" /></td>
		
	</tr>
	</s:iterator>
</table>
<div align="center">
<pages:pages value="info" beanName="info" formName="forms(0)" />
</div>
<p align="center">
	
</p>
</s:form>











