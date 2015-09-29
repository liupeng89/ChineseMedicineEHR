<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<style type="text/css">
		#description {
			position: relative;
			text-align:left;
			vertical-align:middle;
			border-radius: 0px;
		  	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
		  	border-bottom: 1px solid #aaa;
		  	
		  	padding: 40px;
			width: 274px;
			background-color: #F7F7F7;
			margin: 20px auto 10px;
			border-radius: 0px;
			box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
			overflow: hidden;
			text-align:center;
		}
		#qdescription {
			
			position: relative;
			background-color:#f5f5f5;
			text-align:left;
			vertical-align:middle;
			height:80%;
			width:100%;
			border-radius: 0px;
		  	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
		}
		body {
		margin:0;
		padding:0;
		font:12px/15px "Helvetica Neue",Arial, Helvetica, sans-serif;
		color: #555;
		border: 1px solid #aaa;
		height:800px;
	}
	table, caption, tbody, tfoot, thead, tr, th, td {
		margin:0;
		padding:0;
		border:0;
		outline:0;
		font-size:100%;
		vertical-align:baseline;
		background:transparent;
	}
	/*
	Pretty Table Styling
	CSS Tricks also has a nice writeup: http://css-tricks.com/feature-table-design/
	*/
	
	table {
		overflow:hidden;
		border:1px solid #d3d3d3;
		background:#fefefe;
		width:100%;
		margin:0% auto 0;
		-moz-border-radius:0px; /* FF1+ */
		-webkit-border-radius:0px; /* Saf3-4 */
		border-radius:0px;
		-moz-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
		-webkit-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
	}
	
	th, td {padding:8px 18px 8px; text-align:center; }
	
	th {padding-top:5px; text-shadow: 1px 1px 1px #fff; background:#e8eaeb;}
	
	td {border-top:1px solid #e0e0e0; border-right:1px solid #e0e0e0;}
	
	tr.odd-row td {background:#f6f6f6;}
	
	td.first, th.first {text-align:left}
	
	td.last {border-right:none;}
	
	</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div >
		<div id="description">
			<form action="medicineProba" method="get">
				<p>
	    				批次：
	    				<select name="batch">  
        					<c:forEach items="${batchList }" var="item">  
            					<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item }</option>  
        					</c:forEach>  
    					</select>  
	    			</p>
	    		<br>
			 	请输入中药：（空格分隔）
			 	<br>
			 	
			 	<input type="text" name="medicines" />
			 	<input type="submit" value="query" />	
			</form>
			<br>
			<p>
				<div>
					<h3>输入中药：</h3> ${medicines }
				</div>
			</p>
		</div>
	</div>
	<div>
		<table border="1">
			<c:if test="${results.size() == 1 }">
				<c:if test="${medicines.trim().contains(' ') }">
					<tr>
						<th></th>
						<th>名称</th>
						<th>并集数量</th>
						<th>并集百分百</th>
						<th>交集数量</th>
						<th>交集百分百</th>
					</tr>
					<c:forEach var="item" items="${results}" varStatus="status">
						<c:if test="${item.value.size() > 2 }">
						<tr>
							<td>${status.index + 1 }</td>
							<td> ${item.key.replace("|",",") }</td>
							<td>${item.value.get(0) }</td>
							<td>
								<fmt:formatNumber type="percent" maxFractionDigits="3" value="${item.value.get(1) }"	/>
							</td>
							<td>${item.value.get(2) }</td>
							<td>
								<fmt:formatNumber type="percent" maxFractionDigits="3" value="${item.value.get(3) }"	/>
							</td>
						</tr>
						</c:if>
					</c:forEach>
				</c:if>
				<c:if test="${!medicines.trim().contains(' ') }">
					<tr>
						<th></th>
						<th>名称</th>
						<th>数量</th>
						<th>百分比</th>
					</tr>
					<c:forEach var="item" items="${results}" varStatus="status">
						<tr>
							<td>${status.index + 1 }</td>
							<td>${item.key }</td>
							<td>${item.value.get(0) }</td>
							<td>
								<fmt:formatNumber type="percent" maxFractionDigits="3" value="${item.value.get(1) }"	/>
							</td>
						</tr>
					</c:forEach>
					
				</c:if>
			</c:if>
			<c:if test="${results.size() > 1}">
				<tr>
					<th></th>
					<th>名称</th>
					<th>并集数量</th>
					<th>并集百分百</th>
					<th>交集数量</th>
					<th>交集百分百</th>
				</tr>
				<c:forEach var="item" items="${results}" varStatus="status">
					<tr>
						<td>${status.index + 1 }</td>
						<td>${item.key.replace("|",",") }</td>
						<td>${item.value.get(0) }</td>
						<td>
							<fmt:formatNumber type="percent" maxFractionDigits="3" value="${item.value.get(1) }"	/>
						</td>
						<td>${item.value.get(2) }</td>
						<td>
							<fmt:formatNumber type="percent" maxFractionDigits="3" value="${item.value.get(3) }"	/>
						</td>
					</tr>
				</c:forEach>
			</c:if>
		</table>
			<c:forEach var="item" items="${descriptionlist}" varStatus="status">
				${item }
			</c:forEach>
		</div>
</body>
</html>