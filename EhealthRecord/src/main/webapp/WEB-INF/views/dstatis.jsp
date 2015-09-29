<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<style type="text/css">
		body {
			
		}
    	a { 
            font:15px "Lucida Grande", "Lucida Sans Unicode", Verdana, Geneva, sans-serif;        
			display: block;
			width: 100%;
			height: 22px;
			color: #222222;
		}
		.col-lg-12{
			position:relative;
			overflow:auto; 
			min-height:1px;
			padding-right:0px;
			padding-left:0px;
			
		}
		#cnmedicine {
			
			position: relative;
			left:150px;
			top: 100px;
			background-color:#f5f5f5;
			text-align:center;
			vertical-align:middle;
			height:200px;
			width:20%;
			border-radius: 0px;
		  	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
			
		}
		#cwdiagstatis {
			position: relative;
			left:500px;
			top:-100px;
			background-color:#cccccc;
			text-align:center;
			height:200px;
			width:20%;
			border-radius: 0px;
		  	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
		}
		#cdmedicinestatis {
			position: relative;
			left:850px;
			top:-300px;
			background-color:#c4c4c4;
			text-align:center;
			height:200px;
			width:20%;
    		border-radius: 0px;
		  	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);			
		}
		.query-card {
		  padding: 40px;
		  width: 274px;
		  background-color: #F7F7F7;
		  margin: 0 auto 10px;
		  border-radius: 0px;
		  box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
		  overflow: hidden;
		}
    </style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div class="col-lg-12">
        	<div id="cnmedicine">
        		<a href="cnmedicinestatis">统计中药处方</a>
        	</div>
        	<div id="cwdiagstatis">
        		<a href="cwdiagstatis">中西医诊断统计分类</a>
        	</div>
        	<div id=cdmedicinestatis>
        		<a href="CDMedicineStatis">中医诊断处方统计</a>
        	</div>
        	
        </div>
</body>
</html>