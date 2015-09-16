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
<html>
<head>
  <title>PhotoToss User Page!</title>
  <%@include file="includes/stdincludes.jsp" %>
</head>
<body>
<%@include file="includes/header.jsp" %>
<%
  String pathInfo = request.getPathInfo(); // /{value}/test
  String[] pathParts = pathInfo.split("/");
  String userIdStr = pathParts[pathParts.length-1];
  long userId = Long.parseLong(userIdStr);
  UserRecord userRecStatic = ofy().load().key(Key.create(UserRecord.class, (long) userId)).now();
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
