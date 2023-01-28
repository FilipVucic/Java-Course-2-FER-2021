<%@ page import="hr.fer.oprpp1.Keys" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Set color</title>
    <style>
        body {
            background-color: <%= session.getAttribute(Keys.KEY_BG_COLOR) %>;
        }
    </style>
</head>
<body>
<p><a href="setcolor?${Keys.KEY_BG_COLOR}=white">WHITE</a></p>
<p><a href="setcolor?${Keys.KEY_BG_COLOR}=red">RED</a></p>
<p><a href="setcolor?${Keys.KEY_BG_COLOR}=green">GREEN</a></p>
<p><a href="setcolor?${Keys.KEY_BG_COLOR}=cyan">CYAN</a></p>
</body>
</html>
