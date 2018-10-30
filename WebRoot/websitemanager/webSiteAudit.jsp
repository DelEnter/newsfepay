<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="pages" uri="/xs-pages" %>
 <%@ include file="../include/dialog.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>商户网址审核</title>
<script language="JavaScript" src="../js/util.js"></script>
<link type="text/css" rel="stylesheet" href="../css/other.css" />
<%@ include file="../include/checkAll.jsp" %>
</head>
<script language="JavaScript" type="text/JavaScript">
function audit(){
	var bj=document.getElementById("bj").value;
	var merNo=document.getElementById("merNo").value;
	var auditStatus=document.getElementById("auditStatus").value;
	if(bj=="1"){
		if(merNo!="" && auditStatus!="" && auditStatus=="0"){
			openWindow("../PaySystem/toWebSiteType.action?merNo="+merNo+"&auditStatus"+auditStatus,"12");
		}else{
			alert("商户号、 审核状态不能为空、并且审核状态必须为未审核");
			return false;
		}
	}else{
		var str=document.getElementsByName("merIdAndStatus");
		var ids="";
		for(var i=0;i<str.length;i++){
			if(str[i].checked==true){
				var value=str[i].value.split("/");
				if(value[1]!="0"){
					alert("选项内有已审核过的项！");
					return false;
				}
					ids+=value[0]+",";
			}
		}
		if(ids==""){
			alert("请选择要审核的项！");
			return  false;
		}
	 	openWindow("../PaySystem/toWebSiteType.action?ids="+ids,"12"); 
	}
}

function noAudit(){//拒绝
	var bj=document.getElementById("bj").value;
	var merNo=document.getElementById("merNo").value;
	var auditStatus=document.getElementById("auditStatus").value;
	if(bj=="1"){
		if(merNo!="" && auditStatus!="" && auditStatus=="0"){
			if(confirm("确认审核拒绝？")){
				var remark=prompt("请输入备注：","");
				if(remark==""||remark==null){
					alert("审核原因为必填项！");
					return false;
				}else{
					window.location="../PaySystem/notAuditWebSiteManager1?merNo="+merNo+"&auditStatus="+auditStatus+"&remark="+remark;
				}
			}
		}else{
			alert("商户号、 审核状态不能为空、并且审核状态必须为未审核");
			return false;
		}
	}else{
		var str=document.getElementsByName("merIdAndStatus");
		var ids="";
		for(var i=0;i<str.length;i++){
			if(str[i].checked==true){
				var value=str[i].value.split("/");
				if(value[1]!="0"){
					alert("选项内有已审核过的项！");
					return false;
				}
				ids+=value[0]+",";
			}
		}
		if(ids==""){
			alert("请选择要审核的项！");
			return  false;
		}
		if(confirm("确认审核拒绝？")){
			var remark=prompt("请输入备注：","");
			if(remark==""||remark==null){
				alert("审核原因为必填项！");
				return false;
			}else{
				window.location="../PaySystem/notAuditWebSiteManager?remark="+remark+"&ids="+ids;
			}
		}
	}
}
function delWebSite(){//删除
	var bj=document.getElementById("bj").value;
	var merNo=document.getElementById("merNo").value;
	var auditStatus=document.getElementById("auditStatus").value;
	if(bj=="1"){
		if(merNo!="" && auditStatus!="" && auditStatus!="0"){
			if(confirm("确认删除所有按照条件查询的网址吗？(请谨慎操作！！！)")){
				window.location="../PaySystem/delAuditWebSiteManager1?merNo="+merNo+"&auditStatus="+auditStatus;
				}
		}else{
			alert("商户号、 审核状态不能为空、并且审核状态必须为已审核、已拒绝状态");
			return false;
		}
	}else{
		var str=document.getElementsByName("merIdAndStatus");
		var ids="";
		for(var i=0;i<str.length;i++){
			if(str[i].checked==true){
				var value=str[i].value.split("/");
				if(value[1]=="0"){
					alert("未审核的项不能删除！");
					return false;
				}
				ids+=value[0]+",";
			}
		}
		if(ids==""){
			alert("请选择要审核的项！");
			return  false;
		}
		if(confirm("确认删除吗？")){
		window.location="../PaySystem/delAuditWebSiteManager?ids="+ids;
		}
	}
}
function againAudit(){//重审
	var bj=document.getElementById("bj").value;
	var merNo=document.getElementById("merNo").value;
	var auditStatus=document.getElementById("auditStatus").value;
	if(bj=="1"){
		if(merNo!="" && auditStatus!="" && auditStatus!="0"){
			if(confirm("确定选中的项重新审核吗？")){
				window.location="../PaySystem/againAuditWebSiteManager1?merNo="+merNo+"&auditStatus="+auditStatus;
				}
		}else{
			alert("商户号、 审核状态不能为空、并且审核状态必须为已审核、已拒绝状态");
			return false;
		}
	}else{
		var str=document.getElementsByName("merIdAndStatus");
		var ids="";
		for(var i=0;i<str.length;i++){
			if(str[i].checked==true){
				var value=str[i].value.split("/");
				if(value[1]=="0"){
					alert("未审核的项不需要重审！");
					return false;
				}
				ids+=value[0]+",";
			}
		}
		if(ids==""){
			alert("请选择重新审核的项！");
			return  false;
		}
		if(confirm("确定选中的项重新审核吗？")){
		window.location="../PaySystem/againAuditWebSiteManager?ids="+ids;
		}
	}
}
function modify(id){
	openWindow('../PaySystem/toModifyWebSite.action?webId='+id,'12');
	}

</script>
<body>
<h3 align="center"> 商户管理</h3>
<s:form action="toAuditWebSiteManager?webtype=webtype" id="form1" namespace="/PaySystem">
     
                  <table width="80%" border="0" cellspacing="0" cellpadding="0" style="margin-top: 20px">
                    <tr>
                     <td align="left">&nbsp;商户号： 
                 	
                     <s:textfield theme="simple"  name="merNo" id="merNo" type="merNo"/>
                     <%-- <input type="text" name="merNo"  value="${merNo}"/>  --%>                     &nbsp;审核状态：
					 <s:select theme="simple" name='auditStatus'  id="auditStatus"  type="auditStatus" list="#{'':'---请选择---','0':'未审核','1':'审核通过','2':'拒绝'}"></s:select>
                     <%-- <select name="auditStatus">
                     <option value="">---请选择---</option>
                     <option value="0">未审核</option>
                     <option value="1">审核通过</option>
                     <option value="2">拒绝</option>                   	
                     </select> --%>                       &nbsp; 网站分类：
                     <s:select name="webtype" theme="simple" list="webTypeList" headerKey="" headerValue="--请选择--" />
                     <input type="submit" value="搜索"/> 
                     </td>
                    </tr>
                    
                  </table>
                  
                  <table width="100%" border="0" cellpadding="0" cellspacing="1" bgcolor="#333333" style="margin-top: 20px">
                    <tr>
                      <td width="4%" bgcolor="#FFFFFF" align="center">
                      <input type="hidden" onclick='chkall("form1",this)' id="bj" value="0">
  					  <input type="checkbox" onclick='chkall("form1",this)' name=chk>
  					  </td>
                      <td height="30" width="5%" align="center" bgcolor="#cccccc" class="font14px">商户号 </td>
                      <td height="30" width="21%" align="center" bgcolor="#cccccc" class="font14px">交易网址</td>
                      <td height="30" width="25%" align="center" bgcolor="#cccccc" class="font14px">返回网址</td>
                      <td height="30" width="12%" align="center" bgcolor="#cccccc" class="font14px">提交时间</td>
                      <td height="30" width="9%" align="center" bgcolor="#cccccc" class="font14px">审核人</td>
                      <td height="30" width="13%" align="center" bgcolor="#cccccc" class="font14px">备注</td>
                      <td height="30" width="7%" align="center" bgcolor="#cccccc" class="font14px">审核状态</td>
                      <td height="30" width="6%" align="center" bgcolor="#cccccc" class="font14px">操作</td>
                    </tr>
                    <s:iterator value="info.result" id="s">
                    <tr>
                      <td bgcolor="#FFFFFF" align="center"><input type="checkbox" name="merIdAndStatus" value="<s:property value="#s[0]"/>/<s:property value="#s[5]"/>"></td>
                      <td height="30" align="center" bgcolor="#FFFFFF"><font color=#930909> <s:property value="#s[1]"/></font></td>
                      <td height="30" align="center" bgcolor="#FFFFFF"><s:property value="#s[2]"/></td>
                      <td height="30" align="center" bgcolor="#FFFFFF"><font color=#930909> <s:property value="#s[3]"/></font>	</td>
                      <td height="30" align="center" bgcolor="#FFFFFF"><s:property value="#s[4]"/></td>
					  <td height="30" align="center" bgcolor="#FFFFFF"><s:property value="#s[6]"/></td>
					  <td height="30" align="center" bgcolor="#FFFFFF"><s:property value="#s[7]"/></td>
					  <s:if test="#s[5]==\"0\"">
					  <td height="30" align="center" bgcolor="#FFFFFF">未审核</td>
					  <td height="30" align="center"  bgcolor="#FFFFFF"><a href="#" onclick="modify('<s:property value="#s[0]"/>')">修 改</a></td>
					  </s:if>
					  <s:elseif test="#s[5]==\"1\"">
					  <td height="30" align="center" bgcolor="#FFFFFF">审核通过</td>
					  <td height="30" align="center" bgcolor="#FFFFFF"></td>
					  </s:elseif>
					  <s:elseif test="#s[5]==\"2\"">
					  <td height="30" align="center" bgcolor="#FFFFFF">拒绝</td>
					  <td height="30" align="center" bgcolor="#FFFFFF"></td>
					  </s:elseif>
					  
                    </tr>
                    </s:iterator>
  </table>
	
<pages:pages value="info" beanName="info" formName="getElementById('form1')" />
<table width="80%" border="0" cellspacing="0" cellpadding="0" style="margin-top: 20px">
                    <tr>
                      <td align="left">
                      <input type="button" value="通  过" onclick="audit()" />
                      <input type="button" value="拒  绝" onclick="noAudit()" />
                      <input type="button" value="重  审" onclick="againAudit()" /> 
                      <input type="button" value="删  除" onclick="delWebSite()" />  
                      </td>
                    </tr>
                    
                  </table>
</s:form>
</body>
</html>