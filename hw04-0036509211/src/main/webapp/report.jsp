<%@ page import="hr.fer.oprpp1.Keys" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>OS usage report</title>
    <style>
        body {
            background-color: <%= session.getAttribute(Keys.KEY_BG_COLOR) %>;
        }
    </style>
</head>
<body>
<h1>OS usage</h1>
<p>Here are the results of OS usage in survey that we completed.</p>
<img src="reportImage" alt="Pie Chart">
</body>
</html>
