<%-- 
    Document   : statics
    Created on : Feb 14, 2015, 5:01:18 PM
    Author     : lp
--%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.um.model.EHealthRecord,java.util.List,java.util.ArrayList,java.util.Set,java.util.Map,com.um.mongodb.converter.MedicineStatics" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <style type="text/css">
        body { 
			margin:0; 
			padding:20px; 
			font:13px "Lucida Grande", "Lucida Sans Unicode", Helvetica, Arial, sans-serif;
			border: 1px solid #aaa;
			}
    </style>
        
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/bootstrap.min.css">
        <title>JSP Page</title>
    </head>
    <body>
    	<a href="javascript:history.back()">Go Back</a>
        <table class="table table-bordered">
           <thead>
           		<tr class="info">
	                <th>编号</th>
	                <th>名称</th>
	                <th>数量</th>
           		</tr>
           </thead>
            <tbody>
            	 <c:forEach var="item" items="${medicinestatics }" varStatus="status">
	            	<tr>
	            		<td>${status.index+1 }</td>
	            		<td>${item.key }</td>
	            		<td>${item.value }
	            			(
	            				<fmt:formatNumber type="percent" maxFractionDigits="3" value="${item.value >= patientCount ? 1.00 : item.value / patientCount}"	/>
	            			)
	            		</td>
	            	</tr>
	            </c:forEach>
            </tbody>
           
        </table>
    </body>
</html>
