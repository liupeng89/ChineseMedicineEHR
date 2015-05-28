<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Map,java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
    	
	
	body {
		margin:0;
		padding:0;
		font:12px/15px "Helvetica Neue",Arial, Helvetica, sans-serif;
		color: #555;
		background:#f5f5f5;
		border: 1px solid #aaa;
		height:1200px;
		width:100%;
	}
	h3 {
		
		color:#4169E1 ;
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
		margin:0% auto 0;
		-moz-border-radius:0px; /* FF1+ */
		-webkit-border-radius:0px; /* Saf3-4 */
		border-radius:0px;
		-moz-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
		-webkit-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
	}
	
	th, td {padding:8px 18px 8px; text-align:left; }
	
	th {padding-top:5px; text-shadow: 1px 1px 1px #fff; background:#e8eaeb;}
	
	td {border-top:1px solid #e0e0e0; border-right:1px solid #e0e0e0;}
	
	tr.odd-row td {background:#f6f6f6;}
	
	td.first, th.first {text-align:left}
	
	td.last {border-right:none;}
	
	/*
	Background gradients are completely unnecessary but a neat effect.
	*/
	
	td {
		background: -moz-linear-gradient(100% 25% 90deg, #fefefe, #f9f9f9);
		background: -webkit-gradient(linear, 0% 0%, 0% 25%, from(#f9f9f9), to(#fefefe));
	}
	
	tr.odd-row td {
		background: -moz-linear-gradient(100% 25% 90deg, #f6f6f6, #f1f1f1);
		background: -webkit-gradient(linear, 0% 0%, 0% 25%, from(#f1f1f1), to(#f6f6f6));
	}
	
	th {
		background: -moz-linear-gradient(100% 20% 90deg, #e8eaeb, #ededed);
		background: -webkit-gradient(linear, 0% 0%, 0% 20%, from(#ededed), to(#e8eaeb));
	}
	
	tr:first-child th.first {
		-moz-border-radius-topleft:5px;
		-webkit-border-top-left-radius:5px; /* Saf3-4 */
	}
	
	tr:first-child th.last {
		-moz-border-radius-topright:5px;
		-webkit-border-top-right-radius:5px; /* Saf3-4 */
	}
	
	tr:last-child td.first {
		-moz-border-radius-bottomleft:5px;
		-webkit-border-bottom-left-radius:5px; /* Saf3-4 */
	}
	
	tr:last-child td.last {
		-moz-border-radius-bottomright:5px;
		-webkit-border-bottom-right-radius:5px; /* Saf3-4 */
	}
	
	#input {
	
		float:left;
		width:35%;
		height:800px;
	}
	
	#zhenxing {
		float:left;
		width:100%;
	}
	#description {
		float:left;
		width:100%;
	}
	
	#table1{
		
		width:100%;
		border:1px;
	}
	#table2 {
	
		width:100%;
		border:1px;
	}
	
	#output {
	
		float:right;
		width:65%;
	}
	#similayrecords {
		float:left;
		width:100%;
	}
	#similarytable {
		float:left;
		width:100%;
	}
	#left {
		float:left;
		width:55%;
	}
	#right {
		float:right;
		width:45%;
	}
	#left_left {
		float:left;
		width:60%;
	}
	#left_right {
		float:right;
		width:40%;
	}
    </style>
</head>
<body>
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