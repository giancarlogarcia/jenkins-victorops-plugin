package org.jenkinsci.plugins.victorops;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import net.sf.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import hudson.ProxyConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class StandardVictorOpsService implements VictorOpsService {

    private static final Logger logger = Logger.getLogger(StandardVictorOpsService.class.getName());

    private String serverUrl;
    private String apiKey;
    private String routingKey;

    public StandardVictorOpsService(String serverUrl, String apiKey, String routingKey) {
        super();
        this.serverUrl = serverUrl;
        this.apiKey = apiKey;
        this.routingKey = routingKey;
    }

    public boolean publish(String status, String message){
        String incident = "unset incident";
        return publish(status, message, incident);
    }

    public boolean publish(String status, String message, String incident){
        String integrationPath = "integrations/generic/20131114/alert";
        String url = "https://" + serverUrl + integrationPath + '/' + apiKey + '/' + routingKey;
        logger.info("Posting '" + status + "' status to " + url);
        HttpClient client = getHttpClient();
        PostMethod post = new PostMethod(url);
        JSONObject json = new JSONObject();
        try {
            json.put("message_type", status);
            json.put("state_message", message);
            json.put("entity_id", incident);
            String requestBody = json.toString();
            logger.info("Posting request: " + requestBody);
            StringRequestEntity requestEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            post.setRequestEntity(requestEntity);
            int responseCode = client.executeMethod(post);
            String response = post.getResponseBodyAsString();
            if(responseCode != HttpStatus.SC_OK) {
                logger.log(Level.WARNING, "VictorOps post may have failed. Response: " + response);
                return false;
            }
            logger.info("VictorOps post succeeded. Response: " + response);
            return true;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error posting to VictorOps", e);
            return false;
        } finally {
            post.releaseConnection();
        }
    }

    private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        if (Jenkins.getInstance() != null) {
            ProxyConfiguration proxy = Jenkins.getInstance().proxy;
            if (proxy != null) {
                client.getHostConfiguration().setProxy(proxy.name, proxy.port);
                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?
                if (username != null && !"".equals(username.trim())) {
                    logger.info("Using proxy authentication (user=" + username + ")");
                    // http://hc.apache.org/httpclient-3.x/authentication.html#Proxy_Authentication
                    // and
                    // http://svn.apache.org/viewvc/httpcomponents/oac.hc3x/trunk/src/examples/BasicAuthenticationExample.java?view=markup
                    client.getState().setProxyCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                }
            }
        }
        return client;
    }

}