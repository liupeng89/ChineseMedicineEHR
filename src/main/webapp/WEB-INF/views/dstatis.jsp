<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	 <link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/style.css">
	<title>Insert title here</title>
	<style>
		#div1 {
			border:1px solid;
			position: relative;
			top:100px;
			width: 300px;
			height:300px;
			text-align:center;
		}
		#div2 {
			border:1px solid;
			position: relative;
			top:-200px;
			left: 330px;
			width: 300px;
			height:300px;
			text-align:center;
		}
		#div3 {
			border:1px solid;
			position: relative;
			top:-200px;
			left:400px;
			width: 300px;
			height:300px;
			text-align:center;
		}
	</style>
</head>
<body>
	<div class="container">
		<div class="row">
			<div id="div1" class="span4">
				<h2>统计中药处方</h2>
				<br>
				<p class="text-danger">统计该年代病例中出现的全部的中药处方的数量和百分比。</p>
				<br>
				<form action="cnmedicinestatis" method="get">
        			<p>
	    				年度：
	    				<select name="batch">  
        					<c:forEach items="${batchList }" var="item">  
            					<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item }</option>  
        					</c:forEach>  
    					</select> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    			</p>
        			<input type="submit" class="btn btn-success btn-sm" value="统计中药处方" />
        		</form>
			</div>
			<div id="div2" class="col-lg-1">
				<h2>中西医诊断统计分类</h2>
				<br>
				<p class="text-danger">分别基于中西医诊断统计病例的数量和百分比。</p>
				<br>
				<form action="cwdiagstatis" method="get">
	        		<p>
	    				年度：
	    				<select name="batch">  
        					<c:forEach items="${batchList }" var="item">  
            					<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item }</option>  
        					</c:forEach>  
    					</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		    		</p>
		    		<input type="submit" class="btn btn-success btn-sm" value="中西医诊断统计分类" /> 
        		</form>
			</div>
			<div id="div3" class="col-lg-1">
				<h2>中医诊断处方统计</h2>
				<br>
				<p class="text-danger">基于中医诊断统计中药处方。</p>
				<br>
				<form action="CDMedicineStatis" method="get">
	        		<p>
		    				年度：
		    				<select name="batch">  
	        					<c:forEach items="${batchList }" var="item">  
	            					<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item }</option>  
	        					</c:forEach>  
	    					</select> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		    		</p>
		    		<input type="submit" class="btn btn-success btn-sm" value="中医诊断处方统计" />
        		</form>
			</div>
		</div>
	</div>
</body>
</html>