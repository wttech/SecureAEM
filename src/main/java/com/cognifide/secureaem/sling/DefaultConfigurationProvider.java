package com.cognifide.secureaem.sling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentConfig;
import com.day.cq.replication.AgentManager;

/**
 * This component tries to find the author, publish and dispatcher URLs automatically, during the SecureAEM
 * deployment.
 * 
 */
@Component(immediate = true, enabled = true, metatype = false)
public class DefaultConfigurationProvider {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultConfigurationProvider.class);

	private static final Pattern P_ARGUMENT = Pattern.compile("-p (\\d+)");

	private static final Pattern JAR_FILENAME = Pattern.compile("-.(\\d+)\\.jar");

	private static final String CONFIG_NODE = "/etc/secureaem/jcr:content/globalConfig";

	@Reference
	private AgentManager agentManager;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Activate
	protected void activate() throws LoginException {
		ResourceResolver resolver = resolverFactory.getAdministrativeResourceResolver(null);
		try {
			String currentHost = getCurrentHost();
			String publishHost = getTransportUri(
					agentConfig -> "durbo".equals(agentConfig.getSerializationType()));
			String dispatcher = getTransportUri(
					agentConfig -> "flush".equals(agentConfig.getSerializationType()));
			LOG.info("Discovered author instance URL: " + currentHost);
			LOG.info("Discovered publish instance URL: " + publishHost);
			LOG.info("Discovered dispatcher URL: " + dispatcher);
			Session session = resolver.adaptTo(Session.class);
			if (!session.nodeExists(CONFIG_NODE)) {
				LOG.info("Configuration doesn't exists, saving discovered values");
				Node configNode = JcrUtil.createPath(CONFIG_NODE, JcrConstants.NT_UNSTRUCTURED, session);
				if (StringUtils.isNotBlank(currentHost)) {
					configNode.setProperty("author", currentHost);
				}
				if (StringUtils.isNotBlank(publishHost)) {
					configNode.setProperty("publish", publishHost);
				}
				if (StringUtils.isNotBlank(dispatcher)) {
					configNode.setProperty("dispatcher", dispatcher);
				}
				session.save();
			}
		} catch (RepositoryException e) {
			LOG.error("Can't save default configuration", e);
		} catch (URISyntaxException e) {
			LOG.error("Can't parse publish URL", e);
		} finally {
			resolver.close();
		}
	}

	private String getTransportUri(AgentConfigFilter filter) throws URISyntaxException {
		for (Agent agent : agentManager.getAgents().values()) {
			if (!agent.isEnabled()) {
				continue;
			}
			if (!agent.isValid()) {
				continue;
			}
			AgentConfig agentConfig = agent.getConfiguration();
			if (!filter.matches(agentConfig)) {
				continue;
			}
			String transportUri = agentConfig.getTransportURI();
			if (StringUtils.isNotBlank(transportUri)) {
				URI uri = new URI(transportUri);
				if (!StringUtils.startsWith(uri.getScheme(), "http")) {
					continue;
				}
				return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null)
						.toString();
			}
		}
		return null;
	}

	private String getCurrentHost() {
		String cmd = System.getProperty("sun.java.command");
		String port = null;
		if (StringUtils.isNotBlank(cmd)) {
			Matcher matcher = P_ARGUMENT.matcher(cmd);
			if (matcher.find()) {
				port = matcher.group(1);
			}
			if (StringUtils.isBlank(port)) {
				matcher = JAR_FILENAME.matcher(cmd);
				if (matcher.find()) {
					port = matcher.group(1);
				}
			}
		}
		if (StringUtils.isNotBlank(port)) {
			return "http://localhost:" + port;
		} else {
			return null;
		}
	}

	private interface AgentConfigFilter {
		boolean matches(AgentConfig agentConfig);
	}
}
