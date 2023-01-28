<%@ page import="hr.fer.oprpp1.Keys" %>
<%@ page import="java.util.Random" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Funny</title>
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
    Jesi čuo da je rodila Šuput Marija?
</h1>
<p>
<form action="funny2.jsp">
    <input type="submit" value="Maja?"/>
</form>
</p>
</body>
</html>
