<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">

<head>
	<title>DCAT Admin</title>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
</head>

<body>
	<h1>DCAT Admin</h1>
	
	<c:if test="${not empty dcatSources}">
		<table class="table">
			<thead>
				<tr>
					<th>Name</th>
					<th>Url</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
		    <c:forEach var="dcatSource" items="${dcatSources}">
		        <tr>
		            <td>${dcatSource.name}</td>
		            <td>${dcatSource.url}</td>
		            <td><a class="btn btn-default" href="http://localhost:8090/api/admin/harvest?name=${dcatSource.name}" role="button">Harvest</a></td>
		        </tr>
		    </c:forEach>
		    </tbody>
		</table>
	</c:if>
	
</body>

</html>