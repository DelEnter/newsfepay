<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    
    <title>生成保证金划款表</title>
   	<LINK href="../css/head.css" type=text/css rel=stylesheet>
  </head>
  
  <body>
  	<center>
  		<h3>生成保证金划款表</h3>
	    <TABLE borderColor=#ffffff cellSpacing=0 cellPadding=0 width="900" align=center 
				bgColor=#ffffff borderColorLight=#000000 border=1 height="10">
			<tr>
	    		<th height=25>商户号</th>
	    		<td colSpan=10>
	    			<s:property value="merchant.merno"/>
	    			&nbsp;&nbsp;<s:property value="merchantno"/>
	    		</td>
	    	</tr>
	    	<tr>
	    		<th height=25>时间范围</th>
	    		<td colSpan=10>
	    			<s:property value="o[0]"/>&nbsp;&nbsp;&nbsp;&nbsp;<s:property value="o[1]"/>
	    		</td>
	    	</tr>
	    	<tr>
	    		<th rowSpan=4>收款人</th>
	    	</tr>
	    	<tr>
	    		<th height=25>开户名</th>
	    		<td colSpan=10>
	    			&nbsp;&nbsp;<s:property value="merchant.accountname"/>
	    		</td>
	    	</tr>
	    	<tr>
	    		<th height=25>开户行</th>
	    		<td colSpan=10>
	    			&nbsp;&nbsp;<s:property value="merchant.bank"/>
				</td>   		
	    	</tr>
	    	<tr>
	    		<th height=25>帐号</th>
	    		<td colSpan=10>
	    			&nbsp;&nbsp;<s:property value="merchant.cardno"/>
	    		</td>
	    	</tr>
	    	<tr>
	    		<th bgColor=#cccccc>通道</th>
	    		<th bgColor=#cccccc>结算汇率</th>
	    		<th bgColor=#cccccc>交易笔数</th>
	    		<th bgColor=#cccccc>交易金额</th>
	    		<th bgColor=#cccccc>交易币种</th>
	    		<th bgColor=#cccccc>保证金费率</th>
	    		<th bgColor=#cccccc>部分退款保证金</th>
	    		<th bgColor=#cccccc>保证金金额</th>
	    		<th bgColor=#cccccc>结算币种</th>
	    		<th bgColor=#cccccc>应结算金额(RMB)</th>
	    	</tr>
	    	<s:iterator id="it" value="list">
			    <tr align="center">
			    	<td>
				    		<s:property value="#it.channelName"/>&nbsp;
				    	</td>
				    	<td>
				    		<s:property value="#it.balanceexchangerate"/>&nbsp;
				    	</td><td>
				    		<s:property value="#it.tradenumber"/>&nbsp;
				    	</td>
				    	<td>
				    		<s:property value="#it.trademoney"/>&nbsp;
				    	</td>
				    	<td>
				    		<s:property value="#it.moneykindname"/>&nbsp;
				    	</td>
				    	<td>
				    		<s:property value="#it.bailexchangerate"/>&nbsp;
				    	</td>
				    	<td>
				    		<s:property value="#it.partbailrefund"/>&nbsp;
				    	</td>
				    	<td>
				    		<s:property value="#it.bailmoney"/>&nbsp;
				    	</td>
				    	<td>
				    		<s:property value="#it.balancemoneykind"/>&nbsp;
				    	</td>
				    	<td>
				    		<s:property value="#it.balancemoney"/>&nbsp;
				    	</td>
				</tr>
			</s:iterator>
	    </table>
    </center>
  </body>
</html>
