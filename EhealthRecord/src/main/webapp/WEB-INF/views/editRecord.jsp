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
   
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
 		<link rel="stylesheet" href="css/bootstrap.min.css">
    </head>
    <body>
       <script src="js/bootstrap.min.js"></script>
	<script src="js/jquery-2.1.4.min.js"></script>
		<div class="container">
       <h2>病历信息</h2>
       <form action="saveEditRecord" method="post">
       <table class="table" border="1px">
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
       </div>
    </body>
</html>
