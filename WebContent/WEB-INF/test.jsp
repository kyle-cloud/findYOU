<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="java.io.*"%>
<%@ page language="java" import="trail.*"%>
<%@ page language="java" import="test.test"%>
<% ArrayList<Object> result = new test().testCompressOnMap(); 
   ArrayList<Trail> trails = (ArrayList<Trail>)result.get(0);
   ArrayList<Trail> coarseTrails = (ArrayList<Trail>)result.get(1);
   ArrayList<ArrayList<Trail>> fineTrails = (ArrayList<ArrayList<Trail>>)result.get(2);%>
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
		<%System.out.println(trails.size());
		for(int i = 0; i < 1; i ++) {
			List<Point> points = trails.get(i).getPoints();%>
			var pois = [
				<%for(int j = 0; j < points.size(); j ++) {%>
					new BMap.Point(<%=points.get(j).getLng()%>, <%=points.get(j).getLat()%>),
				<%}%>
			];
			var polyline = new BMap.Polyline(pois, {
				enableEditing: false,
				enableClicking: true,
				strokeWeight: 2,
				strokeOpacity: 0.8,
				strokeColor: "red"
			});
			map.addOverlay(polyline);
		<%}%>
		
		<%System.out.println(coarseTrails.size());
		for(int i = 0; i < 1; i ++) {
			List<Point> points = coarseTrails.get(i).getPoints();%>
			var pois = [
				<%for(int j = 0; j < points.size(); j ++) {%>
					new BMap.Point(<%=points.get(j).getLng()%>, <%=points.get(j).getLat()%>),
				<%}%>
			];
			var polyline = new BMap.Polyline(pois, {
				enableEditing: false,
				enableClicking: true,
				strokeWeight: 2,
				strokeOpacity: 0.8,
				strokeColor: "blue"
			});
			map.addOverlay(polyline);
		<%}%>
		
		<%System.out.println(fineTrails.size());
		for(int i = 0; i < 1; i ++) {
			List<Trail> trail = fineTrails.get(i);%>
			var pois = [
				<%
				for(int j = 0; j < trail.size(); j ++) {
					List<Point> points = trail.get(j).getPoints();
					for(int k = 0; k < points.size(); k ++) {%>
						new BMap.Point(<%=points.get(k).getLng()%>, <%=points.get(k).getLat()%>),
				<%	}
				}%>
			];
			var polyline = new BMap.Polyline(pois, {
				enableEditing: false,
				enableClicking: true,
				strokeWeight: 2,
				strokeOpacity: 0.8,
				strokeColor: "green"
			});
			map.addOverlay(polyline);
		<%}%>
</script>

</html>