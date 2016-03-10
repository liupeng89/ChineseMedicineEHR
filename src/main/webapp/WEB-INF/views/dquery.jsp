<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Insert title here</title>
		<link rel="stylesheet" href="css/bootstrap.min.css">
	 	<link rel="stylesheet" href="css/style-query.css">
	 	<link rel="stylesheet" href="css/jquery-ui.css">
	 	
	 	<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
		<script type="text/javascript" src="js/jquery-ui.js"></script>
		<script type="text/javascript" src="js/query.js"></script>
		<script type="text/javascript">
			
		/* function queryRecord(){
				var url = 'recordquery?batch='+ $("#recordbatch").val() + '&pname='+$("#tags").val();
				$.get(url, function(data){
					$('#contents').html(data);
				});
			} */
		</script>
	</head>
	<body class="container">
		<div class="col-sm-12">
			<div>
	    		<h1>病历查询</h1>
	    	</div>
	    	<div>
	    		<form action="recordquery" method="get">
	    			年度：
	    			<select id="recordbatch" name="batch">  
        				<c:forEach items="${batchList }" var="item">  
            				<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item  }</option>  
        				</c:forEach>  
    				</select>  
					<label for="tags">姓名： </label>
					<input id="tags" type="text"  name="pname" />
	    			<button id="query" type="submit" class="btn btn-success btn-sm" >查询</button>
	        	</form>
	    	</div>
	    	
		</div>
		<div id="contents">
		</div>
	</body>
</html>