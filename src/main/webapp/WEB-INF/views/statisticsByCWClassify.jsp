<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
       <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
	           中西医诊断统计分类
	            </h1>
	            </div>
	        </div>
			<form action="cwdiagstatis" method="get">
	        	年度：
		    	<select name="batch">  
	  				<%-- <c:forEach items="${batchList }" var="item">  
	          			<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item }</option>  
	        		</c:forEach>  --%>
	        		<option value="null">全部</option>
                        	<option value="2012" selected>2012</option>
                        	<option value="2011">2011</option>
                        	<option value="2010">2010</option>
                        	<option value="2009">2009</option> 
	    		</select>
			    <input type="submit" class="btn btn-success btn-xs" value="中西医诊断统计分类" /> 
	        </form>
		</div>
		<div>
			<%
			int count = (Integer) request.getAttribute("count");
			%>
			<h2>中医诊断统计</h2>
			<table class="table table-bordered">
				<tr>
					<th>序号</th>
					<th>描述</th>
					<th>数量</th>
				</tr>
				<c:forEach var="item" items="${chineseDiagnostics }" varStatus="status">
					<tr>
						<td>${status.index + 1 }</td>
						<td>${item.key }</td>
						<td>${item.value }
							(
								<fmt:formatNumber type="percent" maxFractionDigits="3" value="${item.value / count}"	/>
							)
						</td>
					</tr>
				</c:forEach>
			</table>
			
			<br>
			<br>
			<h2>西医诊断统计</h2>
			<table class="table table-bordered">
				<tr>
					<th>序号</th>
					<th>描述</th>
					<th>数量</th>
				</tr>
				
				<c:forEach var="item" items="${westernDiagnostics }" varStatus="status">
					<tr>
						<td>${status.index + 1 }</td>
						<td>${item.key }</td>
						<td>${item.value }
							(
								<fmt:formatNumber type="percent" maxFractionDigits="3" value="${item.value / count}"	/>
							)
						</td>
					</tr>
				</c:forEach>
			</table>
			<br>
			<br>
		</div>
	</div>
</body>
</html>