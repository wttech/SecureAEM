<%@page contentType="text/html"
            pageEncoding="utf-8"%><%
%><%@include file="/libs/foundation/global.jsp"%>
<p>Following credentials will be checked:</p>
<ul>
<c:forEach var="item" items="${properties.users}">
    <li>
        <div class="li-bullet">
            ${item}
        </div>
    </li>
</c:forEach>
<c:forEach var="item" items="${properties.bundles}">
    <li>
        <div class="li-bullet">
                ${item}
        </div>
     </li>
</c:forEach>
</ul>