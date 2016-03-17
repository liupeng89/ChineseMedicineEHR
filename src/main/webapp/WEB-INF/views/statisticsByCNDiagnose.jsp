<%@page import="java.text.DecimalFormat"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map,java.util.HashMap"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>statistics by chinese </title>
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
			    			<option value="null">全部</option>
                        	<option value="2012" selected>2012</option>
                        	<option value="2011">2011</option>
                        	<option value="2010">2010</option>
                        	<option value="2009">2009</option>  
		    			</select>  
			    		<input type="submit" class="btn btn-success btn-xs" value="中医诊断处方统计" />
	        		</form>
			</div>
		</div>
	</body>
</html>