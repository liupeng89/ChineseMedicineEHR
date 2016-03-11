<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Map,java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
			margin: 0 auto 10px;
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
			height:160px;
			width:100%;
			border-radius: 0px;
		  	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
		}
		#qmedicine {
			
			position: relative;
			background-color:#f5f5f5;
			text-align:left;
			vertical-align:middle;
			height:160px;
			width:100%;
			border-radius: 0px;
		  	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
		}
		 body { 
			margin:0; 
			padding-top:20px; 
			font:13px "Lucida Grande", "Lucida Sans Unicode", Helvetica, Arial, sans-serif;
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
	
	th {text-align:center; height:5px;width:10%}
	
	th {padding-top:0px; text-shadow: 1px 1px 1px #fff; background:#e8eaeb;font-size:15px;}
	
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
	
	</style>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>
</head>
<body>
	<div>
	<div id="description">
		<h3>处方预测</h3>
		<div>
			<form action="diagmedicine" method="post">
				<p>
					<p>
	    				年度：
	    				<select name="batch">  
        					<%-- <c:forEach items="${batchList }" var="item">  
            					<option value="${item }">${item }</option>  
        					</c:forEach>   --%>
        					<option value="null">全部</option>
                        	<option value="2012" selected>2012</option>
                        	<option value="2011">2011</option>
                        	<option value="2010">2010</option>
                        	<option value="2009">2009</option>
    					</select>  
	    			</p>
					<label>输入症状：</label>
					<br>
					<br>
					诊断类型：<input type="text" name="diagnose" />
					<br>
					<br>
					病症描述：<input type="text" name="description" />
					<br>
					<br>
					<input type="submit" value="查询" />
				</p>
			</form>
		</div>
	</div>

	<div>
		<table>
			<tr>
				<th><h4>输入信息</h4></th>
				<td>诊断类型：${diagnose }  <br><br> 描述：${description }</td>
			</tr>
			<tr>
				<th><h4>预测处方</h4></th>
				<td>
					<c:forEach var="name" items="${medicines }">
						${name },
					</c:forEach>
				</td>
			</tr>
		</table>
	</div>
	<div>
		<!-- 相似病历 -->
		<span>
			<h3>相似病历</h3>
		</span>
		<table border="1">
        	 <tr>
                <th>编号</th>
                <th>信息</th>
                <th>详细</th>
            </tr>
            	<c:forEach var="erecord" items="${similaryRecords }" varStatus="status">
            		<tr>
	            		<td>${status.index+1 }</td>
	            		<td>挂号号： ${erecord.getRegistrationno() };时间：${erecord.getDate() }；姓名：${erecord.getPatientInfo().getName() }；地址：${erecord.getPatientInfo().getAddress() }</td>
	            		<td><a href="detailRecord?ehealthregno=${erecord.getRegistrationno() }">详细信息</a></td>
            	 	</tr>
            	</c:forEach>
        </table>
	</div>
	</div>
</body>
</html>
