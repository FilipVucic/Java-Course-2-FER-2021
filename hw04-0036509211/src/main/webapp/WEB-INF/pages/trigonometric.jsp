<%@ page import="hr.fer.oprpp1.Keys" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Trigonometric</title>
    <style>
        body {
            background-color: <%= session.getAttribute(Keys.KEY_BG_COLOR) %>;
        }
    </style>
</head>
<body>
<table>
    <tr>
        <th>x</th>
        <th>sin(x)</th>
        <th>cos(x)</th>
    </tr>
    <c:forEach var="angle" items="<%= request.getAttribute(Keys.KEY_ANGLE_TRIG) %>">
        <tr>
            <td>${angle.key}</td>
            <td>${angle.value[0]}</td>
            <td>${angle.value[1]}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
