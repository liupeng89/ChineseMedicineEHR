<%-- 
    Document   : query
    Created on : Feb 14, 2015, 12:28:38 PM
    Author     : lp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.um.model.EHealthRecord,java.util.List,java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta enctype="multipart/form-data">
        <link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/style.css">
        <title>JSP Page</title>
    </head>
    <body>
        <table class="table table-bordered">
        	 <tr>
                <th>编号</th>
                <th>信息</th>
                <th>详细</th>
            </tr>
            	<c:forEach var="erecord" items="${ehealthrecrods }" varStatus="status">
            		<tr>
	            		<td>${status.index+1 }</td>
	            		<td>挂号号： ${erecord.getRegistrationno() };时间：${erecord.getDate() }；姓名：${erecord.getPatientInfo().getName() }；地址：${erecord.getPatientInfo().getAddress() }</td>
	            		<td><a href="detailRecord?ehealthregno=${erecord.getRegistrationno() }">详细信息</a></td>
            	 	</tr>
            	</c:forEach>
        </table>
    </body>
</html>
