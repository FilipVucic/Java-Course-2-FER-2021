<%@ page import="hr.fer.oprpp2.Keys" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Glasanje</title>
    <style type="text/css">
        table.rez td {
            text-align: center;
        }
    </style>
</head>
<body>
<h1>Rezultati glasanja</h1>
<p>Ovo su rezultati glasanja.</p>
<table border="1" cellspacing="0" class="rez">
    <thead>
    <tr>
        <th>Subjekt</th>
        <th>Broj glasova</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="option" items="<%= request.getAttribute(Keys.KEY_POLL_OPTIONS) %>">
        <tr>
            <td>${option.optionTitle}</td>
            <td>${option.votesCount}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<h2>Grafički prikaz rezultata</h2>
<img alt="Pie-chart" src="glasanje-grafika?pollID=<%= request.getAttribute(Keys.KEY_POLL_ID) %>" width="400"
     height="400"/>

<h2>Rezultati u XLS formatu</h2>
<p>Rezultati u XLS formatu dostupni su <a
        href="glasanje-xls?pollID=<%= request.getAttribute(Keys.KEY_POLL_ID) %>">ovdje</a></p>

<h2>Razno</h2>
<p>Linkovi pobjedničkih subjekata:</p>
<ul>
    <c:forEach var="winner" items="<%= request.getAttribute(Keys.KEY_WINNERS) %>">
        <li><a href=${winner.optionLink} target="_blank">${winner.optionTitle}</a></li>
    </c:forEach>
</ul>
</body>
</html>
