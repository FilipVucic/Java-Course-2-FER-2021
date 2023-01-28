<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Blog Entry</title>

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

<h2>${blogEntry.title}</h2>
<p>${blogEntry.text}</p>
<c:if test="${sessionScope['current.user.nick'].equals(nick)}">
    <p><a href="${pageContext.request.contextPath}/servleti/author/${nick}/edit?eid=${blogEntry.id}">Edit this blog
        entry</a></p>
</c:if>

<h3>Comments:</h3>
<c:choose>
    <c:when test="${blogEntry.comments.isEmpty()}">
        <p>There are currently no comments on this blog entry.</p>
    </c:when>
    <c:otherwise>
        <ol>
            <c:forEach var="comment" items="${blogEntry.comments}">
                <li>${comment.usersEMail}: ${comment.message}</li>
            </c:forEach>
        </ol>
    </c:otherwise>
</c:choose>

<h3>Add new comment:</h3>
<form action="${pageContext.request.contextPath}/servleti/saveblogcomment" method="post">
    <input type="hidden" name="entryId" value='<c:out value="${entryId}"/>'>
    <input type="hidden" name="entryNick" value='<c:out value="${nick}"/>'>
    <div>
        <c:choose>
            <c:when test="${sessionScope['current.user.email'] != null}">
                <div>
                    <span>Your EMail: ${sessionScope['current.user.email']}</span>
                    <input type="hidden" name="usersEMail"
                           value='<c:out value="${sessionScope['current.user.email']}"/>'>
                </div>
            </c:when>
            <c:otherwise>
                <div>
                    <span class="formLabel">Your EMail</span>
                    <input type="text" name="usersEMail" value='<c:out value="${zapis.usersEMail}"/>' size="30">
                </div>
                <c:if test="${zapis.imaPogresku('email')}">
                    <div class="greska"><c:out value="${zapis.dohvatiPogresku('email')}"/></div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>

    <div>
        <div>
            <span class="formLabel">Message</span><input type="text" name="message"
                                                         value='<c:out value="${zapis.message}"/>' size="200">
        </div>
        <c:if test="${zapis.imaPogresku('message')}">
            <div class="greska"><c:out value="${zapis.dohvatiPogresku('message')}"/></div>
        </c:if>
    </div>

    <div class="formControls">
        <span class="formLabel">&nbsp;</span>
        <input type="submit" name="metoda" value="Save">
        <input type="submit" name="metoda" value="Quit">
    </div>
</form>

</body>
</html>
