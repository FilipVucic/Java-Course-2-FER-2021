<%@ page import="hr.fer.oprpp1.Keys" %>
<%@ page import="java.util.Random" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Funny2</title>
    <style>
        body {
            background-color: <%= session.getAttribute(Keys.KEY_BG_COLOR) %>;
            color: <%
                String[] colors = new String[] {
                        "BLUE", "RED", "GREEN", "YELLOW"
                };
                String color = colors[new Random().nextInt(colors.length)];
                out.write(color);
            %>
        }
    </style>
</head>
<body>
<h1>
    Da, Maja :)
</h1>
</body>
</html>
