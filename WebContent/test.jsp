<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>地图</title>
	<link href="style/authority/main_css.css" rel="stylesheet" type="text/css" />
	<link href="style/authority/zTreeStyle.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="scripts/jquery/jquery-1.7.1.js"></script>
	<script type="text/javascript" src="scripts/zTree/jquery.ztree.core-3.2.js"></script>
	<script type="text/javascript" src="scripts/authority/commonAll.js"></script>
	<script type = "text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=FPhbvg5kig8Nv4teppVD50p6dtaCLbPr"></script>
	
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
				//var str = "<div><button type='button'>默认样式</button></div>";
				//document.getElementById("nav_resource").innerHTML = str;
			}else  if('left_tab2' == tabid){
				$(document).ajaxStart(onStart).ajaxSuccess(onStop);
				// 异步加载"系统管理"下的菜单
				
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
		 	<div id="nav_module">
		 		<img src="images/common/module_1.png" width="210" height="58"/>
		 	</div>
		 	<div id="nav_resource">
		 		<div><button type='button'>默认样式</button></div>
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
				side.css({width:'280px'});
				$('#top_nav').css({width:'77%', left:'304px'});
	        	$('#main').css({left:'280px'});
			}else{
		        if ( left_menu_cnt.is(":visible") ) {
					left_menu_cnt.hide(10, 'linear');
					side.css({width:'60px'});
		        	$('#top_nav').css({width:'100%', left:'60px', 'padding-left':'28px'});
		        	$('#main').css({left:'60px'});
		        	$("#show_hide_btn").find('img').attr('src', 'images/common/nav_show.png');
		        } else {
					left_menu_cnt.show(500, 'linear');
					side.css({width:'280px'});
					$('#top_nav').css({width:'77%', left:'304px', 'padding-left':'0px'});
		        	$('#main').css({left:'280px'});
		        	$("#show_hide_btn").find('img').attr('src', 'images/common/nav_hide.png');
		        }
			}
		}
	</script>
	
    <!-- side menu start -->
    <div id="main">
    </div>

</body>

<script>
	var map = new BMap.Map("main");
	map.centerAndZoom(new BMap.Point(116.404, 39.915), 12);
	map.enableScrollWheelZoom(true);
	drawLine();
	
	function drawLine() {
		var pois = [
			new BMap.Point(116.350658, 39.938285),
			new BMap.Point(116.386446, 39.939281),
			new BMap.Point(116.389034, 39.913828),
			new BMap.Point(116.442501, 39.914603)
		];
		var polyline = new BMap.Polyline(pois, {
			enableEditing: false,
			enableClicking: true,
			strokeWeight: 2,
			strokeOpacity: 0.8,
			strokeColor: "red"
		});
		map.addOverlay(polyline);
	}
</script>
</html>
   
 