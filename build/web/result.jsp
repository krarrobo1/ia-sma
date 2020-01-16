<%-- 
    Document   : result
    Created on : 16-ene-2020, 12:37:31
    Author     : kracWin
--%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Resultado</title>
    </head>
    <body>
        <%= request.getAttribute("fb-response") %>
    </body>
</html>
