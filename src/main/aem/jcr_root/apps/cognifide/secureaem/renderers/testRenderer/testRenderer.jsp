<%@page contentType="text/html"
            pageEncoding="utf-8"
            import="java.util.Iterator"
            import="com.day.cq.wcm.api.components.IncludeOptions" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
    String pageTitle = properties.get("jcr:title", "Test page");
	String dialog = "/apps/cognifide/secureaem/renderers/testRenderer/dialog";
	String testComponentType = null;
	boolean metadataExists = false;
	if (resource != null && resource.getChild("testComponent") != null) {
		Resource testComponent = resource.getChild("testComponent");
		testComponentType = testComponent.getResourceType();
		Resource dialogRes = resourceResolver.getResource(testComponentType + "/dialog");
		if (dialogRes != null) {
			dialog = dialogRes.getPath();
		}
	}
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <title><%= pageTitle %></title>
    <meta http-equiv="Content-Type" content="text/html; utf-8" />
    <cq:includeClientLib categories="cq.wcm.edit,cognifide.secureaem"/>
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
</head>
<body>
    <div id="header"><a href="/etc/secureaem.html" class="home"></a></div>
    <h1><%= pageTitle %></h1>
    <div>
    <br/>
    <script type="text/javascript">
        CQ.WCM.edit({
            "path":"<%= resource.getPath() %>",
            "dialog":"<%= dialog %>",
            "type":"cognifide/secureaem/renderers/testRenderer",
            "editConfig":{
                "xtype":"editbar",
                "listeners":{
                    "afteredit":"REFRESH_PAGE"
                },
                "inlineEditing":CQ.wcm.EditBase.INLINE_MODE_NEVER,
                "disableTargeting":true,
                "actions":[
                    {
                        "xtype":"tbtext",
                        "text":"Settings"
                    },
                    CQ.wcm.EditBase.EDIT
                ]
            }
        });
    </script>
        <% IncludeOptions.getOptions(request, true).forceSameContext(Boolean.TRUE); %>
        <sling:include path="testComponent" />
        <h2 class="no-margin">
            Test component: <%= testComponentType %>
        </h2>
        <% IncludeOptions.getOptions(request, true).forceSameContext(Boolean.TRUE); %>
        <sling:include path="testComponent.metadata" />
    </div>
</body>
</html>
