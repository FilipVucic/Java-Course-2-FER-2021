<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Blog Entries</title>

    <style type="text/css">
        .greska {
            font-family: fantasy;
            font-weight: bold;
            font-size: 0.9em;
            color: #FF0000;
            padding-left: 110px;
        }

        .formLabel {
            display: inline-block;
            width: 100px;
            font-weight: bold;
            text-align: right;
            padding-right: 10px;
        }

        .formControls {
            margin-top: 10px;
        }
    </style>
</head>

<header>
    <c:choose>
        <c:when test="${sessionScope['current.user.id']!=null}">
            <h5>First name: ${sessionScope['current.user.fn']}</h5>
            <h5>Last name: ${sessionScope['current.user.ln']}</h5>
            <h5><a href="${pageContext.request.contextPath}/servleti/logout">Log out</a></h5>
        </c:when>
        <c:otherwise>
            <h5>Not logged in</h5>
            <h5><a href="${pageContext.request.contextPath}/servleti/main">Login</a></h5>
        </c:otherwise>
    </c:choose>
    <hr>
</header>

<body>
<h2>Blog entries list:</h2>
<c:choose>
    <c:when test="${blogEntries.isEmpty()}">
        <p>There are currently no blog entries for this author.</p>
    </c:when>
    <c:otherwise>
        <ol>
            <c:forEach var="blogEntry" items="${blogEntries}">
                <li>
                    <a href="${pageContext.request.contextPath}/servleti/author/${nick}/${blogEntry.id}">${blogEntry.title}</a>
                </li>
            </c:forEach>
        </ol>
    </c:otherwise>
</c:choose>

<c:if test="${sessionScope['current.user.nick'].equals(nick)}">
    <p><a href="${pageContext.request.contextPath}/servleti/author/${nick}/new">Create new blog entry</a></p>
</c:if>

</body>
</html>
