<%-- 
    Document   : query
    Created on : Feb 14, 2015, 12:28:38 PM
    Author     : lp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.um.model.EHealthRecord,java.util.List,java.util.ArrayList,com.um.model.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <style type="text/css">
        body { 
	margin:0; 
	padding:20px; 
	font:13px "Lucida Grande", "Lucida Sans Unicode", Helvetica, Arial, sans-serif;
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
	
	/*
	I know this is annoying, but we need additional styling so webkit will recognize rounded corners on background elements.
	Nice write up of this issue: http://www.onenaught.com/posts/266/css-inner-elements-breaking-border-radius
	
	And, since we've applied the background colors to td/th element because of IE, Gecko browsers also need it.
	*/
	
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
        <title>JSP Page</title>
<!--        <link href="css/bootstrap.min.css" rel="stylesheet" >
        <script src="//code.jquery.com/jquery-1.11.2.min.js"></script>
        <script src="js/jquery-2.1.3.min.js"></script>
        <script src="js/bootstrap.min.js"></script>-->
    </head>
    <body>
       
       <h2>病历信息</h2>
       <form action="saveEditRecord" method="post">
       <table border="1">
       	<tr>
       		<th><label>医院</label></th>
       		<td>${ehealthrecordss.getHospital() }</td>
       	</tr>
       	<tr>
       		<th><label>时间</label></th>
       		<td>${ehealthrecordss.getDate() }</td>
       	</tr>
       	<tr>
       		<th><label>科别</label></th>
       		<td>${ehealthrecordss.getMedicalservice() } </td>
       	</tr>
       	<tr>
       		<th><label>挂号号</label></th>
       		<td> <input type="text" name="regno" value="${ehealthrecordss.getRegistrationno() }" />
       		</td>
       	</tr>
       	<tr>
       		<th><label>姓名</label></th>
       		<td>${ehealthrecordss.getPatientInfo().getName() }</td>
       	</tr>
       	<tr>
       		<th><label>性别</label></th>
       		<td>${ehealthrecordss.getPatientInfo().getGender() }</td>
       	</tr>
       	<tr>
       		<th><label>年龄</label></th>
       		<td>${ehealthrecordss.getPatientInfo().getAge() }</td>
       	</tr>
       	<tr>
       		<th><label>职业</label></th>
       		<td>${ehealthrecordss.getPatientInfo().getProfession() }</td>
       	</tr>
       	<tr>
       		<th><label>电话</label></th>
       		<td>${ehealthrecordss.getPatientInfo().getPhoneNumber() }</td>
       	</tr>
       	<tr>
       		<th><label>联系人</label></th>
       		<td>${ehealthrecordss.getPatientInfo().getContact() }</td>
       	</tr>
       	<tr>
       		<th><label>地址</label></th>
       		<td>${ehealthrecordss.getPatientInfo().getAddress() }</td>
       	</tr>
       	<tr>
       		<th><label>病症描述</label></th>
       		<td>
       			<textarea rows="10" cols="150" name="conditondesc">
       				${ehealthrecordss.getConditionsdescribed() }
       			</textarea>
       		 </td>
       	</tr>
       	<tr>
       		<th><label>西医诊断</label></th>
       		<td>
       			<textarea rows="5" cols="150" name="westerndiag">
       				${ehealthrecordss.getWesterndiagnostics() }
       			</textarea>
       		 </td>
       	</tr>
       	<tr>
       		<th><label>中医诊断</label></th>
       		<td>
       			<textarea rows="5" cols="150" name="chinesediag">
       				${ehealthrecordss.getChinesediagnostics() }
       			</textarea>
       		 </td>
       	</tr>
       	<tr>
       		<th><label>处理</label></th>
       		<td>
       			<textarea rows="5" cols="150">
       				${ehealthrecordss.getProcessString() }
       			</textarea>
       		 </td>
       	</tr>
       	<tr>
       		<th><label>西药处方</label></th>
       		<td>
       			<table>
       				<tr>
       					<th></th>
       					<th>组</th>
       					<th>名称</th>
       					<th>规格</th>
       					<th>用法</th>
       				</tr>
       				<c:forEach var="item" items="${allWeMedicines }" varStatus="status">
       						<tr>
       							<!-- group -->
       							<td>${status.index + 1 }</td>
       							<td>${item.getGroupString() }</td>
       							<td>${item.getNameString() }</td>
       							<td>${item.getSpecifications() }</td>
       							<td>${item.getUsageString() }</td>
       						</tr>
       				</c:forEach>
       			</table>
			</td>
       	</tr>
       	<tr>
       		<th><label>中药处方</label></th>
       		<!-- 中药处方 -->
       		<td>
       			<table >
       				<tr>
       					<th></th>
       					<th>名称</th>
       					<th>用量</th>
       					<th>单位</th>
       				</tr>
       				<c:forEach var="item" items="${allCnMedicines }" varStatus="status">
       						<tr>
       							<!-- group -->
       							<td>${status.index + 1 }</td>
       							<td>${item.getNameString() }</td>
       							<td>${item.getNumberString() }</td>
       							<td>${item.getUnitString() }</td>
       						</tr>
       				</c:forEach>
       			</table>
			</td>
       	</tr>
       	<tr>
       		<th>医师</th>
       		<td>${ehealthrecordss.getDoctor() }</td>
       	</tr>
       </table>
       
       <input type="submit" value="保存" />
       </form>
    </body>
</html>
