<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="java.io.*"%>
<%@ page language="java" import="trail.*"%>
<%@ page language="java" import="test.test"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
	<title>Insert title here</title>
	<link rel="stylesheet" href="assets/css/amazeui.min.css"/>
	<link rel="stylesheet" href="assets/css/admin.css">
	<script src="assets/js/jquery.min.js"></script>
	<script src="assets/js/app.js"></script>
	<script type = "text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=FPhbvg5kig8Nv4teppVD50p6dtaCLbPr"></script>
</head>
<body>
	<header class="am-topbar admin-header">
	  	<div class="am-topbar-brand"><img src="assets/i/logo.png"></div>
		<div class="am-collapse am-topbar-collapse" id="topbar-collapse">
	    	<ul class="am-nav am-nav-pills am-topbar-nav admin-header-list">
				<li class="am-dropdown tognzhi" data-am-dropdown>
			  		<button class="am-btn am-btn-primary am-dropdown-toggle am-btn-xs am-radius am-icon-bell-o" data-am-dropdown-toggle> 消息管理<span class="am-badge am-badge-danger am-round">6</span></button>
			  		<ul class="am-dropdown-content">
			  			<li class="am-dropdown-header">所有消息都在这里</li>
			  			<li><a href="#">未激活会员 <span class="am-badge am-badge-danger am-round">556</span></a></li>
			    		<li><a href="#">未激活代理 <span class="am-badge am-badge-danger am-round">69</span></a></a></li>
			    		<li><a href="#">未处理汇款</a></li>
					</ul>
				</li>
				<li class="kuanjie">			 	
					<a href="#">会员管理</a>          
					<a href="#">奖金管理</a> 
					<a href="#">订单管理</a>   
					<a href="#">产品管理</a> 
					<a href="#">个人中心</a> 
					<a href="#">系统设置</a>
				</li>
				<li class="soso">
					<p>   
						<select data-am-selected="{btnWidth: 70, btnSize: 'sm', btnStyle: 'default'}">
					    	<option value="b">全部</option>
					    	<option value="o">产品</option>
					    	<option value="o">会员</option> 
			        	</select>
					</p>
					<p class="ycfg"><input type="text" class="am-form-field am-input-sm" placeholder="圆角表单域" /></p>
					<p><button class="am-btn am-btn-xs am-btn-default am-xiao"><i class="am-icon-search"></i></button></p>
	 			</li>
				<li class="am-hide-sm-only" style="float: right;"><a href="javascript:;" id="admin-fullscreen"><span class="am-icon-arrows-alt"></span> <span class="admin-fullText">开启全屏</span></a></li>
	    	</ul>
		</div>
	</header>
	
	<div class="am-cf admin-main" style="width:100%; height:100%;"> 
	
		<div class="nav-navicon admin-main admin-sidebar">
    		<div class="sideMenu am-icon-dashboard" style="color:#aeb2b7; margin: 10px 0 0 0;"> 欢迎系统管理员：清风抚雪</div>
			<div class="sideMenu">
		      	<h3 class="am-icon-flag"><em></em> <a href="#">商品管理</a></h3>
		      	<ul>
		        	<li><a href="">商品列表</a></li>
		        	<li class="func" dataType='html' dataLink='msn.htm' iconImg='images/msn.gif'>添加新商品</li>
		        	<li>商品分类</li>
		        	<li>用户评论</li>
		        	<li>商品回收站</li>
		        	<li>库存管理 </li>
		      	</ul>
		      	<h3 class="am-icon-cart-plus"><em></em> <a href="#"> 订单管理</a></h3>
		      	<ul>
		        	<li>订单列表</li>
		        	<li>合并订单</li>
		        	<li>订单打印</li>
		        	<li>添加订单</li>
		        	<li>发货单列表</li>
		        	<li>换货单列表</li>
		      	</ul>
		      	<h3 class="am-icon-users"><em></em> <a href="#">会员管理</a></h3>
		      	<ul>
		        	<li>会员列表 </li>
		        	<li>未激活会员</li>
		        	<li>团队系谱图</li>
		        	<li>会员推荐图</li>
		        	<li>推荐列表</li>
		      	</ul>
		      	<h3 class="am-icon-volume-up"><em></em> <a href="#">信息通知</a></h3>
		      	<ul>
		        	<li>站内消息 /留言 </li>
		        	<li>短信</li>
		        	<li>邮件</li>
		        	<li>微信</li>
		        	<li>客服</li>
		      	</ul>
		      	<h3 class="am-icon-gears"><em></em> <a href="#">系统设置</a></h3>
		      	<ul>
		        	<li>数据备份</li>
		        	<li>邮件/短信管理</li>
		        	<li>上传/下载</li>
		        	<li>权限</li>
		        	<li>网站设置</li>
		        	<li>第三方支付</li>
		        	<li>提现 /转账 出入账汇率</li>
		        	<li>平台设置</li>
		        	<li>声音文件</li>
		      	</ul>
		    </div>
		    <!-- sideMenu End --> 
	    	<script type="text/javascript">
				jQuery(".sideMenu").slide({
					titCell:"h3", //鼠标触发对象
					targetCell:"ul", //与titCell一一对应，第n个titCell控制第n个targetCell的显示隐藏
					effect:"slideDown", //targetCell下拉效果
					delayTime:300 , //效果时间
					triggerTime:150, //鼠标延迟触发时间（默认150）
					defaultPlay:true,//默认是否执行效果（默认true）
					returnDefault:true //鼠标从.sideMen移走后返回默认状态（默认false）
					});
			</script> 
		</div>
		
		<div class="admin" id="allmap" style="width:100%; height:100%;">
			123123
		</div>
		
	</div>
</body>

<script>
	var map = new BMap.Map("allmap");
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
<script src="assets/js/amazeui.min.js"></script>
</html>