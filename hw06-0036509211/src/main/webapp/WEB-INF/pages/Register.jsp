<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>New Account</title>

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
<h2>
    New Account
</h2>

<form method="post">
    <div>
        <div>
            <span class="formLabel">First Name</span><input type="text" name="firstName"
                                                            value='<c:out value="${zapis.firstName}"/>' size="30">
        </div>
        <c:if test="${zapis.imaPogresku('firstName')}">
            <div class="greska"><c:out value="${zapis.dohvatiPogresku('firstName')}"/></div>
        </c:if>
    </div>

    <div>
        <div>
            <span class="formLabel">Last Name</span><input type="text" name="lastName"
                                                           value='<c:out value="${zapis.lastName}"/>' size="30">
        </div>
        <c:if test="${zapis.imaPogresku('lastName')}">
            <div class="greska"><c:out value="${zapis.dohvatiPogresku('lastName')}"/></div>
        </c:if>
    </div>

    <div>
        <div>
            <span class="formLabel">EMail</span><input type="text" name="email" value='<c:out value="${zapis.email}"/>'
                                                       size="30">
        </div>
        <c:if test="${zapis.imaPogresku('email')}">
            <div class="greska"><c:out value="${zapis.dohvatiPogresku('email')}"/></div>
        </c:if>
    </div>

    <div>
        <div>
            <span class="formLabel">Nick</span><input type="text" name="nick" value='<c:out value="${zapis.nick}"/>'
                                                      size="30">
        </div>
        <c:if test="${zapis.imaPogresku('nick')}">
            <div class="greska"><c:out value="${zapis.dohvatiPogresku('nick')}"/></div>
        </c:if>
    </div>

    <div>
        <div>
            <span class="formLabel">Password</span><input type="password" name="password"
                                                          value='<c:out value="${zapis.passwordHash}"/>' size="30">
        </div>
        <c:if test="${zapis.imaPogresku('password')}">
            <div class="greska"><c:out value="${zapis.dohvatiPogresku('password')}"/></div>
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
