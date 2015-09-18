<%@ page import="com.eweware.phototoss.core.TossRecord" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.eweware.phototoss.core.PhotoRecord" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%@ page import="com.eweware.phototoss.api.CatchToss" %>
<%@ page import="java.util.logging.Level" %>
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
    String tossIdStr = pathParts[pathParts.length-1];
    long tossId = Long.parseLong(tossIdStr);
    TossRecord tossRecStatic = ofy().load().key(Key.create(TossRecord.class, (long) tossId)).now();
%>

<html>
<head>
    <title>PhotoToss Toss Page!</title>
    <meta name="apple-itunes-app" content="app-id=890164360, app-argument=http://phototoss-server-01.appspot.com/toss/<%=tossIdStr%>">
    <%@include file="includes/stdincludes.jsp" %>
</head>
<body>
<%@include file="includes/header.jsp" %>
<%
    if (tossRecStatic != null) {
        PhotoRecord photoRecStatic = ofy().load().key(Key.create(PhotoRecord.class, (long) tossRecStatic.imageId)).now();
        final Date currentTime = new Date();
        long diffInMillies = currentTime.getTime() - tossRecStatic.shareTime.getTime();
        long elapsedSec = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (elapsedSec > CatchToss.MAX_TOSS_TIME_IN_SECONDS) {
            %>Sorry, looks like that toss has expired!<%
        } else {
%>
<div>Toss In Progress!</div>
<div>
    <img src="https://graph.facebook.com/<%=tossRecStatic.ownerName%>/picture?type=square";>
</div>
<div>
    is tossing this image here:
</div>
<div>
    <img src="<%=photoRecStatic.imageUrl%>";>
</div>
<%}} else {
    %>
<h2 style="color:red">Looks like your toss is not valid.  Soz!</h2>
<%
}
%>
<%@include file="includes/footer.jsp" %>
</body>
</html>
