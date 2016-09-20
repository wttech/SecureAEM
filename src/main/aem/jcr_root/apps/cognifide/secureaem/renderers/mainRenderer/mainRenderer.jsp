<%@page contentType="text/html"
            pageEncoding="utf-8"
            import="java.util.Iterator"
            import="com.day.cq.wcm.api.components.IncludeOptions" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
    String pageTitle = properties.get("jcr:title", "Secure AEM");
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <title><%= pageTitle %></title>
    <meta http-equiv="Content-Type" content="text/html; utf-8" />
    <cq:includeClientLib categories="cq.wcm.edit,cognifide.secureaem"/>
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
</head>
<body>
    <h1><%= pageTitle %></h1>
    <div>
    <br/>
    <button type="button" id="secureaem-export">Export as txt</button>
    <script type="text/javascript">
        CQ.WCM.edit({
            "path":"<%= resource.getPath() %>/globalConfig",
            "dialog":"/apps/cognifide/secureaem/renderers/mainRenderer/dialog",
            "type":"cognifide/secureaem/renderers/mainRenderer",
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
    </div>
    <%
    	for (Iterator<Page> iter = currentPage.listChildren(); iter.hasNext();) {
                Page child = iter.next();
                String id = child.getTitle();
                String title = child.getTitle();
                
                ValueMap content = child.getProperties();
                String testComponentPath = child.getPath() + "/jcr:content/testComponent";
                IncludeOptions.getOptions(request, true).forceSameContext(Boolean.TRUE);
    %>
              <sling:include path="<%= testComponentPath %>" />
            <%
        }
    %>
</body>
</html>