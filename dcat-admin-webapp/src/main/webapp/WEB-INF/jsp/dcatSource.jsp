<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">

<head>
    <title>DCAT Source</title>
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
          crossorigin="anonymous">
</head>

<body>
<div class="col-md-12">


    <h1>DCAT Source</h1>
    <c:if test="${dcatSource != null}">

        <table class="table table-striped">
            <thead>
            <tr>
                <th>Id</th>
                <th>Description</th>
                <th>URL</th>
            </tr>
            </thead>
            <tbody>
            <td>${dcatSource.id}</td>
            <td>${dcatSource.description}</td>
            <td>${dcatSource.url}</td>
            </tbody>
        </table>

        <h3>Harvest History</h3>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Date</th>
                <th>Status</th>
                <th>Message</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="harvest" items="${dcatSource.getHarvestedSorted()}">
                <tr>
                    <td style="min-width: 120px">${harvest.getCreatedDateFormatted()}</td>
                    <td>${harvest.status.getLocalName()}</td>
                    <td>${harvest.getMessageOrEmpty()}</td>
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
        sendRequest('DELETE', '${pageContext.request.contextPath}/api/admin/dcat-source?delete=' + dcatSourceId, null);
    };

    clearErrors();
</script>

</body>
</html>