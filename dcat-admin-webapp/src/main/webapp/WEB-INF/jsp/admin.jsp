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

	<c:if test="${not empty dcatSources}">
		<table class="table table-striped">
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th>URL</th>
					<th>Harvest</th>
					<th>Remove</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="dcatSource" items="${dcatSources}">
					<tr>
						<td>${dcatSource.name}</td>
						<td>${dcatSource.description}</td>
						<td>${dcatSource.url}</td>
						<td><a class="btn btn-default"
							href="${pageContext.request.contextPath}/admin/harvestDcatSource?name=${dcatSource.name}"
							role="button"> <span
								class="glyphicon glyphicon-cloud-download" aria-hidden="true"></span>
						</a></td>
						<td><a class="btn btn-default"
							href="${pageContext.request.contextPath}/admin/deleteDcatSource?name=${dcatSource.name}"
							role="button"> <span
								class="glyphicon glyphicon-remove" aria-hidden="true"></span>
						</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

	<h2>Add new DCAT source</h2>
	<form:form method="POST" action="${pageContext.request.contextPath}/admin/addDcatSource" modelAttribute="dcatSource">
	   <table class="table">
	    <tr>
	        <td><form:label path="description">Description</form:label></td>
	        <td><form:input path="description" /></td>
	    </tr>
	    <tr>
	        <td><form:label path="url">Url</form:label></td>
	        <td><form:input path="url" /></td>
	    </tr>
	    <tr>
	        <td colspan="2">
	            <button class="btn btn-default" type="submit"/><span class="glyphicon glyphicon-floppy-save" aria-hidden="true"> Save</span></button>
	        </td>
	    </tr>
	</table>  
	</form:form>
	
	
	
</body>
</html>