<%--
  Created by IntelliJ IDEA.
  User: Dave
  Date: 2/7/2015
  Time: 4:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title></title>
</head>
<body>
This is an toss test.

<form action="/api/test/notify" method="post">

    key: <input type="text" title="key" name="key"><p/>
    title: <input type="text" title="title" name="title"><p/>
    body: <input type="text" title="body" name="body"><p/>
    <input type="submit" value="Submit">
</form>

</body>
</html>
