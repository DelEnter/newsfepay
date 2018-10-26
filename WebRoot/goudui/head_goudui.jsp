<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<%@ include file="../util/checkAll.jsp" %>
  	<LINK href="../css/head.css" type=text/css rel=stylesheet>
  	<%@ taglib prefix="pages" uri="/xs-pages"%>
  	 <%@ include file="../include/dialog.jsp"%>
    <%@ include file="../../util/pageUtil.jsp"%>
 
    <title>手工勾兑</title>
    
	<link rel="../css/head.css" type="text/css" href="css/bail.css">
	<SCRIPT language=JavaScript>
	function buchongxinxi(tradeid){
		window.showModalDialog ("../PaySystem/toinputFulInfo.action?tradeId="+tradeid, window,'dialogHeight:630px;dialogWidth:593px;toolbar:yes;menubar:yes;scroll:yes;resizable:yes;location:yes;status:yes') ;
	}	
	//判断开始是否大于结束时间
    function checkTime(){
    	var time1= dojo.widget.byId("startTime");
    	var startTime = time1.getValue();
    	var time2= dojo.widget.byId("endTime");
    	var endTime = time2.getValue();
   		if(startTime>endTime){
   			alert("开始时间大于结束时间!");
   		}else{
   			form1.submit();
   		}
    }
    
   	// 检验是否选上需要处理的选项 
	function check(){
   		var stime=document.getElementById("stime").value;
   		var etime=document.getElementById("etime").value;
   		var bj=document.getElementById("bj").value;
		if(bj=="1"){
			if(etime!=null && stime!=null && stime<etime){
				form1.action="goudui1";
				form1.submit();
	   		}else{
				alert("选择正确的时间!");
	   		}
		}else{
			var workorderObjectNos = document.getElementsByName('disposeId');
			var gets = new Array();
			var k = 0;
			var result = 0;
			for(var i=0; i<workorderObjectNos.length; i++){
				if(workorderObjectNos[i].checked){
				  // alert("第" + i + "个值：" + workorderObjectNos[i].value);
				    gets[k] = workorderObjectNos[i].value;
				    result =  gets[k];
	   				k++;
				}
	 		}
	 		if(result==0){
				alert("请选上勾兑的选项！");
				if(stime!=null && etime!=null && stime>etime){
		   			alert("开始时间大于结束时间!");
		   		}
			}else{
				form1.action="goudui";
				form1.submit();
			}
		}
	}
	</SCRIPT>
  </head>
  <body>
  	<H3 align=center>手工勾兑</H3>
  	<s:form id="form1" action="toHeadGoudui" theme="simple" method="post">
  		<table align="center">
  			<tr>
  				<td>商户号</td>
  				<td>
  					<s:textfield theme="simple" name="merchant.merno" type="text"/>
  				</td>
  				<td>支付情况</td>
	 			<td>
		 			<s:select name="trade.tradeState" list="#{'':'所有','1':'成功','0':'失败','5':'未返回'}" listKey="key" listValue="value"/>
		 		</td>
		 		<td>卡号</td>
  				<td>
  					<s:textfield theme="simple" name="card.cardNo" type="text"/>
  				</td>
  			</tr>
  			
  			<tr>
  				<td>授权号</td>
		 		<td>
		 			<s:textfield theme="simple" name="trade.VIPAuthorizationNo" type="text"/>
		 		</td>
		 		<td>支付通道</td>
	 			<td>
	 				<s:select name="chann.id" list="list" listKey="id" listValue="channelName" headerKey="" headerValue="所有"/>	 				
				</td>
				<td>交易流水订单号</td>
		 		<td>
		 			<s:textfield theme="simple" name="trade.orderNo" type="text"/>
		 		</td>
  			</tr>
  			<tr>
  				<td>终端号</td>
  				<td>
  					<s:textfield theme="simple" name="trade.VIPTerminalNo" type="text"/>
  				</td>
  			</tr>
  			<tr>
  				<td>StarTime：</td>
  				<td>
  					<input id="stime" type="text" name="stime" ondblclick="cleanDate(this.id)" size="15" value="<s:property value='stime'/>"/>
  				</td>
  				<td>EndTime：</td>
  				<td>
  					<input id="etime" type="text" name="etime" ondblclick="cleanDate(this.id)" size="15" value="<s:property value='etime'/>"/>
  				</td>
  			</tr>
  		</table>
  	   	<table align="center">
  	   		<tr>
  	   			<td>
  					<input type="button" value="确定勾兑" onclick="check()"/>
  				</td>
  				<td>
  					<input type="submit" value="查询"/>
  				</td>
  				<td>
  					<input type="reset" value="取消">
  				</td>
  			</tr>
  	   	</table>
    	<table borderColor=#ffffff cellSpacing=0 cellPadding=0 width="900" align=center 
			bgColor=#ffffff borderColorLight=#000000 border=1 height="10">
	   		<tr bgColor=#cccccc align=center>
	   			<td>
  					<input type="hidden" id="bj" value="0" />
  					<input type="checkbox" onclick='chkall("form1",this)' name=chk>
  				</td>
    			<td>商户号</td>
    			<td>流水订单号</td>
    			<td>订单号</td>	
    			<td>交易金额(RMB)</td>
    			<td>交易时间</td>
    			<td>处理时间</td>
    			<td>支付状态</td>
    			<td>通道</td>
    			<td>授权号</td>
    			<td>终端号</td>
    			<td>卡号</td>
    			<td>CVV</td>
    			<td>有效期</td>
    			
    			<td>是否勾兑</td>		
    			<td>操作</td>		    			
    		</tr>
	    		<s:iterator id="gouDuiList" value="info.result">
		    		<tr align=center>
		    			<td>
	  						<input type="checkbox" name="disposeId" id="id" value="<s:property value="#gouDuiList[0].id"/>">
	  					</td>
		    			<td>
		    				<s:property value="#gouDuiList[1].merno"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="#gouDuiList[0].orderNo"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="#gouDuiList[0].merchantOrderNo"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="#gouDuiList[0].rmbAmount"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="#gouDuiList[0].tradeTime"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="#gouDuiList[0].VIPDisposeDate"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="states.getStateName(#gouDuiList[0].tradeState,1)" escape="false"/>&nbsp;
		    			</td>   
		    			<td>
		    				<s:property value="#gouDuiList[2].channelName"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="#gouDuiList[0].VIPAuthorizationNo"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="#gouDuiList[0].VIPTerminalNo"/>&nbsp;
		    			</td>
		    			<td>
		    				<s:property value="getCarNo(#gouDuiList[4].cardNo)"/>&nbsp;
		    			</td>
		    			<td>
		    			<s:property value="#gouDuiList[4].cvv2"/>&nbsp;
		    			</td>
		    			<td>
		    			<s:property value="#gouDuiList[4].expiryDate"/>&nbsp;
		    			</td>
		    			
		    			<td>
		    				<s:property value="states.getStateName(#gouDuiList[0].tradeState,5)" escape="false"/>&nbsp;
		    			</td>
		    			<td>
		    			<a href="#" onclick="buchongxinxi('<s:property value="#gouDuiList[0].id"/>')">修改</a>
		    			</td>
		    		</tr>
	    		</s:iterator>
    	</table>
    	<table align="center">
    		<tr>
    			<td>
    				<input type="button" value="确定勾兑" onclick="check()"/>
    			</td>
    		</tr>
    		<tr bgColor=#ffffff>
				<td colspan="30">
				<pages:pages value="info" beanName="info"
					formName="getElementById('form1')" />
				</td>
		   </tr>
    	</table>	
    	<script language="javascript" type="text/javascript">

    	loadcalendar('stime', 'stime', '%Y-%m-%d', false, true, "cn");
        loadcalendar('etime', 'etime', '%Y-%m-%d', false, true, "cn");
            function cleanDate(vid){
            	document.getElementById(vid).value="";
            }
        </script>
    </s:form>
    <script language="javascript">	
		var temflag='<s:property value='flag'/>';
		if(temflag==1){
			alert("勾兑成功!");
		}else if(temflag==2){
			alert("系统出现异常!");
		}else if(temflag==3){
			alert("商户号错误!");
		}		
	</script>	
  </body>
</html>
