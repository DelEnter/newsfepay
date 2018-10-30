<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <script language="JavaScript">
		function chkall(input1,input2)//判断勾选
		{
			alert("此选项 针对所有根据条件查询出来的数据！(并不只是当前页面)");
		    var objForm = document.forms[input1];//form表单重点所有数据
		    var objLen = objForm.length;//数据的条数
		    for (var iCount = 0; iCount < objLen; iCount++)//循环这些数据
		    {
		        if (input2.checked == true)//如果全选框被选中
		        {
		            if (objForm.elements[iCount].type == "checkbox")
		            {
		                objForm.elements[iCount].checked = true;//选中的数据状态为true
		                document.getElementById("bj").value="1";
		            }
		        }
		        else//如果全选框没有被选中
		        {
		            if (objForm.elements[iCount].type == "checkbox")
		            {
		                objForm.elements[iCount].checked = false;//选中的数据状态为false
		                document.getElementById("bj").value="0";
		            }
		        }
		    }
		}
	</script>
    
  </head>
  
  <body>
    
  </body>
</html>
