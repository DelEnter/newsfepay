<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   
    <title>保证金划款明细表</title>
	<link rel="stylesheet" type="text/css" href="../css/head.css">
  </head>
  
  <body>
    <center>
    	<h3>保证金划款明细表</h3>
    	<TABLE borderColor=#ffffff cellSpacing=0 cellPadding=0 width="900" align=center 
				bgColor=#ffffff borderColorLight=#000000 border=1 height="10">
			<TR align=center>
			<TD bgColor=#cccccc>
			
			</TD>
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
			   		人民币 
			    </TD>
			    <TD bgColor=#cccccc>
			    	交易币种
			    </TD>
			    <TD bgColor=#cccccc>
			    	通道
			    </TD>
			    <TD bgColor=#cccccc>
			    	支付情况
			    </TD>
			    <TD bgColor=#cccccc>
			    	是否勾兑 
			    </TD>
			
			    <TD bgColor=#cccccc>
			    	是否退款
			    </TD>
			    <TD bgColor=#cccccc>
			    	是否冻结 
			    </TD>
			   
			    <TD bgColor=#cccccc>
			    	是否划款 
			    </TD>   
			    <TD bgColor=#cccccc>
			    	是否拒付
			    </TD>   
			    <TD bgColor=#cccccc>
			    	是否传图
			    </TD>   
			    <TD bgColor=#cccccc>
			    	是否传号
			    </TD>   
			    <TD bgColor=#cccccc>
			    	备注
			    </TD>   
		  	</TR>
		  	<s:iterator id="it" value="list" status="i">
			  <tr align="center">
			  <td>
				<s:property value="#i.index+1"/>
			</td>	
			  <td>
			  		<s:property value="#it[1].merno"/>
			  	</td>
			  		<td>
			  		<s:property value="#it[0].orderNo"/>
			  	</td>
			  		<td>
			  		<s:property value="#it[0].merchantOrderNo"/>
			  	</td>
			  		<td>
			  		<s:property value="#it[0].tradeTime"/>
			  	</td>
			  		<td>
			  		<s:property value="#it[0].tradeAmount"/>
			  	</td>
			  		<td>
			  		<s:property value="#it[0].rmbAmount"/>
			  	</td>
			  	<td>
			  		<s:property value="#it[2].moneykindname"/>
			  	</td>
			  	<td>
			  		<s:property value="#it[3].channelName"/>
			  	</td>
			  	<td>
			  		<s:property value="states.getStateName(#it[0].tradeState,1)" escape="false"/>&nbsp;
			  	</td>
			  	<td>
			  		<s:property value="states.getStateName(#it[0].tradeState,5)" escape="false"/>&nbsp;
			  	</td>
			  	<td>
			  		<s:property value="#it[0].backCount" />&nbsp;
			  	</td>
			  	<td>
			  		<s:property value="states.getStateName(#it[0].tradeState,4)" escape="false"/>&nbsp;
			  	</td>
			  	<td>
			  		<s:property value="states.getStateName(#it[0].tradeState,8)" escape="false"/>&nbsp;
			  	</td>
			  	<td>
			  		<s:property value="states.getStateName(#it[0].tradeState,3)" escape="false"/>&nbsp;
			  	</td>
			  	<td>
			  		<s:if test="#it[0].isPicture!=null">
			  			已上传
			  		</s:if>
			  		<s:else>
			  			未上传
			  		</s:else>&nbsp;
			  	</td>
			  	<td>
			  		<s:if test="#it[0].isTrackNo!=null">
			  			<s:property value="#it[0].isTrackNo"/>
			  		</s:if>
			  		<s:else>
			  			未上传
			  		</s:else>&nbsp;
			  	</td>
			  	<td>
			  		<s:property value="#it[0].remark"/>&nbsp;
			  	</td>
			  </tr>
		  	</s:iterator>
    	</table>
    </center>
  </body>
</html>
