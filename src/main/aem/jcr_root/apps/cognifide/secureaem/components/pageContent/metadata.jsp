<%@page contentType="text/html"
            pageEncoding="utf-8"%><%
%><%@include file="/libs/foundation/global.jsp"%>
<p>Following paths will be checked:</p>
<ul>
<c:forEach var="item" items="${properties.paths}">
    <li>
        <div class="li-bullet">
            ${item}
        </div>
    </li>
</c:forEach>
</ul>