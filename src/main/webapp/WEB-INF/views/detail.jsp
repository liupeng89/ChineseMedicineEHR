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
        <title>detail</title>
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<script src="js/bootstrap.min.js"></script>
		<script src="js/jquery-2.1.4.min.js"></script>
    </head>
    <body>
       
		<div class="container">
	       <h2>病历信息</h2>
	       <div>
	       		<a href="javascript:history.back()">Go Back</a>
	       </div>
	       <table class="table table-bordered">
	       	<tr class="info">
	       		<th rowspan="8"><label>个人信息</label></th>
	       		<th><label>医院</label></th>
	       		<th><label>时间</label></th>
	       		<th><label>科别</label></th>
	       	</tr>
	       	<tr>
	       		<td>${ehealthrecordss.getHospital() }</td>
	       		<td>${ehealthrecordss.getDate() }</td>
	       		<td>${ehealthrecordss.getMedicalservice() } </td>
	       	</tr>
	       	
	       	<tr class="info">
	       		<th><label>挂号号</label></th>
	       		<th><label>姓名</label></th>
	       		<th><label>性别</label></th>
	       	</tr>
	       	<tr>
	       		<td>${ehealthrecordss.getRegistrationno() }</td>
	       		<td>${ehealthrecordss.getPatientInfo().getName() }</td>
	       		<td>${ehealthrecordss.getPatientInfo().getGender() }</td>
	       	</tr>
	       	
	       	<tr class="info">
	       		<th><label>年龄</label></th>
	       		<th><label>职业</label></th>
	       		<th><label>电话</label></th>
	       	</tr>
	       	<tr>
	       		<td>${ehealthrecordss.getPatientInfo().getAge() }</td>
	       		<td>${ehealthrecordss.getPatientInfo().getProfession() }</td>
	       		<td>${ehealthrecordss.getPatientInfo().getPhoneNumber() }</td>
	       	</tr>
	       	
	       	<tr class="info">
	       		<th><label>联系人</label></th>
	       		<th><label>地址</label></th>
	       		<th><label></label></th>
	       	</tr>
	       	<tr>
	       		<td>${ehealthrecordss.getPatientInfo().getContact() }</td>
	       		<td>${ehealthrecordss.getPatientInfo().getAddress() }</td>
	       		<td></td>
	       	</tr>
	       	<tr>
	       		<th class="info"><label>病症描述</label></th>
	       		<td colspan="3">${ehealthrecordss.getConditionsdescribed() }</td>
	       	</tr>
	       	
	       	<%-- <tr>
	       		<th class="info"><label>病症描述分析</label></th>
	       		<td colspan="3">
	       			<table>
	       				<c:forEach var="item" items="${conditions }">
	       					<c:if test="${item.value.size() != 0 }">
	       						<tr>
		       						<td>${item.key }</td>
		       						<td><table>
		       						<c:forEach var="k" items="${item.value }">
		       							<tr>
		       								<td>${k.value }</td>
		       							</tr>
		       						</c:forEach>
		       						</table>
		       						</td>
	       						</tr>
	       					</c:if>
	       				</c:forEach>
	       			</table>
				</td>
	       	</tr> --%>
	       	<tr>
	       		<th class="info"><label>西医诊断</label></th>
	       		<td colspan="3">${ehealthrecordss.getWesterndiagnostics() }</td>
	       	</tr>
	       	<tr>
	       		<th class="info"><label>中医诊断</label></th>
	       		<td colspan="3">${ehealthrecordss.getChinesediagnostics() }</td>
	       	</tr>
	       	<tr>
	       		<th class="info"><label>处理</label></th>
	       		<td colspan="3">${ehealthrecordss.getProcessString() }</td>
	       	</tr>
	       	<tr>
	       		<th class="info"><label>西药处方</label></th>
	       		<td colspan="3">
	       			<table>
	       				<tr>
	       					<th></th>
	       					<!-- <th>组</th> -->
	       					<th>名称</th>
	       					<!-- <th>规格</th>
	       					<th>用法</th> -->
	       				</tr>
	       				<c:forEach var="item" items="${allWeMedicines }" varStatus="status">
	       						<tr>
	       							<!-- group -->
	       							<td>${status.index + 1 },  </td>
	       							<%-- <td>${item.getGroupString() }</td> --%>
	       							<td>${item.getNameString() }</td>
	       							<%-- <td>${item.getSpecifications() }</td>
	       							<td>${item.getUsageString() }</td> --%>
	       						</tr>
	       				</c:forEach>
	       			</table>
				</td>
	       	</tr>
	       	<tr>
	       		<th class="info"><label>中药处方</label></th>
	       		<!-- 中药处方 -->
	       		<td colspan="3">
	       			<table >
	       				<tr>
	       					<th></th>
	       					<th>名称</th>
	<!--        					<th>用量</th>
	       					<th>单位</th>
	 -->       				</tr>
	       				<c:forEach var="item" items="${allCnMedicines }" varStatus="status" >
	       						<tr>
	       							<!-- group -->
	       							<td>${status.index + 1 },  </td>
	       							<td>${item.getNameString() }</td>
	       							<%-- <td>${item.getNumberString() }</td>
	       							<td>${item.getUnitString() }</td> --%>
	       						</tr>
	       				</c:forEach>
	       			</table>
				</td>
	       	</tr>
	       	<tr>
	       		<th class="info">医师</th>
	       		<td colspan="3">${ehealthrecordss.getDoctor() }</td>
	       	</tr>
	       </table>
	       <br>
	       <h3><a href="editRecord?ehealthregno=${ehealthrecordss.getRegistrationno() }">编辑病例</a></h3>
       </div>
    </body>
</html>
