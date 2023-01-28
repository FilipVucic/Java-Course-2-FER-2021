<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Main</title>

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
        <c:when test="${sessionScope['current.user.id'] != null}">
            <h5>First name: ${sessionScope['current.user.fn']}</h5>
            <h5>Last name: ${sessionScope['current.user.ln']}</h5>
            <h5><a href="${pageContext.request.contextPath}/servleti/logout">Log out</a></h5>
        </c:when>
        <c:otherwise>
            <h5>Not logged in</h5>
            <h5><a href="${pageContext.request.contextPath}/servleti/register">Create new account</a></h5>
            <form method="post">
                <div>
                    <div>
                        <span class="formLabel">Nickname</span><input type="text" name="nick"
                                                                      value='<c:out value="${zapis.nick}"/>' size="20">
                    </div>
                    <c:if test="${zapis.imaPogresku('nick')}">
                        <div class="greska"><c:out value="${zapis.dohvatiPogresku('nick')}"/></div>
                    </c:if>
                </div>

                <div>
                    <div>
                        <span class="formLabel">Password</span><input type="password" name="password"
                                                                      value='<c:out value="${zapis.passwordHash}"/>'
                                                                      size="20">
                    </div>
                    <c:if test="${zapis.imaPogresku('password')}">
                        <div class="greska"><c:out value="${zapis.dohvatiPogresku('password')}"/></div>
                    </c:if>
                </div>

                <div class="formControls">
                    <span class="formLabel">&nbsp;</span>
                    <input type="submit" name="metoda" value="Login">
                </div>
            </form>
        </c:otherwise>
    </c:choose>
    <hr>
</header>

<body>
<h2>Author list:</h2>
<c:choose>
    <c:when test="${authors.isEmpty()}">
        <p>There are currently no authors.</p>
    </c:when>
    <c:otherwise>
        <ol>
            <c:forEach var="author" items="${authors}">
                <li><a href="author/${author.nick}">${author.firstName} ${author.lastName}</a></li>
            </c:forEach>
        </ol>
    </c:otherwise>
</c:choose>
</body>
</html>
