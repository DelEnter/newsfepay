<%@ page import="com.ecpss.util.MD5" %>
<%
    String MD5key; //MD5key值
    MD5key = "YFYJkCkS";

    String MerNo;   //商户ID
    MerNo = "1688";

    String BillNo;  //订单编号
    BillNo = String.valueOf(System.currentTimeMillis());
    String Currency;    //币种
    Currency = "1";
    String Amount;  //支付金额
    Amount = "1.00";

    String Language;    //支付语言
    Language = "en";

    String ReturnURL;   //返回地址
    
    ReturnURL = "http://www.sslepay.com/payresult.jsp";


//    ReturnURL = "http://192.168.1.23:8888/pay/payresult.jsp";


    String tradeAdd;   //返回地址
    
    String remark = "好产品";
   

    String md5src;  //加密字符串    
    md5src = MerNo + BillNo + Currency + Amount + Language + ReturnURL + MD5key ;
    MD5 md5 = new MD5();
    String MD5info; //MD5加密后的字符串
    MD5info = md5.getMD5ofStr(md5src);
    
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>测试接口</title></head>

<body>
<form action="sslpaymenttest" method="post">
    <table align="center">
        <caption align="top"><font color=blove>=支付信息</font></caption>
       
        <tr><td>商户ID号</td>
            <td>
            	<input type="text" name="MerNo" value="<%=MerNo%>">
            </td>
        </tr>
        <tr><td>币种</td>
            <td><input type="text" name="Currency" value="<%=Currency%>"></td></tr>

        <tr><td>订单号</td>
            <td><input type="text" name="BillNo" value="<%=BillNo%>"></td></tr>

        <tr><td>订单金额</td>
            <td><input type="text" name="Amount" value="<%=Amount%>"></td></tr>

        <tr><td>返回地址</td>
            <td><input type="text" name="ReturnURL" value="<%=ReturnURL%>" size="60"></td></tr>

        <tr><td>支付语言</td>
            <td><input type="text" name="Language" value="<%=Language%>"></td></tr>

		<tr>
			<td>备注</td>
			<td><input type="text" name="remark" value="<%=remark%>"></td>
		</tr>

        <tr><td>MD5加密字符串</td>
            <td><input type="text" name="MD5info" value="<%=MD5info%>"></td></tr>
         <tr><td>prouducts imformation</td>
            <td><input type="text" name="products" value="Products"></td></tr>            
         <tr><td>first name</td>
            <td><input type="text" name="firstname" value="firstname"></td></tr>
         <tr><td>listname</td>
            <td><input type="text" name="lastname" value="lastname"></td></tr>   
            
         <td>BillingAddress</td>               
         <tr><td>cardbank</td>
         <td><input type="text" name="cardbank" value="cardbank"></td></tr>     
         <tr><td>email</td>
            <td><input type="text" name="email" value="lcnsy@yahoo.com.cn"></td></tr>            
         <tr><td>phone</td>
            <td><input type="text" name="phone" value="0212136545"></td></tr>            
         <tr><td>zipcode</td>
            <td><input type="text" name="zipcode" value="565452"></td></tr>            
         <tr><td>address</td>
            <td><input type="text" name="address" value="shanghaiStreat"></td></tr>   
         <tr><td>city</td>
            <td><input type="text" name="city" value="Shanghai"></td></tr>            
         <tr><td>state</td>
            <td><input type="text" name="state" value="shanghai"></td></tr>            
         <tr><td>country</td>
            <td><input type="text" name="country" value="China"></td></tr>  

         <td>ShippingAddress</td>             
         <tr><td>ShippingFirstName</td>
            <td><input type="text" name="shippingFirstName" value="hali"></td></tr>   
         <tr><td>ShippingLastName</td>
            <td><input type="text" name="shippingLastName" value="coms"></td></tr>                        
         <tr><td>shippingEmail</td>
            <td><input type="text" name="shippingEmail" value="lcnsy@yahoo.com.cn"></td></tr>            
         <tr><td>shippingPhone</td>
            <td><input type="text" name="shippingPhone" value="02152658748"></td></tr>            
         <tr><td>shippingZipcode</td>
            <td><input type="text" name="shippingZipcode" value="56545874"></td></tr>            
         <tr><td>shippingAddress</td>
            <td><input type="text" name="shippingAddress" value="shanghaiStreat"></td></tr>   
         <tr><td>shippingCity</td>
            <td><input type="text" name="shippingCity" value="Shaihai"></td></tr>   
         <tr><td>shippingSstate</td>
            <td><input type="text" name="shippingSstate" value="Shaihai"></td></tr>   
         <tr><td>shippingCountry</td>
            <td><input type="text" name="shippingCountry" value="China"></td></tr>   
    </table>
    <p align="center"><input type="submit" name="b1" value="支付"></p>

</form>
</body>
</html>
