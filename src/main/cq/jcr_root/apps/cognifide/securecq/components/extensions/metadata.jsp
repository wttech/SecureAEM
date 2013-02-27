<%@page contentType="text/html"
            pageEncoding="utf-8"%><%
%><%@include file="/libs/foundation/global.jsp"%>
<p>Following extensions will be checked:</p>
<ul>
<c:forEach var="item" items="${properties.extensions}">
    <li>
        <div class="li-bullet">
            ${item}
        </div>
    </li>
</c:forEach>
</ul>