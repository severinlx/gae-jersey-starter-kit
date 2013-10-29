<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="name.vysoky.example.domain.Resource" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/jsp/head.jsp" %>
    <title>Resources</title>
</head>
<body>
<%@include file="/jsp/header.jsp" %>
<%
    @SuppressWarnings("unchecked")
    List<Resource> resources = (List<Resource>) request.getAttribute("it");
%>
<h1>Upload resource</h1>
<form action="<%= request.getContextPath() + "/resources/upload"%>" method="post" enctype="multipart/form-data">
    <p>Select a file : <input type="file" name="file" size="45" /></p>
    <input type="submit" value="Upload It" />
</form>
<h1>Resources</h1>
<ul>
<% for (Resource resource : resources) { %>
    <li><%= resource.getPath() %></li>
<% } %>
</ul>
<%@include file="/jsp/footer.jsp" %>
</body>
</html>