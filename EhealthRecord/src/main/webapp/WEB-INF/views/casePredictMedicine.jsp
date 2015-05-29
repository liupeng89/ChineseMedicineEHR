<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Map,java.util.Set" %>
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
	<script src="js/bootstrap.min.js"></script>
	<div id="left">
		<div>
			<h1>处方预测</h1>
			<form name="form" method="post">

				<div id="left_rigth">
					<p>
						<label>请入病例号(1-${allcount })</label>
					</p>
					<p>
						<input type="text" name="count" />
					</p>
					<p>
						<input type="submit" value="根据病例预测处方" onclick="javascript: form.action='predictByCount';" />
					</p>
				</div>
			</form>
		</div>
	</div>
	<div id="right">
		<div id="">
		<!-- 输入数据 -->
		<div style="width:100%">
			<hr>
			<h2>批次：${batch == 'null' ? '全部批次' : batch }</h2>
			<hr>
			<h3>诊断：</h3>
			<p>${diagnose }</p>
			<hr>
			<h3>描述：</h3>
			<p>${description }</p>
			<hr>
		</div>
		<!-- 预测结果 -->
		<c:if test="${medicineListByStatis != null }">
			<div style="width:100%">
				<h3>基于病例统计的预测处方：</h3>
				<p>
					<c:forEach items="${medicineListByStatis }" var="item">
						${item }
					</c:forEach>
				</p>
				<p>
					${predictMedicine }
				</p>
			</div>
		</c:if>
		<!-- 预测结果 -->
		<c:if test="${medicineListByMachine != null }">
			<div style="width:100%">
				<hr>
				<h3>基于机器学习的预测处方：</h3>
				<p>
					<c:forEach items="${medicineListByMachine }" var="item">
						${item }
					</c:forEach>
				</p>
			</div>
		</c:if>
		<!-- 预测结果 -->
		
		<c:if test="${orignMedicines != null }">
			<div style="width:100%">
				<hr>
				<h3>原始病例处方：</h3>
				<p>
					<c:forEach items="${orignMedicines }" var="item">
						${item }
					</c:forEach>
				</p>
			</div>
		</c:if>
			
		
		<!-- 相似病历 -->
		<c:if test="${similaryRecords != null}">
			<div id="similayrecords">
				<hr>
				<h3>相似病历：</h3>
				<table id="similarytable" border="1px">
					<tr>
						<th>序号</th>
						<th>挂号号</th>
						<th>中医诊断</th>
						<th>中医描述</th>
						<th>备注</th>
					</tr>
					<c:forEach var="erecord" items="${similaryRecords }" varStatus="status">
						<tr>
				        	<td>${status.index+1 }</td>
				        	<td>${erecord.getRegistrationno() }</td>
				        	<td>${erecord.getChinesediagnostics() }</td>
				        	<td>${erecord.getConditionsdescribed() }</td>
			    	        <td><a href="detailRecord?ehealthregno=${erecord.getRegistrationno() }">详细信息</a></td>
		            	</tr>
					</c:forEach>
				</table>
			</div>
		</c:if>
	
	</div>
	</div>

</body>
</html>