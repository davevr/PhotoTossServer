<%--
  Created by IntelliJ IDEA.
  User: davevr
  Date: 1/21/15
  Time: 11:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ page import="com.eweware.phototoss.core.PhotoRecord" %>
<%@ page import="java.util.List" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="com.eweware.phototoss.core.UserRecord" %>
<%@ page import="com.eweware.phototoss.api.Authenticator" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.eweware.phototoss.core.TossRecord" %>

<%
  String tossIdStr = request.getParameter("id");
  long tossId = Long.parseLong(tossIdStr);

%>
<html>
<head>
    <title></title>
</head>
<body>
<%
  PhotoRecord photo = null;
  TossRecord toss = ofy().load().key(Key.create(TossRecord.class, tossId)).now();
  long imageId;
  if (toss != null) {
    photo = ofy().load().key(Key.create(PhotoRecord.class, toss.imageid)).now();
  }

  if (photo == null) { %>
    Sorry, there is no image here!
 <% }
  else { %>
    Good news!  Here is your image!<p/>
    <img src="<%=photo.imageUrl%>=s256"><br/>
    <b><%=photo.caption%></b>
<%  }

%>
<p/>
And now we are at the end!
</body>
</html>
