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

<body>
<h1>
    <c:choose>
        <c:when test="${zapis.title.isEmpty()}">
            New Blog Entry
        </c:when>
        <c:otherwise>
            Edit Blog Entry
        </c:otherwise>
    </c:choose>
</h1>

<form action="${pageContext.request.contextPath}/servleti/saveblogentry" method="post">

    <input type="hidden" name="id" value='<c:out value="${zapis.id}"/>'>
    <div>
        <div>
            <span class="formLabel">Title</span><input type="text" name="title" value='<c:out value="${zapis.title}"/>'
                                                       size="20">
        </div>
        <c:if test="${zapis.imaPogresku('title')}">
            <div class="greska"><c:out value="${zapis.dohvatiPogresku('title')}"/></div>
        </c:if>
    </div>

    <div>
        <div>
            <span class="formLabel">Text</span><input type="text" name="text" value='<c:out value="${zapis.text}"/>'
                                                      size="500">
        </div>
        <c:if test="${zapis.imaPogresku('text')}">
            <div class="greska"><c:out value="${zapis.dohvatiPogresku('text')}"/></div>
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
