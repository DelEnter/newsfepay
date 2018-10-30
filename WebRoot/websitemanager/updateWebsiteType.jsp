<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="pages" uri="/xs-pages" %><head>
	<title>更新网站类型</title>
</head>
    <script language="JavaScript" type="text/JavaScript">
	function addCourse(f) {	
		var auditStatus= document.getElementById("auditStatus").value//获取审核状态
		var merNo= document.getElementById("merNo").value//获取商户号
		var ids = document.getElementById("ids").value; //获取选中的网址ID
		var t = document.getElementById("webSiteType1"); //获取已有的网址类型的
		var webSiteType1=t.options[t.selectedIndex].value;//获取选中的网址类型
		var webSiteType2=document.getElementById("webSiteType2").value;//获取新添加的网址类型
		var webSiteType="";//定义一个网址类型为空
		if(ids!=""){
			if(webSiteType1!=""){
				webSiteType=webSiteType1;//赋值已有的网址类型
			}
			if(webSiteType2!=""){
				webSiteType=webSiteType2;//赋值新加的网址类型
			}
			goFormWindow(f,"../PaySystem/auditWebSiteManager.action?ids="+ids+"&webSiteType="+webSiteType);//选中的ID和网址类型传到action去执行操作
		}
		else{
			if(webSiteType1!=""){
				webSiteType=webSiteType1;//赋值已有的网址类型
			}
			if(webSiteType2!=""){
				webSiteType=webSiteType2;//赋值新加的网址类型
			}
			goFormWindow(f,"../PaySystem/auditWebSiteManager1.action?auditStatus="+auditStatus+"&webSiteType="+webSiteType+"&merNo="+merNo);//选中的ID和网址类型传到action去执行操作
		}
	}
</script>  
<body>
<div id="title" value="更新网站类型"/>
<input type="hidden" id="ids" value='<s:property value="ids" />' />
<div id="resizetable" width="400" height="360">
<div class="windows">
  <s:form action="toAuditWebSiteManager" id="form2" namespace="/PaySystem" theme="simple">
   <div class="left">&nbsp;已有网站种类：</div>
     <div class="right"><s:select id="webSiteType1" list="webSiteTypeList" headerKey="" headerValue="--请选择--" /></div>
     <div style="clear:both">&nbsp;</div> <br/>
     <div class="left">&nbsp;网站类型：&nbsp;</div>
     <div class="right"><input type="text" id="webSiteType2" name="webSiteType2" /></div>
      <div style="clear:both">&nbsp;</div> 
      <div style=" width:300px;font-size: 12px;color: blue;margin-left: 50px">提示:&nbsp;&nbsp;如果网址分类已存在，请下拉选择，如果不存在请在网站类型输入，二填一即可，如果都填默认输入框有效!</div>
     <div class="left">
        <input type="button" onClick="addCourse(this.form);" value="更新分类" class="windows_icon1"/>
     </div>
  </s:form>
</div>
</div>
</body>
