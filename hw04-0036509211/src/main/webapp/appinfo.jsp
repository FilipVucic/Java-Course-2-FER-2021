<%@ page import="hr.fer.oprpp1.Keys" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Title</title>
    <style>
        body {
            background-color: <%= session.getAttribute(Keys.KEY_BG_COLOR) %>;
        }
    </style>
</head>
<body>
<h1>App is running for: <%
    long startTime = (long) request.getServletContext().getAttribute(Keys.KEY_START);
    long difference = System.currentTimeMillis() - startTime;
    long days = TimeUnit.MILLISECONDS.toDays(difference);
    long hours = TimeUnit.MILLISECONDS.toHours(difference) - TimeUnit.DAYS.toHours(days);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(hours);
    long seconds = TimeUnit.MILLISECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(minutes);
    long millis = difference - TimeUnit.SECONDS.toMillis(seconds);
    out.write(String.format("%d days %d hours %d minutes %d seconds %d milliseconds",
            days, hours, minutes, seconds, millis));
%>
</h1>
</body>
</html>
