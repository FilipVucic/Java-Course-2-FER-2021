<%@ page import="hr.fer.oprpp1.Keys" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Glasanje</title>
    <style>
        body {
            background-color: <%= session.getAttribute(Keys.KEY_BG_COLOR) %>;
        }
    </style>
</head>
<body>
<h1>Glasanje za omiljeni bend:</h1>
<p>Od sljedećih bendova, koji Vam je bend najdraži? Kliknite na link kako biste glasali!</p>
<ol>
    <c:forEach var="band" items="<%= request.getAttribute(Keys.KEY_BANDS) %>">
        <li><a href="glasanje-glasaj?id=${band.key}">${band.value[0]}</a></li>
    </c:forEach>
</ol>
</body>
</html>
