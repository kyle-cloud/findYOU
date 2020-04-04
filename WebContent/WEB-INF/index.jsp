<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java" import="dao.MongoUtil"%>
<% MongoUtil.instance.getDB("liu"); %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>地图</title>
	<link href="style/authority/main_css.css" rel="stylesheet" type="text/css" />
	<link href="style/authority/common_style.css" rel="stylesheet" type="text/css" />
	<script src="scripts/jquery/jquery-1.7.1.js" type="text/javascript"></script>
	<script src="http://api.map.baidu.com/getscript?v=2.0&ak=FPhbvg5kig8Nv4teppVD50p6dtaCLbPr"  type = "text/javascript"></script>
	
	<script type="text/javascript">
		/* 上方菜单 */
		function switchTab(tabpage,tabid){
		var oItem = document.getElementById(tabpage).getElementsByTagName("li"); 
		    for(var i=0; i<oItem.length; i++){
		        var x = oItem[i];    
		        x.className = "";
			}
			if('left_tab1' == tabid){
				$(document).ajaxStart(onStart).ajaxSuccess(onStop);
				// 异步加载"业务模块"下的菜单
				var str = "<hr><br><div>&nbsp;IMSI&nbsp;&nbsp;<input type='text' class='ui_input_txt02' id='find_the_trail' placeholder='请输入IMSI'>&nbsp;<input type='button' value='查询' class='ui_input_btn01' onclick='findTheTrail();'>&nbsp;<input type='button' value='清除' class='ui_input_btn01' onclick='clearTheTrail();'></div><div id = 'numberOfTrails'></div>";
				$("#nav_resource").html(str);
			}else  if('left_tab2' == tabid){
				$(document).ajaxStart(onStart).ajaxSuccess(onStop);
				// 异步加载"系统管理"下的菜单
				var str = "<hr><br><div>&nbsp;IMSI&nbsp;&nbsp<input type='text' class='ui_input_txt02' id='find_the_similarity' placeholder='请输入IMSI'>&nbsp<input type='button' value='评分' class='ui_input_btn01' onclick='findTheSimilarity();'>&nbsp<input type='button' value='清除' class='ui_input_btn01' onclick='clearTheSimilarity();'></div><div id = 'tableOfTrails'></div>"
				$("#nav_resource").html(str);
			}else  if('left_tab3' == tabid){
				$(document).ajaxStart(onStart).ajaxSuccess(onStop);
				// 异步加载"其他"下的菜单
				
			} 
		}
		
		//ajax start function
		function onStart(){
			$("#ajaxDialog").show();
		}
		
		//ajax stop function
		function onStop(){
	// 		$("#ajaxDialog").dialog("close");
			$("#ajaxDialog").hide();
		}
	</script>
</head>

<body>
    <!-- side menu start -->
	<div id="side">
		<div id="left_menu">
		 	<ul id="TabPage2" style="height:200px; margin-top:50px;">
				<li id="left_tab1" class="selected" onClick="javascript:switchTab('TabPage2','left_tab1');" title="业务模块">
					<img alt="业务模块" title="业务模块" src="images/common/1_hover.jpg" width="33" height="31">
				</li>
				<li id="left_tab2" onClick="javascript:switchTab('TabPage2','left_tab2');" title="系统管理">
					<img alt="系统管理" title="系统管理" src="images/common/2.jpg" width="33" height="31">
				</li>		
				<li id="left_tab3" onClick="javascript:switchTab('TabPage2','left_tab3');" title="其他">
					<img alt="其他" title="其他" src="images/common/3.jpg" width="33" height="31">
				</li>
			</ul>
			
			
			<div id="nav_show" style="position:absolute; bottom:0px; padding:10px;">
				<a href="javascript:;" id="show_hide_btn">
					<img alt="显示/隐藏" title="显示/隐藏" src="images/common/nav_hide.png" width="35" height="35">
				</a>
			</div>
		 </div>
		 <div id="left_menu_cnt">
		 	<!-- <div id="nav_module">
		 		<img src="images/common/module_1.png" width="210" height="58"/>
		 	</div> -->
		 	<div id="nav_resource">
	 			<hr>
	 			<br>
 				<div>
 					&nbsp;IMSI&nbsp;
    				<input type="text" class="ui_input_txt02" id="find_the_trail" placeholder="请输入IMSI">		
    				<input type="button" value="查询" class="ui_input_btn01" onclick="findTheTrail();">
    				<input type="button" value="清除" class="ui_input_btn01" onclick="clearTheTrail();">
  				</div>
  				<div id = "numberOfTrails"></div>
		 	</div>
		 </div>
	</div>
	<script type="text/javascript">
		$(function(){
			$('#TabPage2 li').click(function(){
				var index = $(this).index();
				$(this).find('img').attr('src', 'images/common/'+ (index+1) +'_hover.jpg');
				$(this).css({background:'#fff'});
				$('#nav_module').find('img').attr('src', 'images/common/module_'+ (index+1) +'.png');
				$('#TabPage2 li').each(function(i, ele){
					if( i!=index ){
						$(ele).find('img').attr('src', 'images/common/'+ (i+1) +'.jpg');
						$(ele).css({background:'#044599'});
					}
				});
				// 显示侧边栏
				switchSysBar(true);
			});
			
			// 显示隐藏侧边栏
			$("#show_hide_btn").click(function() {
		        switchSysBar();
		    });
		});
		
		/**隐藏或者显示侧边栏**/
		function switchSysBar(flag){
			var side = $('#side');
	        var left_menu_cnt = $('#left_menu_cnt');
			if( flag==true ){	// flag==true
				left_menu_cnt.show(500, 'linear');
				side.css({width:'450px'});
				$('#top_nav').css({width:'77%', left:'304px'});
	        	$('#main').css({left:'450px'});
			}else{
		        if ( left_menu_cnt.is(":visible") ) {
					left_menu_cnt.hide(10, 'linear');
					side.css({width:'60px'});
		        	$('#top_nav').css({width:'100%', left:'60px', 'padding-left':'28px'});
		        	$('#main').css({left:'60px'});
		        	$("#show_hide_btn").find('img').attr('src', 'images/common/nav_show.png');
		        } else {
					left_menu_cnt.show(500, 'linear');
					side.css({width:'450px'});
					$('#top_nav').css({width:'77%', left:'304px', 'padding-left':'0px'});
		        	$('#main').css({left:'450px'});
		        	$("#show_hide_btn").find('img').attr('src', 'images/common/nav_hide.png');
		        }
			}
		}
		
		function findTheTrail() {
			var Imsi = document.getElementById("find_the_trail").value;
			if(Imsi == "") {
				alert("空值");
				$('#find_the_trail').val(""); //清空上次input框里的数据
				return
			}
			$.ajax({
		        //type: "POST", //请求的方式，默认get请求
		        url: "findTheTrail.do", //请求地址，后台提供的
		        data: {'IMSI': Imsi},//data是传给后台的字段，后台需要哪些就传入哪些
		        dataType: "json", //json格式，如果后台返回的数据为json格式的数据，那么前台会收到Object
		        success: function(data, status){
		        	$('#find_the_trail').val(""); //清空上次input框里的数据
		            console.log(data);
		            console.log(status);
		            $('#numberOfTrails').html("&nbsp;共查询到&nbsp;<b>" + data.length + "</b>&nbsp;条IMSI为&nbsp;<b>" + Imsi + "</b>&nbsp;的轨迹");	
		            drawLine(data);
		        }
		    });
		}
		
		function findTheSimilarity() {
			var Imsi = document.getElementById("find_the_similarity").value;
			if(Imsi == "") {
				alert("空值");
				$('#find_the_similarity').val(""); //清空上次input框里的数据
				return
			}
			$.ajax({
		        //type: "POST", //请求的方式，默认get请求
		        url: "findTheSimilarity.do", //请求地址，后台提供的
		        data: {'IMSI': Imsi},//data是传给后台的字段，后台需要哪些就传入哪些
		        dataType: "json", //json格式，如果后台返回的数据为json格式的数据，那么前台会收到Object
		        success: function(data, status){
		        	$('#find_the_similarity').val(""); //清空上次input框里的数据
		            console.log(data);
		            console.log(status);
		            if(data[0] == '0') {
		            	alert("测试轨迹不存在");
		            } else if (data[0] == '1') {
		            	alert("此轨迹为噪声轨迹");
		            } else {
		            	drawLineAndTable(data);
		            }
		        }
		    });
		}
	</script>
	
    <!-- side menu start -->
    <div id="main" style="border:1px solid #a9a9a9"></div>

</body>

<script>
	var map = new BMap.Map("main");
	map.centerAndZoom(new BMap.Point(116.404, 39.915), 12);
	map.enableScrollWheelZoom(true);
	
	function drawLine(data) {
		for(var i = 0; i < data.length; i ++) {
			var pois = [];
			var color = randomColor();
			if(i == 0 && data[i].test == 1) {
				color = "#FF0000";
			}
			for(var j = 0; j < data[i].points.length; j ++) {
				pois.push(new BMap.Point(data[i].points[j].lng, data[i].points[j].lat))
			}
			var polyline = new BMap.Polyline(pois, {
				enableEditing: false,
				enableClicking: true,
				strokeWeight: 2,
				strokeOpacity: 0.8,
				strokeColor: color,
			});
			map.addOverlay(polyline);
		}
	}
	
	function drawLineAndTable(data) {
		var tableInfos = document.getElementById('tableOfTrails');
		var code = '<br><div class="ui_tb"><TABLE class="table" cellspacing="0" cellpadding="0" width="80%" align="center" border="0">';
		code += '<TR><TH>No.</TH><TH>IMSI</TH><TH>Score</TH><TH>Color</TH></TR>';
		for(var i = 0; i < data.length; i ++) {
			var pois = [];
			for(var j = 0; j < data[i].points.length; j ++) {
				pois.push(new BMap.Point(data[i].points[j].lng, data[i].points[j].lat))
			}
			var color = randomColor();
			if(data[i].test == 1) {
				color = "#FF0000";
				code += '<TR><TD>' + i + '</TD><TD>' + data[i].IMSI + '</TD><TD>' + data[i].score + '</TD><TD>' + "<div style='width:60px;height:20px;margin:0 auto;background:" + color + "'></div>" + '</TD></TR>';
			}
			else {
				code += '<TR><TD>' + i + '</TD><TD>' + data[i].IMSI + '</TD><TD>' + data[i].score + '</TD><TD>' + "<div style='width:60px;height:20px;margin:0 auto;background:" + color + "'></div>" + '</TD></TR>';
			}
			var polyline = new BMap.Polyline(pois, {
				enableEditing: false,
				enableClicking: true,
				strokeWeight: 2,
				strokeOpacity: 0.8,
				strokeColor: color,
			});
			map.addOverlay(polyline);
		}
		tableInfos.innerHTML = code + '</TABLE></div>';
	}
	
	
	function randomColor() {
		let r = Math.floor(Math.random()*256)
		let g = Math.floor(Math.random()*256)
		let b = Math.floor(Math.random()*256)
		return "rgb("+r+','+g+','+b+")"
	}
	
	function clearTheTrail() {
		map.clearOverlays();
		 $('#numberOfTrails').html("");
	}
	
	function clearTheSimilarity() {
		map.clearOverlays();
		 $('#tableOfTrails').html("");
	}
</script>
</html>
   
 