<%@page import="java.text.DecimalFormat"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map,java.util.HashMap"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="css/bootstrap.min.css">
</head>
<body>
	<script src="js/bootstrap.min.js"></script>
		<script src="js/jquery-2.1.4.min.js"></script>
	<% Map<String, HashMap<String, Integer>> classmaps = (Map<String, HashMap<String, Integer>>)request.getAttribute("cnClassifyStatistics");
		int ehealthCount = (Integer)request.getAttribute("ehealthCount");
		DecimalFormat df = new DecimalFormat("0.##");
		
	%>
	
	<h1>中医诊断中药处方统计</h1>
	<a href="javascript:history.back()">Go Back</a>
	<table class="table" border="1">
		<tr class="something">
			<th style="width:200px">编码</th>
			<th>中医诊断处方统计</th>
		</tr>
		
		<%
			if(classmaps != null && classmaps.size() > 0){
				Set<String> keys = classmaps.keySet();
				
				for(String s : keys ){
					if(classmaps.get(s) != null){
						HashMap<String,Integer> hmap = (HashMap<String,Integer>)classmaps.get(s);
						%>
							<tr class="something">
								<td class="span6"><%=s %></td>
								<td><table class="table table-striped " border="0px">
										<thead>
											<tr class="info">
												<th>中医名称</th>
												<th>出现数量</th>
												<th>百分比</th>
											</tr>
										</thead>
										<tbody>
								<%
									String cmedicines = "";
									Set<String> cnKeys = hmap.keySet();
									if(cnKeys != null && cnKeys.size() > 0){
										int index = 0;
										for(String cns : cnKeys){
											%>
												<tr>
													<td><%=cns %></td>
													<td><%=hmap.get(cns) %></td>
													<td><%=df.format((double)hmap.get(cns) * 100 / ehealthCount) +"%" %></td>
												</tr>
											<%
										}
									}
								%>
								</tbody>
								</table></td>
							</tr>
						<%
					}
				}
			}		
		%>
	</table>
</body>
</html>