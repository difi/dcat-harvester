<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
			href="${pageContext.request.contextPath}/login?logout" role="button">
			Log out </a>
			<c:if test="${isAdmin}">
			<a class="btn btn-primary"
			href="${pageContext.request.contextPath}/admin/users" role="button">
			Users Admin</a>
			</c:if>
	</p>

	<div class="alert alert-danger" role="alert" id="errors"></div>

	<div class="col-md-3">
	
		<c:set var="editDcatSource" value="${editDcatSource}"/>
		
		<input type="hidden" id="inputId" value="${editDcatSource.id}"></input>
	
		<div class="form-group">
			<label for="inputDescription">Description</label> <input type="text"
				class="form-control" id="inputDescription" placeholder="Description" value="${editDcatSource.description}"></input> 
		</div>
		<div class="form-group">
			<label for="inputUrl">URL</label> <input type="text"
				class="form-control" id="inputUrl" placeholder="URL" value="${editDcatSource.url}"></input> 
		</div>
		
		<button class="btn btn-default" type="button" onclick="saveDcatSource();">
			<span class="glyphicon glyphicon-floppy-save" aria-hidden="true">
				Save</span>
		</button>
		
		<a class="btn btn-default" href="${pageContext.request.contextPath}/admin" role="button">Clear</a>
	</div>

	<div class="col-md-9">
		<c:if test="${not empty dcatSources}">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>Id</th>
						<th>Description</th>
						<th>URL</th>
						<th>Time</th>
						<th>Status</th>
						<th>Harvest</th>
						<th>Edit</th>
						<th>Remove</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="dcatSource" items="${dcatSources}">
						<tr>
							<td>${dcatSource.id}</td>
							<td>${dcatSource.description}</td>
							<td>${dcatSource.url}</td>
							<td>
								<c:if test="${dcatSource.getLastHarvest().isPresent()}">
									${dcatSource.getLastHarvest().get().getCreatedDateFormatted()}	
								</c:if>
							</td>
							<td>
								<c:if test="${dcatSource.getLastHarvest().isPresent()}">
									<a href="#" data-toggle="tooltip" title="${dcatSource.getLastHarvest().get().message}">
										${dcatSource.getLastHarvest().get().status.getLocalName()}
									</a>
								</c:if>
							</td>
							<td><a class="btn btn-default" role="button" href="${pageContext.request.contextPath}/admin/harvestDcatSource?id=${dcatSource.id}"> <span
									class="glyphicon glyphicon-cloud-download" aria-hidden="true"></span>
							</a></td>
							<td><a class="btn btn-default" href="${pageContext.request.contextPath}/admin?edit=${dcatSource.id}" role="button"> <span
									class="glyphicon glyphicon-edit" aria-hidden="true"></span>
							</a></td>
							<td><a class="btn btn-default" onclick="deleteDcatSource('${dcatSource.id}');" role="button"> <span
									class="glyphicon glyphicon-remove" aria-hidden="true"></span>
							</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</div>
	
	<script src="${pageContext.request.contextPath}/js/scripts.js" type="text/javascript"></script>
	<script type="text/javascript">
		var saveDcatSource = function () {
			var id = document.getElementById('inputId').value;
			var description = document.getElementById('inputDescription').value;
			var url = document.getElementById('inputUrl').value;
			
			var data = {
				'id': id,
				'description': description,
				'url': url,
				'user': '${username}'
			};

			sendRequest('POST', '${pageContext.request.contextPath}/api/admin/dcat-source', data);
		};

		var deleteDcatSource = function (dcatSourceId) {
			sendRequest('DELETE', '${pageContext.request.contextPath}/api/admin/dcat-source?delete='+dcatSourceId, null);
		};

		clearErrors();
	</script>

</body>
</html>