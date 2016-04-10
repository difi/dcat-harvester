<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">

<head>
    <title>DCAT Source</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css"
    >
</head>

<body>
<div class="container-fluid">
    <div class="row">
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

            <div>

            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">

            <h3>Harvest History (100 last harvests) -
                <a href="http://demo.difi.no/app/kibana/#/dashboard/dashboard_template?_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-24h,mode:quick,to:now))&_a=(filters:!(),options:(darkTheme:!f),panels:!((col:4,id:Crawler-Operations,panelIndex:1,row:3,size_x:9,size_y:3,type:visualization),(col:1,id:Crawler-Results,panelIndex:2,row:3,size_x:3,size_y:3,type:visualization),(col:1,id:Crawler-Metadata,panelIndex:4,row:1,size_x:12,size_y:2,type:visualization),(col:7,id:Validation-RuleId,panelIndex:6,row:6,size_x:6,size_y:4,type:visualization),(col:1,columns:!(ruleSeverity,event,cause,ruleId),id:validation_ruleId,panelIndex:7,row:6,size_x:6,size_y:4,sort:!(timestamp,desc),type:search)),query:(query_string:(analyze_wildcard:!t,query:'%22${dcatSource.getIdUrlEncoded()}%22')),title:dashboard_template,uiState:())">
                    Se mer i Kibana (link)
                </a></h3>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Status</th>
                    <th>Message</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="harvest" items="${dcatSource.getHarvestedLast100()}">
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
    </div>
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