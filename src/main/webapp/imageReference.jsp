<%@ page import="com.eweware.phototoss.core.TossRecord" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.eweware.phototoss.core.PhotoRecord" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%@ page import="com.eweware.phototoss.api.CatchToss" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.List" %>
<%@ page import="com.eweware.phototoss.api.ImageLineage" %>
<%--
  Created by IntelliJ IDEA.
  User: ultradad
  Date: 9/16/15
  Time: 8:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String pathInfo = request.getPathInfo(); // /{value}/test
  String[] pathParts = pathInfo.split("/");
  String imageIdStr = pathParts[pathParts.length-1];
  long imageId = Long.parseLong(imageIdStr);
  PhotoRecord photoRecStatic = ofy().load().key(Key.create(PhotoRecord.class, (long) imageId)).now();
  String imageUrl = "", ownerid="";
  if (photoRecStatic != null) {
      imageUrl = photoRecStatic.imageUrl;
      ownerid = photoRecStatic.ownername;
  }
%>
<html>
<head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# article: http://ogp.me/ns/article#">
    <meta property="fb:app_id" content="439651239569547" />
    <meta property="og:type"   content="article" />
    <meta property="og:url"    content="http://phototoss-server-01.appspot.com/image/<%=imageIdStr%>" />
    <meta property="og:title"  content="Tossed from PhotoToss" />
    <meta property="og:image"  content="<%=imageUrl%>" />
    <meta property="article:author"  content="<%=ownerid%>" />
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:site" content="@eweware">
    <meta name="twitter:title" content="Tossed from PhotoToss">
    <meta name="twitter:description" content="This image is being tossed about on PhotoToss!">
    <meta name="twitter:image" content="<%=imageUrl%>" />
  <title>PhotoToss Toss Page!</title>
  <%@include file="includes/stdincludes.jsp" %>
</head>
<body>
<%@include file="includes/header.jsp" %>
<%

  if (photoRecStatic != null) {
%>
<div>This guy here</div>
<div>
  <a href="${pageContext.request.contextPath}/user/<%=photoRecStatic.ownerid%>"> <img src="https://graph.facebook.com/<%=photoRecStatic.ownername%>/picture?type=square";> </a>
</div>
<div>
  has this image here:
</div>
<div>
  <a href="${pageContext.request.contextPath}/image/<%=photoRecStatic.id%>"> <img src="<%=photoRecStatic.imageUrl%>";> </a>
</div>
<%
  List<PhotoRecord> lineage = ImageLineage.GetImageLineage(photoRecStatic);
  PhotoRecord lastRecord = photoRecStatic;

  if (lineage.size() > 0) {
    for (PhotoRecord curRec : lineage) {
      %>
    <div>
      who got it with this catch:
    </div>
<div>
  <img src="<%=lastRecord.catchUrl%>";>
</div>
<div>
  from the guy here:
</div>
<div>
    <a href="${pageContext.request.contextPath}/user/<%=curRec.ownerid%>"> <img src="https://graph.facebook.com/<%=curRec.ownername%>/picture?type=square";></a>
</div>
<%
      lastRecord = curRec;
    }
  } else {
    %>This is the original owner of the image!<%
  }


} else {
%>
<h2 style="color:red">Looks like an invalid image.  That sucks!  Soz!</h2>
<%
  }
%>
<%@include file="includes/footer.jsp" %>
</body>
</html>
