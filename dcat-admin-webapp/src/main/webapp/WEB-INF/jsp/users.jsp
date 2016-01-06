<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">

<head>
<title>DCAT Harvester Admin</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
	integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
	crossorigin="anonymous">
</head>

<body>
	<h1>DCAT Harvester Admin</h1>

	<p>
	You are logged in as <b>${username}</b>. <a class="btn btn-default"
							href="${pageContext.request.contextPath}/login?logout"
							role="button">
							Log out
	</a>
	</p>
	
	<c:if test="${not empty users}">
		<table class="table table-striped">
			<thead>
				<tr>
					<th>Username</th>
					<th>Email</th>
					<th>Role</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="user" items="${users}">
					<tr>
						<td>${user.username}</td>
						<td>${user.email}</td>
						<td>${user.role}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	
</body>
</html>