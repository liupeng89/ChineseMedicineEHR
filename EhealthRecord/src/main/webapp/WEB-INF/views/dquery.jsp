<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style type="text/css">
		body {
			margin: 0;
			padding: 0;
			line-height: 1.7em;
			font-family: Verdana, Geneva, sans-serif;
			font-size: 12px;
			color: #707b84;
			background: #ffffff;
		}
		div {
			text-align: center; 
		}
		
		.query_card h1 {
		  font-weight: 100;
		  text-align: center;
		  font-size: 2.3em;
		}
		.query-card {
		  padding: 80px;
		  width: 274px;
		  background-color: #F7F7F7;
		  margin: 10% auto 10px;
		  border-radius: 0px;
		  box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
		  overflow: hidden;
		}
		.login-card input[type=submit] {
		  width: 100%;
		  display: block;
		  margin-bottom: 10px;
		  position: relative;
		}
		
		.login-card input[type=text], input[type=password] {
		  height: 44px;
		  font-size: 16px;
		  width: 100px;
		  margin-bottom: 10px;
		  -webkit-appearance: none;
		  background: #fff;
		  border: 1px solid #d9d9d9;
		  border-top: 1px solid #c0c0c0;
		  /* border-radius: 2px; */
		  padding: 0 8px;
		  box-sizing: border-box;
		  -moz-box-sizing: border-box;
		}
		
		.login-card input[type=text]:hover, input[type=password]:hover {
		  border: 1px solid #b9b9b9;
		  border-top: 1px solid #a0a0a0;
		  -moz-box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
		  -webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
		  box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
		}
		
	</style>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Insert title here</title>
		 <link rel="stylesheet" href="css/style.css">
	</head>
	<body>
		<div class="query-card">
			<div>
	    		<h1>病历查询</h1>
	    	</div>
	    	<br>
	    	<br>
	    	<div>
	    		<form action="recordquery" method="get">
	    			<p>
	    				批次：
	    				<select name="batch">  
        					<c:forEach items="${batchList }" var="item">  
            					<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item  }</option>  
        					</c:forEach>  
    					</select>  
	    			</p>
	    			<div>
	    				<label>姓名：</label><input type="text" name="pname" />
	    			</div>
	    			<br>
	    			<div>
	    				 <button type="submit">查询</button>
	    			</div>
	        	</form>
	    	</div>
	    </div>
	</body>
</html>