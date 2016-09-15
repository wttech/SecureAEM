<%@page contentType="text/html" pageEncoding="utf-8"
  import="org.apache.sling.api.resource.ValueMap"
%><%@include file="/libs/foundation/global.jsp"
%><% ValueMap testMetadata = resource.getParent().adaptTo(ValueMap.class); %>
<div class="test disabled" data-url="${resource.path}.json" data-hide-passed="${properties.hidePassed}">
<h2 class="no-margin">
  <div class="secureaem-test-icon icon-loading"></div>
  <a href="<%= resource.getParent().getParent().getPath() %>.html"><%= testMetadata.get("jcr:title") %></a>
</h2>
<p><%= testMetadata.get("jcr:description") %></p>
<ul>
  <% if(testMetadata.get("urlDesc") != null) { %>
  <li>
    More info: <a href="<%= testMetadata.get("url") %>"><%= testMetadata.get("urlDesc") %></a>
  </li>
  <% } %>
  <li>
    Severity: <strong><%= testMetadata.get("severity") %></strong>
  </li>
</ul>
</div>
