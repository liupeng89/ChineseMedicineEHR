<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Map,java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
    	
	
	body {
		margin:0;
		padding:0;
		font:12px/15px "Helvetica Neue",Arial, Helvetica, sans-serif;
		color: #555;
		background:#f5f5f5;
		border: 1px solid #aaa;
		height:1200px;
		width:100%;
	}
	h3 {
		
		color:#4169E1 ;
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
		margin:0% auto 0;
		-moz-border-radius:0px; /* FF1+ */
		-webkit-border-radius:0px; /* Saf3-4 */
		border-radius:0px;
		-moz-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
		-webkit-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
	}
	
	th, td {padding:8px 18px 8px; text-align:left; }
	
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
	
	#input {
	
		float:left;
		width:35%;
		height:800px;
	}
	
	#zhenxing {
		float:left;
		width:100%;
	}
	#description {
		float:left;
		width:100%;
	}
	
	#table1{
		
		width:100%;
		border:1px;
	}
	#table2 {
	
		width:100%;
		border:1px;
	}
	
	#output {
	
		float:right;
		width:65%;
	}
	#similayrecords {
		float:left;
		width:100%;
	}
	#similarytable {
		float:left;
		width:100%;
	}
	#left {
		float:left;
		width:55%;
	}
	#right {
		float:right;
		width:45%;
	}
	#left_left {
		float:left;
		width:60%;
	}
	#left_right {
		float:right;
		width:40%;
	}
    </style>
</head>
<body>
	<div id="left">
		<div>
			<h1>处方预测</h1>
			<form name="form" method="post">
				<div id="left_left">
					<!-- 症型 -->
					<div>
						<p>
			    			批次：
			    			<select name="batch">  
		        				<c:forEach items="${batchList }" var="item">  
		            				<option value="${item }" <c:if test="${item == '2012'}">selected</c:if>>${item == 'null'? '全部' : item  }</option>  
		        				</c:forEach>  
		    				</select> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		    				<input type="submit" value="预测处方" onclick="javascript: form.action='predictByStatisticAndMachine';" /> 
			    		</p>
			    		<hr>
						<p>证型</p>
						<table id="table1">
							<tr>
								<td><label>虚:</label></td>
								<td>
									<select name="xu">
										<option value="气虚">气虚</option>
										<option value="脾虚">脾虚</option>
										<option value="气阴两虚">气阴两虚</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>痰瘀</label></td>
								<td>
									<input type="radio" name="tanyu" value="yes">有
									<input type="radio" name="tanyu" value="no" checked>无
								</td>
							</tr>
							<tr>
								<td><label>痰湿</label></td>
								<td>
									<input type="radio" name="tanshi" value="yes">有
									<input type="radio" name="tanshi" value="no" checked>无
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<label><input name="zhengxing" type="checkbox" value="互结" checked/>互结</label>
									<label><input name="zhengxing" type="checkbox" value="阻络"/>阻络</label>
									<label><input name="zhengxing" type="checkbox" value="热结"/>热结</label>
									<label><input name="zhengxing" type="checkbox" value="夹热"/>夹热</label>
									<label><input name="zhengxing" type="checkbox" value="瘀热"/>瘀热</label>
									<label><input name="zhengxing" type="checkbox" value="湿阻"/>湿阻</label>
								</td>
							</tr>
						</table>
						<br>
						<br>
					</div>
					<!-- 症状 -->
					<div id="">
						<hr>
						<p>症状</p>
						<table id="table2">
							<tr>
								<td><label>寒热：</label></td>
								<td>
									<select name="hanre">
										<option value="hanwu" selected>寒（无）</option>
										<option value="hanqing">寒（轻）</option>
										<option value="hanzhong">寒（重）</option>
										<option value="rewu">热（无）</option>
										<option value="reqing">热（轻）</option>
										<option value="rezhong">热（重）</option>
										<option value="hanre">寒热往来</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>汗：</label></td>
								<td>
									<select name="sweat">
										<option value="sweat">有</option>
										<option value="nosweat" selected>无</option>
										<option value="zihan">自汗</option>
										<option value="daohan">盗汗</option>
										<option value="dahan">大汗</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>痛：</label></td>
								<td>
									<select name="xonglei">
										<option value="noxonglei" selected>胸肋痛（无）</option>
										<option value="okxonglei">胸肋痛（轻）</option>
										<option value="badxonglei">胸肋痛（中）</option>
										<option value="worsexonglei">胸肋痛（重）</option>
									</select>
									<select name="futong">
										<option value="nofutong" selected>腹痛（无）</option>
										<option value="okfutong">腹痛（轻）</option>
										<option value="badfutong">腹痛（中）</option>
										<option value="worsefutong">腹痛（重）</option>
									</select>
									<br>
									<label><input name="tengtong" type="checkbox" value="yaotong"/>腰痛</label>
									<label><input name="tengtong" type="checkbox" value="wantong"/>脘痛</label>
									<label><input name="tengtong" type="checkbox" value="toutong"/>头痛</label>
									<br>
									<label>头身胸腹不适：</label>
									<label><input name="bodydiscomfort" type="checkbox" value="touyun"/>头晕</label>
									<label><input name="bodydiscomfort" type="checkbox" value="xinji"/>心悸</label>
									<label><input name="bodydiscomfort" type="checkbox" value="xiongmen"/>胸闷</label>
									<label><input name="bodydiscomfort" type="checkbox" value="fuzhang"/>腹胀</label>
									<label><input name="bodydiscomfort" type="checkbox" value="wanzhang"/>脘胀</label>
									<br>
									<label><input name="bodydiscomfort" type="checkbox" value="shenzhong"/>身重</label>
									<label><input name="bodydiscomfort" type="checkbox" value="erming"/>耳鸣</label>
									<label><input name="bodydiscomfort" type="checkbox" value="muxuan"/>目眩</label>
									<label><input name="bodydiscomfort" type="checkbox" value="mamu"/>麻木</label>
								</td>
							</tr>
							<tr>
								<td><label>大便：</label></td>
								<td>
									<select name="defecate" >
										<option value="okdefecate" selected>大便（轻）</option>
										<option value="baddefecate">大便（中）</option>
										<option value="worsedefecate">大便（重）</option>					
									</select>
									<label><input name="constipation" type="checkbox" value="bianmi"/>便秘</label>
								</td>
							</tr>
							<tr>
								<td><label>小便：</label></td>
								<td>
									<select name="urinate" >
										<option value="okurinate" selected>小便尚可</option>
										<option value="badurinate">小便次多</option>
										<option value="worseurinate">尿频</option>
										<option value="bloodurinate">尿血</option>					
									</select>
								</td>
							</tr>
							<tr>
								<td><label>舌：</label></td>
								<td>
									<label>舌色：</label>
									<select name="tonguecolor">
										<option value="oktonguecolor" selected>淡红正常</option>
										<option value="whitetonguecolor">淡白</option>
										<option value="redtonguecolor">红色</option>
										<option value="jiangtonguecolor">降色</option>
										<option value="purpletonguecolor">紫色</option>
										<option value="cyantonguecolor">青色</option>
										<option value="bluetonguecolor">蓝色</option>
									</select>
									<label>舌苔：</label>
									<select name="coatedtongue">
										<option value="whitecoatedtongue" selected>白苔</option>
										<option value="yellowcoatedtongue">黄苔</option>
										<option value="purplecoatedtongue">紫苔</option>
										<option value="blackcoatedtongue">黑（灰）苔</option>
										<option value="nitaicoatedtongue">腻苔</option>
										<option value="houtaicoatedtongue">厚苔</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>痰量：</label></td>
								<td>
									<select name="sputumamount">
										<option value="oksputumamount" selected>正常</option>
										<option value="littlesputumamount">少</option>
										<option value="muchsputumamount">多</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>痰色：</label></td>
								<td>
									<select name="sputumcolor">
										<option value="yellowsputumcolor">黄</option>
										<option value="whitesputumcolor">白</option>
										<option value="redlittlesputumcolor">红血痰（少）</option>
										<option value="redmuchsputumcolor">红血痰（多）</option>
										<option value="redmoresputumcolor">红血痰（特多）</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>眠：</label></td>
								<td>
									<select name="sleep">
										<option value="oksleep" selected>正常</option>
										<option value="badsleep">失眠（轻）</option>
										<option value="worsesleep">失眠（中）</option>
										<option value="worstsleep">失眠（重）</option>
										<option value="somnolencesleep">嗜睡</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>纳：</label></td>
								<td>
								<select name="na">
									<option value="okna" selected>正常</option>
									<option value="badna">纳差</option>
									<option value="anorexiana">厌食</option>
									<option value="worsena">食欲减退</option>
								</select>
								</td>
							</tr>
							<tr>
								<td><label>气力：</label></td>
								<td>
									<select name="energy">
										<option value="okenergy" selected>正常</option>
										<option value="badenergy">差</option>
										<option value="worseenergy">特差</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>脉：</label></td>
								<td>
									<label><input name="pulse" type="checkbox" value="floatpulse" checked/>浮</label>
									<label><input name="pulse" type="checkbox" value="sinkpulse"/>沉</label>
									<label><input name="pulse" type="checkbox" value="slimpulse"/>细</label>
									<label><input name="pulse" type="checkbox" value="roughpulse"/>粗</label>
									<label><input name="pulse" type="checkbox" value="latepulse"/>迟</label>
									<label><input name="pulse" type="checkbox" value="numberpulse"/>数</label>
									<label><input name="pulse" type="checkbox" value="chordpulse"/>弦</label>
									<label><input name="pulse" type="checkbox" value="slidepulse"/>滑</label>
								</td>
							</tr>
							<tr>
								<td><label>口渴：</label></td>
								<td>
									<select name="thirst">
										<option value="okthirst" selected>口不渴</option>
										<option value="badthirst">喝不多饮</option>
										<option value="worsethirst">口渴多饮</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>口味：</label></td>
								<td>
									<select name="taste">
										<option value="lighttaste">口淡</option>
										<option value="acidtaste">泛酸</option>
										<option value="bittertaste">口苦</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label>咳嗽：</label></td>
								<td>
									<select name="cough">
										<option value="okcough" selected>正常</option>
										<option value="badcough">轻</option>
										<option value="worsecough">中</option>
										<option value="worstcough">重</option>
									</select>
								</td>
							</tr>
						</table>
						<br>
					</div>
					<hr>
					
				</div>
			</form>
		</div>
	</div>
	<div id="right">
		<div id="">
		<!-- 输入数据 -->
		<div style="width:100%">
			<hr>
			<h2>批次：${batch == 'null' ? '全部批次' : batch }</h2>
			<hr>
			<h3>诊断：</h3>
			<p>${diagnose }</p>
			<hr>
			<h3>描述：</h3>
			<p>${description }</p>
			<hr>
		</div>
		<!-- 预测结果 -->
		<c:if test="${medicineListByStatis != null }">
			<div style="width:100%">
				<h3>基于病例统计的预测处方：</h3>
				<p>
					<c:forEach items="${medicineListByStatis }" var="item">
						${item }
					</c:forEach>
				</p>
				<p>
					${predictMedicine }
				</p>
			</div>
		</c:if>
		<!-- 预测结果 -->
		<c:if test="${medicineListByMachine != null }">
			<div style="width:100%">
				<hr>
				<h3>基于机器学习的预测处方：</h3>
				<p>
					<c:forEach items="${medicineListByMachine }" var="item">
						${item }
					</c:forEach>
				</p>
			</div>
		</c:if>
		<!-- 预测结果 -->
		
		<c:if test="${orignMedicines != null }">
			<div style="width:100%">
				<hr>
				<h3>原始病例处方：</h3>
				<p>
					<c:forEach items="${orignMedicines }" var="item">
						${item }
					</c:forEach>
				</p>
			</div>
		</c:if>
			
		
		<!-- 相似病历 -->
		<c:if test="${similaryRecords != null}">
			<div id="similayrecords">
				<hr>
				<h3>相似病历：</h3>
				<table id="similarytable" border="1px">
					<tr>
						<th>序号</th>
						<th>挂号号</th>
						<th>中医诊断</th>
						<th>中医描述</th>
						<th>备注</th>
					</tr>
					<c:forEach var="erecord" items="${similaryRecords }" varStatus="status">
						<tr>
				        	<td>${status.index+1 }</td>
				        	<td>${erecord.getRegistrationno() }</td>
				        	<td>${erecord.getChinesediagnostics() }</td>
				        	<td>${erecord.getConditionsdescribed() }</td>
			    	        <td><a href="detailRecord?ehealthregno=${erecord.getRegistrationno() }">详细信息</a></td>
		            	</tr>
					</c:forEach>
				</table>
			</div>
		</c:if>
	
	</div>
	</div>

</body>
</html>