<%@page import="java.text.DecimalFormat"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map,java.util.HashMap"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>statistics by chinese </title>
<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/style.css">
</head>
	<body>
		<div class="container">
			<div>
				<div class="row">
	                    <div class="col-lg-12">
	                        <h1 class="page-header">
	                            中医诊断统计
	                        </h1>
	                    </div>
	                </div>
					<form action="CDMedicineStatis" method="get">
			    		年度：
			    		<select name="batch">
			    			<option value="null">全部</option>
                        	<option value="2012" selected>2012</option>
                        	<option value="2011">2011</option>
                        	<option value="2010">2010</option>
                        	<option value="2009">2009</option>  
		    			</select>  
			    		<input type="submit" class="btn btn-success btn-xs" value="中医诊断处方统计" />
	        		</form>
			</div>
			<% Map<String, HashMap<String, Integer>> classmaps = (Map<String, HashMap<String, Integer>>)request.getAttribute("cnClassifyStatistics");
				int ehealthCount = (Integer)request.getAttribute("ehealthCount");
				DecimalFormat df = new DecimalFormat("0.##");
				Map<String, Integer> cnClassifyNumber = (Map<String, Integer>)request.getAttribute("cnClassifyNumber");
			%>
			<p></p>
			<div class="col-lg-12">
				<table class="table" border="1">
					<tr class="info">
						<th style="width:200px">中医诊断类型</th>
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
											<td class="span6"><%=s %>
												<font color="red"><p>数量: <%=cnClassifyNumber.get(s) %></p></font>
											</td>
											<td><table class="table table-striped " border="0px">
													<thead>
														<tr class="warning">
															<th>中药名称</th>
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
														if(hmap.get(cns) * 100 / (Integer)cnClassifyNumber.get(s) > 5){
														%>
															<tr>
																<td><%=cns %></td>
																<td><%=hmap.get(cns) %></td>
																<td><%=df.format((double)hmap.get(cns) * 100 / (Integer)cnClassifyNumber.get(s)) +"%" %></td>
															</tr>
														<%
														}
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
			</div>
		</div>
	</body>
</html>