<%@ page import="hr.fer.oprpp2.Keys" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Index</title>
</head>
<body>
<h1>Odaberite anketu:</h1>
<ol>
    <c:forEach var="poll" items="<%= request.getAttribute(Keys.KEY_POLLS) %>">
        <li><a href="glasanje?pollID=${poll.ID}">${poll.title}</a></li>
    </c:forEach>
</ol>
</body>
</html>