<%@page import="model.Document"%>
<%@page import="servlets.SearchServlet"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Azure Search - Feature Search</title>
</head>
<body>
<body>
	<h2>Hotels Search</h2>
	<form action="SearchServlet" method="POST">
		<input type="text" name="SearchQuery" size="100" > 
		<input type="submit" value="Search" />
	</form>

	<%
		List<Document> DocList = (List<Document>) request.getAttribute("DocList");
		
		if (DocList != null) {	
	%>
	<br/>
	<table border="1">
		<tr>
			<td>Hotel Id</td>
			<td>Hotel Name</td>
			<td>Description</td>
			<td>Description (French)</td>
			<td>Category</td>
			<td>Tags</td>
			<td>Parking Included?</td>
			<td>Last Renovation Date</td>
			<td>Rating</td>
			<td>Address</td>
			<td>Location</td>
			<td>Number of Rooms</td>
		</tr>
		<%
			for (int i = 0; i < DocList.size(); i++) {
		%>
		<tr>
			<td><%=DocList.get(i).getHotelId()%></td>
			<td><%=DocList.get(i).getHotelName()%></td>
			<td><%=DocList.get(i).getDescription()%></td>
			<td><%=DocList.get(i).getDescription_fr()%></td>
			<td><%=DocList.get(i).getCategory()%></td>
			<td><%=DocList.get(i).getTags()%></td>
			<td><%=DocList.get(i).isParkingIncluded()%></td>
			<td><%=DocList.get(i).getLastRenovationDate()%></td>
			<td><%=DocList.get(i).getRating()%></td>
			<td><%=DocList.get(i).getAddress().toString()%></td>
			<td><%=DocList.get(i).getLocation().toString()%></td>
			<td><%=DocList.get(i).getRooms().size()%></td>
		</tr>
		<%
			}
		%>
	</table>
	<%
		}
	%>
</body>

</html>