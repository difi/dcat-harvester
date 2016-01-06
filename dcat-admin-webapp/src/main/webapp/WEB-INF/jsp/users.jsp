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
			href="${pageContext.request.contextPath}/login?logout" role="button">
			Log out </a>
	</p>

	<c:if test="${not empty users}">
		<table class="table table-striped">
			<thead>
				<tr>
					<th>Username</th>
					<th>Email</th>
					<th>Role</th>
					<th>Edit</th>
					<th>Remove</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="user" items="${users}">
					<tr>
						<td>${user.username}</td>
						<td>${user.email}</td>
						<td>${user.role}</td>
						<td><a class="btn btn-default" href="/" role="button"> <span
								class="glyphicon glyphicon-edit" aria-hidden="true"></span>
						</a></td>
						<td><a class="btn btn-default" onclick="deleteUser('${user.username}');" role="button"> <span
								class="glyphicon glyphicon-remove" aria-hidden="true"></span>
						</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

	<h1>New User</h1>

	<div class="form-group">
		<label for="inputUsername">Username</label> <input type="text"
			class="form-control" id="inputUsername" placeholder="Username">
	</div>
	<div class="form-group">
		<label for="inputPassword">Password</label> <input type="password"
			class="form-control" id="inputPassword" placeholder="Password">
	</div>
	<div class="form-group">
		<label for="inputEmail">Email</label> <input type="email"
			class="form-control" id="inputEmail" placeholder="Email">
	</div>
	<div class="form-group">
		<label for="inputRole">Role</label> <input type="email"
			class="form-control" id="inputRole" placeholder="Role">
	</div>

	<button class="btn btn-default" type="button" onclick="saveUser();">
		<span class="glyphicon glyphicon-floppy-save" aria-hidden="true">
			Save</span>
	</button>

	<script type="text/javascript">
		var saveUser = function () {

			var username = document.getElementById('inputUsername').value;
			var password = document.getElementById('inputPassword').value;
			var email = document.getElementById('inputEmail').value;
			var role = document.getElementById('inputRole').value;
			
			var data = {
				'username': username,
				'password': password,
				'email': email,
				'role': role
			};

			if (!username || !password || !email || !role) {
				console.log("Empty values not allowed", data);
				return;
			}
			
			var request = new XMLHttpRequest();
			request.open('POST', '/api/admin/user', true);
			request.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
			request.send(JSON.stringify(data));
		};

		var deleteUser = function (username) {

			var request = new XMLHttpRequest();
			request.open('DELETE', '/api/admin/user?delete='+username, true);
			request.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
			request.send();
			
		};
	</script>

</body>
</html>