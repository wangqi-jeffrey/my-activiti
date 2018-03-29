<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
<meta charset="utf-8">
<title>Activiti 5.19 流程设计器</title>
<base href="<%=basePath%>">

<script type='text/javascript' src='ext/ext-all.js'></script>
<script type='text/javascript' src='ext/ext-theme-neptune.js'></script>
<script type='text/javascript' src='ext/examples/shared/examples.js'></script>
<link rel="stylesheet" type="text/css" href="ext/packages/ext-theme-neptune/build/resources/ext-theme-neptune-all.css" />

<link rel="stylesheet" type="text/css" href="ext/examples/shared/example.css" />
<script type='text/javascript' src='app.js'></script>

<link rel="stylesheet" type="text/css" href="resources/css/custom.css" />
<link rel="stylesheet" type="text/css" href="resources/css/icon.css" />
</head>
<body>
</body>
</html>
