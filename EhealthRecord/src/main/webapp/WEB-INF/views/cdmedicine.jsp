<%@page import="java.text.DecimalFormat"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map,java.util.HashMap"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style>
	body { 
	margin:0; 
	padding:20px; 
	font:13px "Lucida Grande", "Lucida Sans Unicode", Helvetica, Arial, sans-serif;
	border: 1px solid #aaa;
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
<title>Insert title here</title>
</head>
<body>
	<% Map<String, HashMap<String, Integer>> classmaps = (Map<String, HashMap<String, Integer>>)request.getAttribute("cnClassifyStatistics");
		int ehealthCount = (Integer)request.getAttribute("ehealthCount");
		DecimalFormat df = new DecimalFormat("0.##");
		
	%>
	
	<h1>中医诊断中药处方统计</h1>
	<table border="1">
		<tr>
			<th>编码</th>
			<th>中医诊断处方统计</th>
		</tr>
		<%
			if(classmaps != null && classmaps.size() > 0){
				Set<String> keys = classmaps.keySet();
				
				for(String s : keys ){
					if(classmaps.get(s) != null){
						HashMap<String,Integer> hmap = (HashMap<String,Integer>)classmaps.get(s);
						%>
							<tr>
								<td><%=s %></td>
								<td><table>
								<%
									String cmedicines = "";
									Set<String> cnKeys = hmap.keySet();
									if(cnKeys != null && cnKeys.size() > 0){
										int index = 0;
										for(String cns : cnKeys){
											cmedicines = cns + ": " + hmap.get(cns) + "味(" + (df.format((double)hmap.get(cns) * 100 / ehealthCount) )+"%)";
											
											if(index == 0){
												%>
												<tr><td><%=cmedicines %></td>
												<%
											}else if(index % 5 == 0 && index != 0){
												%>
												</tr><tr><td><%=cmedicines %></td>
												<%
											}else{
												%>
												<td><%=cmedicines %></td>
												<%
											}
											
											index++;
										}
									}
								%>
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