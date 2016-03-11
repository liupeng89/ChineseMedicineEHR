<%@page import="java.text.DecimalFormat"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map,java.util.HashMap"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/style.css">
</head>
	<body>
		<div class="container">
			<div>
				<div class="row">
	                    <div class="col-lg-12">
	                        <h1 class="page-header">
	                            中医诊断统计
	                        </h1>
	                    </div>
	                </div>
					<form action="CDMedicineStatis" method="get">
			    		年度：
			    		<select name="batch">  
		        			<c:forEach items="${batchList }" var="item">  
		            			<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item }</option>  
		        			</c:forEach>  
		    			</select>  
			    		<input type="submit" class="btn btn-success btn-xs" value="中医诊断处方统计" />
	        		</form>
			</div>
			
		</div>
	</body>
</html>