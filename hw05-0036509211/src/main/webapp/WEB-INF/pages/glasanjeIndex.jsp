<%@ page import="hr.fer.oprpp2.Keys" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Glasanje</title>
</head>
<body>
<h1><%= request.getAttribute(Keys.KEY_POLL_TITLE) %></h1>
<p><%= request.getAttribute(Keys.KEY_POLL_MESSAGE) %></p>
<ol>
    <c:forEach var="option" items="<%= request.getAttribute(Keys.KEY_POLL_OPTIONS) %>">
        <li>
            <a href="glasanje-glasaj?pollID=<%= request.getAttribute(Keys.KEY_POLL_ID) %>&pollOptionID=${option.ID}">${option.optionTitle}</a>
        </li>
    </c:forEach>
</ol>
</body>
</html>
