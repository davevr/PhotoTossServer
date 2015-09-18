<%@ page import="com.eweware.phototoss.core.UserRecord" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%--
  Created by IntelliJ IDEA.
  User: ultradad
  Date: 9/16/15
  Time: 9:45 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String pathInfo = request.getPathInfo(); // /{value}/test
  String[] pathParts = pathInfo.split("/");
  String userIdStr = pathParts[pathParts.length-1];
  long userId = Long.parseLong(userIdStr);
  UserRecord userRecStatic = ofy().load().key(Key.create(UserRecord.class, (long) userId)).now();
  %>
%>
<html>
<head>
  <title>PhotoToss User Page!</title>
  <meta name="apple-itunes-app" content="app-id=890164360, app-argument=http://phototoss-server-01.appspot.com/user/<%=userIdStr%>">
  <meta name="author" content="eweware, inc.">
  <meta name="google-play-app" content="app-id=com.eweware.heard">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="css/jquery.smartbanner.css" type="text/css" media="screen">
  <link rel="apple-touch-icon" href="apple-touch-icon.png">
  <%@include file="includes/stdincludes.jsp" %>
</head>
<body>
<script src="includes/jquery.smartbanner.js"></script>
<script type="text/javascript">
  $(function() { $.smartbanner() } )
</script>
<%@include file="includes/header.jsp" %>
<%
  if (userRecStatic != null) {
%>
<div>This guy here</div>
<div>
  <a href="${pageContext.request.contextPath}/user/<%=userRecStatic.id%>"> <img src="https://graph.facebook.com/<%=userRecStatic.username%>/picture?type=square";> </a>
</div>
<div>
  <% } else {
  %>
    <h2 style="color:red">Looks like that user doesn't really exist!  Soz!</h2>
  <%
  }
  %>
<%@include file="includes/footer.jsp" %>
</body>
</html>
