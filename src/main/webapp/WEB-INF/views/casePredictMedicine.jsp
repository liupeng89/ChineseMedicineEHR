<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Map,java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/style.css">
	<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<style type="text/css">
		#left {
		
			float:left;
			width:40%;
		}
		
		#right {
		
			float:right;
			width:60%;
		}
	</style>
	<title>case predict medicines</title>
</head>
<body>
	
	<div id="left">
		<div>
			<div class="row">
	        	<div class="col-lg-12">
	            <h1 class="page-header">
	            基于案例预测处方
	            </h1>
	            </div>
	        </div>
			<form name="form" method="post">

				<div id="left_rigth">
					<p class="text-danger">
						<label>请入病例序号(1-1130) 或挂号号</label>
					</p>
					<p>
						<input type="text" name="count" />
					</p>
					<p class="text-danger">
						机器学习阈值(0~1):<br>
						<input type="text" name="threshold" value="0.5" />
					</p>
					<p>
						<input type="submit" class="btn btn-success btn-xs" value="根据病例预测处方" onclick="javascript: form.action='predicetByCase';" />
					</p>
				</div>
			</form>
		</div>
	</div>
	<div id="right">
		<div>
		<!-- 输入数据 -->
		<div style="width:100%">
			<h3>病例序号：</h3>
			<p>${count }</p>
			<hr>
			<h3>挂号号：</h3>
			<p><a href="detailRecord?ehealthregno=${regno }">${regno }</a></p>
			<hr>
			<h3>诊断：</h3>
			<p>${diagnose }</p>
			<hr>
			<h3>描述：</h3>
			<p>${description }</p>
			<hr>
		</div>
		<!-- 案例统计预测结果 -->
		<c:if test="${medicineListByStatis != null }">
			<div style="width:100%">
				<h3>基于病例统计的预测处方：(共 ${medicineListByStatis.size() } 味 ,准确率：<fmt:formatNumber type="number" maxIntegerDigits="3" value="${statisticsPercent * 100}"/> %)</h3>
				<p>
					<c:forEach items="${medicineListByStatis }" var="item">
						<%-- ${item } --%>
						<c:if test="${orignMedicines.contains(item) }">
							<font color="black">
								${item }，
							</font>
						</c:if>
						<c:if test="${!orignMedicines.contains(item) }">
							<font color="red">
								${item }，
							</font>
						</c:if>
					</c:forEach>
				</p>
				<p>
					${predictMedicine }
				</p>
			</div>
		</c:if>
		<!-- 机器学习预测结果 -->
		<c:if test="${medicineListByMachine != null }">
			<div style="width:100%">
				<hr>
				<h3>基于机器学习的预测处方：(共 ${medicineListByMachine.size() } 味,准确率：<fmt:formatNumber type="number" maxIntegerDigits="2" value="${mechineLearningPercent * 100 }"/> %)</h3>
				<p>
					<c:forEach items="${medicineListByMachine }" var="item">
						<%-- ${item } --%>
						<c:if test="${orignMedicines.contains(item) }">
							<font color="black">
								${item }，
							</font>
						</c:if>
						<c:if test="${!orignMedicines.contains(item) }">
							<font color="red">
								${item }，
							</font>
						</c:if>
					</c:forEach>
				</p>
			</div>
		</c:if>
		<!-- 预测结果 -->
		
		<c:if test="${orignMedicines != null }">
			<div style="width:100%">
				<hr>
				<h3>原始病例处方：(共 ${orignMedicines.size() } 味)</h3>
				<p>
					<c:forEach items="${orignMedicines }" var="item">
						${item }，
					</c:forEach>
				</p>
			</div>
		</c:if>
	</div>
	</div>

</body>
</html>