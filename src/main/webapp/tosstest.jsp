<%--
  Created by IntelliJ IDEA.
  User: ultradad
  Date: 1/8/15
  Time: 2:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>
<head>
  <title></title>
</head>
<body>
This is an toss test.

<form action="/api/toss" method="post">

  image: <input type="text" title="image" name="image"><p/>
  game: <input type="text" title="game" name="game"><p/>
  lat: <input type="text" title="lat" name="lat"><p/>
  long: <input type="text" title="long" name="long"><p/>
  <input type="submit" value="Submit">
</form>

</body>
</html>
