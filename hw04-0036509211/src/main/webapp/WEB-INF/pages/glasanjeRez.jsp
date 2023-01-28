<%@ page import="hr.fer.oprpp1.Keys" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Glasanje</title>
    <style type="text/css">
        table.rez td {
            text-align: center;
        }

        body {
            background-color: <%= session.getAttribute(Keys.KEY_BG_COLOR) %>;
        }
    </style>
</head>
<body>
<h1>Rezultati glasanja</h1>
<p>Ovo su rezultati glasanja.</p>
<table border="1" cellspacing="0" class="rez">
    <thead>
    <tr>
        <th>Bend</th>
        <th>Broj glasova</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="result" items="<%= request.getAttribute(Keys.KEY_BAND_VOTING) %>">
        <tr>
            <td>${result.name}</td>
            <td>${result.votes}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<h2>Grafički prikaz rezultata</h2>
<img alt="Pie-chart" src="glasanje-grafika" width="400" height="400"/>

<h2>Rezultati u XLS formatu</h2>
<p>Rezultati u XLS formatu dostupni su <a href="glasanje-xls">ovdje</a></p>

<h2>Razno</h2>
<p>Primjeri pjesama pobjedničkih bendova:</p>
<ul>
    <c:forEach var="band" items="<%= request.getAttribute(Keys.KEY_BAND_WINNERS) %>">
        <li><a href=${band[1]} target="_blank">${band[0]}</a></li>
    </c:forEach>
</ul>
</body>
</html>
