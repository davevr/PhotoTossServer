<%@ page import="com.eweware.phototoss.core.PhotoRecord" %>
<%@ page import="java.util.List" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="com.eweware.phototoss.core.TossRecord" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>PhotoToss Home</title>
  <%@include file="includes/stdincludes.jsp" %>
</head>
<body>
<%@include file="includes/header.jsp" %>
<h2>Welcome to PhotoToss!</h2>
<div>Here are some recent tosses to get you excited...</div>
<%
  List<PhotoRecord> photoList = ofy().load().type(PhotoRecord.class).order("-created").limit(10).list();
  long numItems = ofy().load().type(PhotoRecord.class).count();

  for (PhotoRecord curRec : photoList) {
    %>
<div>
  <a href="${pageContext.request.contextPath}/user/<%=curRec.ownerid%>"> <img src="https://graph.facebook.com/<%=curRec.ownername%>/picture?type=square";> </a>
  tossed image
  <a href="${pageContext.request.contextPath}/image/<%=curRec.id%>"> <img height="128" src="<%=curRec.thumbnailurl%>";> </a>
</div>
<%
  }
%>
<h2 style="margin-bottom: 100px">There are <%=numItems%> more where those came from!  Install today!</h2>
<%@include file="includes/footer.jsp" %>
</body>
</html>
