package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.CliConfiguration;
import com.cognifide.secureaem.HttpHelper;
import com.cognifide.secureaem.TestConfiguration;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;
import org.apache.sling.commons.json.JSONObject;

import java.io.IOException;

/**
 * Look on the adobe website for the latest service pack and compares it with the installed service pack.
 */
public class LatestSecurityHotfixTest extends AbstractTest implements AuthorTest, OsgiConfigurationTest {
    String AEM_SERVICE_PACK_OVERVIEW_PAGE = "https://experienceleague.adobe.com/docs/experience-manager-release-information/aem-release-updates/aem-releases-updates.html?lang=en#aem-on-prem-managed-services";

    public LatestSecurityHotfixTest(CliConfiguration config, TestConfiguration testConfiguration) {
        super(config, testConfiguration);
    }

    @Override
    public boolean doTest(String url, String instanceName) throws Exception {
        String configurationEndpoint = url
                + "/system/console/status-productinfo.json";
        String body = getJsonBodyOfOsgiConfiguration(configurationEndpoint, getUsernamePasswordCredentials(instanceName), instanceName);
        String currentVersion = getCurrentVersion(body);
        boolean latestVersionEqualsCurrentVersion = latestVersionEqualsCurrentVersion(currentVersion);
        if(latestVersionEqualsCurrentVersion) {
            addInfoMessage("[%s] has latest Service Pack installed (%s)", instanceName, currentVersion);
            return true;
        } else {
            addErrorMessage("[%s] doesn't have the latest Service Pack installed (%s)", instanceName, currentVersion);
            return false;
        }
    }

    public String getCurrentVersion(String body){
        String currentVersion = "";
        String[] lines = body.split("\\n");
        for(int i = 0; i < lines.length; i++) {
            if(lines[i].contains("Installed Products") && lines[i+1].contains("Adobe Experience Manager (")){
                currentVersion = lines[i+1].substring(lines[i+1].indexOf("(")+1,lines[i+1].indexOf(")"));
                break;
            }
        }
        return currentVersion;
    }

    public boolean latestVersionEqualsCurrentVersion(String currentVersion) throws IOException {
        boolean latestVersionEqualsCurrentVersion = false;
        String[] currentAEMVersion = currentVersion.split("\\.");
        if(currentAEMVersion.length == 4) {
            String stringToFind = "Experience Manager " + currentAEMVersion[0] + "." + currentAEMVersion[1] + " Service Pack " + currentAEMVersion[2] + " Cumulative Fix Pack " + currentAEMVersion[3];
            latestVersionEqualsCurrentVersion = httpHelper.pageContainsString(AEM_SERVICE_PACK_OVERVIEW_PAGE, stringToFind);
        } else {
            addErrorMessage("Current AEM version doesn't have the expected length");
        }
        return latestVersionEqualsCurrentVersion;
    }
}
