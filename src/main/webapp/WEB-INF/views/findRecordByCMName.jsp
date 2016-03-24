<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/style.css">
<title>Insert title here</title>
</head>
<body>
	<div class="container">
			<div class="row">
	        	<div class="col-lg-12">
	            <h1 class="page-header">
	           		输入中药查找病历
	            </h1>
	            </div>
	        </div>
			<form action="findrecordsbycmnane" method="get">
			
				<p>
	    				年度：
	    				<select name="batch">  
        					<option value="null">全部</option>
                        	<option value="2012" selected>2012</option>
                        	<option value="2011">2011</option>
                        	<option value="2010">2010</option>
                        	<option value="2009">2009</option>
    					</select>  
	    		</p>
			 	<input type="text" name="medicines" />
			 	<input type="submit" class="btn btn-success btn-xs" value="查询" />
			 	<label>(多味中药空格分割)</label>	
			</form>
			<hr>
			<p class="text-danger">
				<h4>输入中药：</h4> ${medicines }
			</p>
			
			<table class="table table-bordered">
        	 <tr class="info">
                <th>编号</th>
                <th>信息</th>
                <th>详细</th>
            </tr>
            	<c:forEach var="erecord" items="${targetList }" varStatus="status">
            		<tr>
	            		<td>${status.index+1 }</td>
	            		<td>挂号号： ${erecord.getRegistrationno() };时间：${erecord.getDate() }；姓名：${erecord.getPatientInfo().getName() }；地址：${erecord.getPatientInfo().getAddress() }</td>
	            		<td><a href="detailRecord?ehealthregno=${erecord.getRegistrationno() }">详细信息</a></td>
            	 	</tr>
            	</c:forEach>
        </table>
        <!-- page -->
       <%--  <div>
        	<c:forEach var="i" begin="1" end="${pagenum }">
        		<a href="pages/${i }" >${i }</a>
        	</c:forEach>
        </div> --%>
			
		</div>

</body>
</html>