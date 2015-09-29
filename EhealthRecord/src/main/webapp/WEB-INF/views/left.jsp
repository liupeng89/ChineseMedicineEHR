<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style type="text/css">
 body{
            background-color: #fff;
            /*color: whitesmoke;*/
        }
.left-block ul li {
  font-size:0.8em;
	list-style-type:none;
	background:#fff bottom repeat-x;
	position:relative;
	border: 1px solid #aaa;
	text-align:center;
}
 
.left-block ul li:hover,
.left-block ul li.active {
	background:#00739B  bottom repeat-x;
	font-weight: bold;
	color: #FFF;
}
 
.left-block ul li a:link {
  color:#333;
	text-decoration:none;
	display:block;
	padding:8px 0 10px 0px;
}
 
.left-block ul li a:visited {
  color:#333;
	text-decoration:none;
	display:block;
	padding:8px 10px 0px;
	background:  bottom repeat-x;
}
 
.left-block ul li a:hover {
  color: #FFF;
	text-decoration:none;
	display:block;
	padding:8px 0 10px 0px;
}
 
.left-block ul li a img {
  position:absolute;
	top:8px;
	left:8px;
}
    </style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div class="left-block">
		<h3><div class="left-block-header-inner">功能</div></h3>
		<ul>
			<li>
				<a href="mainquery" target="showme">病历查询</a>
			</li>
			<br>
			<li>
				<a href="mainstatis" target="showme">数据统计</a>
			</li>
			<br>
			<li>
				<a href="maindiagmedic" target="showme">输入预测处方</a>
			</li>
			<br>
			<li>
				<a href="casediagmedic" target="showme">案例预测处方</a>
			</li>
			<br>
			<li>
				<a href="maincnmedicine" target="showme">中药关系统计</a>
			</li>
		</ul>
	</div>
</body>
</html>