<%--
  Created by IntelliJ IDEA.
  User: Dave
  Date: 1/29/2015
  Time: 8:21 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<p>Enqueue a value, to be processed by a worker.</p>
<form action="/api/stats/enqueue" method="post">
  <input type="text" name="imageid">
  <input type="text" name="taskid">
  <input type="submit">
</form>
</body>
</html>
