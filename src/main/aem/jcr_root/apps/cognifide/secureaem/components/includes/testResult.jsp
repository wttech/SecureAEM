<%@page contentType="text/html" pageEncoding="utf-8" %>
<% ValueMap testMetadata = resource.getParent().adaptTo(ValueMap.class); %>
<h2 class="no-margin">
<div class="secureaem-test-${testResult}">
</div>
  <a href="<%= resource.getParent().getParent().getPath() %>.html"><%= testMetadata.get("jcr:title") %></a>
</h2>
<p><%= testMetadata.get("jcr:description") %></p>
<ul>
  <% if(testMetadata.get("urlDesc") != null) { %>
  <li>
    <div class="li-bullet">
      More info: <a href="<%= testMetadata.get("url") %>"><%= testMetadata.get("urlDesc") %></a>
    </div>
  </li>
  <% } %>
  <li>
    <div class="li-bullet secureaem-<%= testMetadata.get("severity", "").toLowerCase() %>">
      Severity: <strong><%= testMetadata.get("severity") %></strong>
    </div>
  </li>
  <c:if test="${not empty messages}">
  <li><strong>Following errors occured:</strong></li>
  <c:forEach var="message" items="${messages}">
    <li>
      <div class="li-bullet">
      ${message}
      </div>
    </li>
  </c:forEach>
</c:if>
</ul>
