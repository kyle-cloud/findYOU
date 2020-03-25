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
	<style type="text/css">
		body, html, #allmap {width:100%; height:100%; overflow:hidden; margin:0; font-family:"微软雅黑";}
	</style>
	<title>Insert title here</title>
	<script type = "text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=FPhbvg5kig8Nv4teppVD50p6dtaCLbPr"></script>
</head>
<body>
	
	<div id="allmap"></div>
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

</html>