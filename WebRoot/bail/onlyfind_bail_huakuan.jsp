<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   
    <title>保证金明细</title>
    	<%@ taglib prefix="pages" uri="/xs-pages"%>
        <%@ include file="../../util/calendar.jsp" %>
        <%@ include file="../util/checkAll.jsp" %>
		<LINK href="../css/head.css" type=text/css rel=stylesheet>
	<SCRIPT language=JavaScript>
		function huakuan(){
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
				alert("请选上冻结的选项！");
			}else{
				form1.action="bailHuakuan.action";
				form1.submit();
			}
		}	
	
	function exportInfo(){
		var formX = document.getElementById("form1");
		var exportX = document.getElementById("isdownload");
		exportX.value="1";
		formX.submit();
	}
	
	function chaxun11(){
		var formX = document.getElementById("form1");
		var exportX = document.getElementById("isdownload");
		exportX.value="";
		formX.submit();
	}
	</script>
  </head>
  
  <body>
    <center>
    	<h3>保证金明细</h3>
		<s:form action="onlyfindHuakuanBail" theme="simple" method="post" id="form1" name="form1">
		<s:hidden name="isdownload" id="isdownload"/>
		    <table align="center">
		    	<tr>
			    	<td>商户号</td>
			    	<td>
			    		<s:textfield name="merchant.merno"/>
			    	</td>
		    	</tr>
		    	<tr>
		    		<td>批次号</td>
			    	<td>	
			    		<s:textfield name="bailhua.batchno"/>
			    	</td>
			    	<td>
			    		<input type="button" name="chaxun" onclick="chaxun11()" value="查询" />
			    	
			    	</td>
		    	</tr>
		    </table>
	 
			<TABLE borderColor=#ffffff cellSpacing=0 cellPadding=0 width="900" align=center 
				bgColor=#ffffff borderColorLight=#000000 border=1 height="10">
				<tr>
					<th> </th>
					<th bgColor=#cccccc>商户号</th>
					<th bgColor=#cccccc>批次号</th>
					<th bgColor=#cccccc>保证金额</th>
					<th bgColor=#cccccc>结算人民币</th>
					<th bgColor=#cccccc>开户人</th>
					<th bgColor=#cccccc>制表时间</th>
					<th bgColor=#cccccc>开始交易时间</th>
					<th bgColor=#cccccc>结束交易时间</th>
					<th bgColor=#cccccc>划款日期</th>
					<th bgColor=#cccccc>划款行</th>
					<th bgColor=#cccccc>查看</th>
					<th bgColor=#cccccc>明细</th>
				</tr>
				<s:iterator id="it" value="info.result">
					<tr align="center">
					<td>
					
		   					
		   			</td>
						<td>
							<s:property value="#it[1].merno"/> &nbsp;
						</td>
						<td>
							<s:property value="#it[0].batchno"/> &nbsp;
						</td>
						<td>
							<s:property value="#it[0].bailmoney"/> &nbsp;
						</td>
						<td>
							<s:property value="#it[0].balancemoney"/> &nbsp;
						</td>
						<td>
						<s:property value="#it[1].accountname"/> &nbsp;
						</td>
						<td>
							<s:property value="#it[0].ceatetabletime"/> &nbsp;
						</td>
						<td>
							<s:property value="#it[0].tradestarttime"/> &nbsp;
						</td>
						<td>
							<s:property value="#it[0].tradeendtime"/> &nbsp;
						</td>
						<td>
						
							
								<s:property value="#it[0].huakuantime"/>
							 &nbsp;
						</td>
						<td>
							
							
								<s:property value="#it[0].huakuanbank"/>
							&nbsp;
						</td>
						<td> 
							<a href="findPreviewBail.action?bailhua.batchno=<s:property value="#it[0].batchno"/>& merchant.merno=<s:property value="#it[1].merno"/>">查看</a>
						</td>
						<td>
							<a href="findListBail.action?bailhua.batchno=<s:property value="#it[0].batchno"/>">明细</a>
						</td>
					</tr>
				</s:iterator>
				
			</TABLE>
			<table>
				<tr>
					<td>
						
					</td>
				</tr>
				<tr>
					
					<td>
						<pages:pages value="info" beanName="info"
							formName="getElementById('form1')" />
					</td>
				</tr>
			</table>
		</s:form>
	</center>
  </body>
</html>
							