/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-14
 * Description : Create N8nClientService: (Python original name: N8nClient)
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.datastore.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Getter
@Setter
public class N8nClientService implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(N8nClientService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String baseUrl;
    private String jwtSecret;
    private String apiKey;
    private HttpClient httpClient;

    /**
     * Create HTTP client with SSL verification disabled and 30s timeout
     */
    private HttpClient createHttpClient() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, new SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HTTP client", e);
        }
    }

    /**
     * Get headers for API requests
     */
    private Map<String, String> getHeaders(boolean includeAuth) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        if (includeAuth && apiKey != null) {
            headers.put("X-N8N-API-KEY", apiKey);
        }

        return headers;
    }

    /**
     * Make HTTP request to n8n API
     */
    private Map<String, Object> makeRequest(String method, String endpoint,
                                            Map<String, Object> data,
                                            Map<String, String> params) throws Exception {
        String url = this.baseUrl + "/api/v1" + endpoint;

        try {
            // Build URI with query parameters
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                params.forEach(uriBuilder::addParameter);
            }

            // Build request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(uriBuilder.build())
                    .timeout(Duration.ofSeconds(30));

            // Add headers
            Map<String, String> headers = getHeaders(true);
            headers.forEach(requestBuilder::header);

            // Set method and body
            HttpRequest.BodyPublisher bodyPublisher = data != null
                    ? HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(data))
                    : HttpRequest.BodyPublishers.noBody();

            switch (method.toUpperCase()) {
                case "GET":
                    requestBuilder.GET();
                    break;
                case "POST":
                    requestBuilder.POST(bodyPublisher);
                    break;
                case "PUT":
                    requestBuilder.PUT(bodyPublisher);
                    break;
                case "DELETE":
                    requestBuilder.DELETE();
                    break;
                case "PATCH":
                    requestBuilder.method("PATCH", bodyPublisher);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            HttpRequest request = requestBuilder.build();

            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check status code
            if (response.statusCode() >= 400) {
                logger.error("n8n API error: {} - {}", response.statusCode(), response.body());
                throw new HttpException("HTTP " + response.statusCode() + ": " + response.body());
            }

            // Parse JSON response
            return objectMapper.readValue(response.body(),
                    new TypeReference<Map<String, Object>>() {});

        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("n8n API request failed: {}", e.getMessage());
            throw e;
        }
    }

    // Overloaded convenience methods
    private Map<String, Object> makeRequest(String method, String endpoint) throws Exception {
        return makeRequest(method, endpoint, null, null);
    }

    private Map<String, Object> makeRequest(String method, String endpoint, Map<String, Object> data) throws Exception {
        return makeRequest(method, endpoint, data, null);
    }

    /**
     * Generate JWT token for n8n authentication
     */
    public String generateJwtToken(String userId, String projectId, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("projectId", projectId);
        claims.put("permissions", permissions != null ? permissions : Arrays.asList("read"));

        Date expiration = Date.from(Instant.now().plus(24, ChronoUnit.HOURS));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    /**
     * Verify JWT token from n8n
     */
    public Claims verifyJwtToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    // ========== User Management ==========

    /**
     * Create user in n8n
     */
    public Map<String, Object> createUser(Map<String, Object> userData) throws Exception {
        try {
            // n8n API expects an array for user creation
            List<Map<String, Object>> userDataArray = Arrays.asList(userData);
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("users", userDataArray);
            return makeRequest("POST", "/users", requestData);
        } catch (Exception e) {
            if (e.getMessage().contains("Connection refused") || e.getMessage().contains("Failed to connect")) {
                return handleN8nNotReady("create_user");
            }
            throw e;
        }
    }

    /**
     * Get user from n8n
     */
    public Map<String, Object> getUser(String userId) throws Exception {
        return makeRequest("GET", "/users/" + userId);
    }

    /**
     * Update user in n8n
     */
    public Map<String, Object> updateUser(String userId, Map<String, Object> userData) throws Exception {
        // n8n API expects an array for user updates
        List<Map<String, Object>> userDataArray = Arrays.asList(userData);
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("users", userDataArray);
        return makeRequest("PUT", "/users/" + userId, requestData);
    }

    /**
     * Delete user from n8n
     */
    public Map<String, Object> deleteUser(String userId) throws Exception {
        return makeRequest("DELETE", "/users/" + userId);
    }

    /**
     * List all users in n8n
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listUsers() throws Exception {
        Map<String, Object> response = makeRequest("GET", "/users");
        return (List<Map<String, Object>>) response.getOrDefault("data", new ArrayList<>());
    }

    // ========== Project Management ==========

    /**
     * Create project in n8n
     */
    public Map<String, Object> createProject(Map<String, Object> projectData) throws Exception {
        return makeRequest("POST", "/projects", projectData);
    }

    /**
     * Get project from n8n
     */
    public Map<String, Object> getProject(String projectId) throws Exception {
        return makeRequest("GET", "/projects/" + projectId);
    }

    /**
     * Update project in n8n
     */
    public Map<String, Object> updateProject(String projectId, Map<String, Object> projectData) throws Exception {
        return makeRequest("PUT", "/projects/" + projectId, projectData);
    }

    /**
     * Delete project from n8n
     */
    public Map<String, Object> deleteProject(String projectId) throws Exception {
        return makeRequest("DELETE", "/projects/" + projectId);
    }

    /**
     * List all projects in n8n
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listProjects() throws Exception {
        Map<String, Object> response = makeRequest("GET", "/projects");
        return (List<Map<String, Object>>) response.getOrDefault("data", new ArrayList<>());
    }

    // ========== Project Members ==========

    /**
     * Add member to n8n project
     */
    public Map<String, Object> addProjectMember(String projectId, String userId, String role) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("role", role != null ? role : "project:member");
        return makeRequest("POST", "/projects/" + projectId + "/members", data);
    }

    /**
     * Remove member from n8n project
     */
    public Map<String, Object> removeProjectMember(String projectId, String userId) throws Exception {
        return makeRequest("DELETE", "/projects/" + projectId + "/members/" + userId);
    }

    /**
     * List project members in n8n
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listProjectMembers(String projectId) throws Exception {
        Map<String, Object> response = makeRequest("GET", "/projects/" + projectId + "/members");
        return (List<Map<String, Object>>) response.getOrDefault("data", new ArrayList<>());
    }

    // ========== Workflow Management ==========

    /**
     * Create workflow in n8n
     */
    public Map<String, Object> createWorkflow(Map<String, Object> workflowData) throws Exception {
        return makeRequest("POST", "/workflows", workflowData);
    }

    /**
     * Get workflow from n8n
     */
    public Map<String, Object> getWorkflow(String workflowId) throws Exception {
        return makeRequest("GET", "/workflows/" + workflowId);
    }

    /**
     * Update workflow in n8n
     */
    public Map<String, Object> updateWorkflow(String workflowId, Map<String, Object> workflowData) throws Exception {
        return makeRequest("PUT", "/workflows/" + workflowId, workflowData);
    }

    /**
     * Delete workflow from n8n
     */
    public Map<String, Object> deleteWorkflow(String workflowId) throws Exception {
        return makeRequest("DELETE", "/workflows/" + workflowId);
    }

    /**
     * List workflows in n8n
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listWorkflows(String projectId) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (projectId != null) {
            params.put("projectId", projectId);
        }
        Map<String, Object> response = makeRequest("GET", "/workflows", null, params);
        return (List<Map<String, Object>>) response.getOrDefault("data", new ArrayList<>());
    }

    /**
     * Activate workflow in n8n
     */
    public Map<String, Object> activateWorkflow(String workflowId) throws Exception {
        return makeRequest("POST", "/workflows/" + workflowId + "/activate");
    }

    /**
     * Deactivate workflow in n8n
     */
    public Map<String, Object> deactivateWorkflow(String workflowId) throws Exception {
        return makeRequest("POST", "/workflows/" + workflowId + "/deactivate");
    }

    /**
     * Toggle workflow status in n8n
     */
    public Map<String, Object> toggleWorkflowStatus(String workflowId, boolean active) throws Exception {
        return active ? activateWorkflow(workflowId) : deactivateWorkflow(workflowId);
    }

    // ========== Workflow Sharing ==========

    /**
     * Share workflow with user in n8n
     */
    public Map<String, Object> shareWorkflow(String workflowId, String userId, String permission) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("permission", permission != null ? permission : "read");
        return makeRequest("POST", "/workflows/" + workflowId + "/share", data);
    }

    /**
     * Unshare workflow with user in n8n
     */
    public Map<String, Object> unshareWorkflow(String workflowId, String userId) throws Exception {
        return makeRequest("DELETE", "/workflows/" + workflowId + "/share/" + userId);
    }

    /**
     * List workflow shares in n8n
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listWorkflowShares(String workflowId) throws Exception {
        Map<String, Object> response = makeRequest("GET", "/workflows/" + workflowId + "/shares");
        return (List<Map<String, Object>>) response.getOrDefault("data", new ArrayList<>());
    }

    // ========== Health Check ==========

    /**
     * Check n8n health status
     */
    public Map<String, Object> healthCheck() {
        try {
            String url = this.baseUrl + "/healthz";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            Map<String, String> headers = getHeaders(false);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET();
            headers.forEach(builder::header);

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            Map<String, Object> result = new HashMap<>();
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                try {
                    Map<String, Object> data = objectMapper.readValue(response.body(),
                            new TypeReference<Map<String, Object>>() {});
                    result.put("status", "healthy");
                    result.put("data", data);
                } catch (Exception e) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("content", response.body());
                    result.put("status", "healthy");
                    result.put("data", data);
                }
            } else {
                result.put("status", "unhealthy");
                result.put("error", "HTTP " + response.statusCode());
            }
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "unhealthy");
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * Test connection to n8n (synchronous)
     */
    public Map<String, Object> testConnection() {
        try {
            Map<String, Object> health = healthCheck();
            Map<String, Object> result = new HashMap<>();

            if ("healthy".equals(health.get("status"))) {
                result.put("connected", true);
                result.put("message", "Successfully connected to n8n");
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) health.get("data");
                result.put("version", data != null ? data.getOrDefault("version", "unknown") : "unknown");
            } else {
                result.put("connected", false);
                result.put("message", "n8n health check failed: " + health.getOrDefault("error", "Unknown error"));
            }
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("connected", false);
            result.put("message", "Connection test failed: " + e.getMessage());
            return result;
        }
    }

    /**
     * Test connection (async wrapper)
     */
    public CompletableFuture<Boolean> testConnectionAsync(Integer userId, Integer projectId, Object db) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> result = testConnection();
                return (Boolean) result.getOrDefault("connected", false);
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Get workflows (async wrapper)
     */
    public CompletableFuture<List<Map<String, Object>>> getWorkflowsAsync(Integer userId, String projectId, Object db) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return listWorkflows(projectId);
            } catch (Exception e) {
                logger.error("Failed to get workflows: {}", e.getMessage());
                return new ArrayList<>();
            }
        });
    }

    /**
     * Handle case when n8n is not ready
     */
    private Map<String, Object> handleN8nNotReady(String operation) {
        logger.warn("n8n not ready for {}. This is normal during startup.", operation);
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", "n8n service not ready");
        result.put("message", "n8n service is starting up. Please try again later for " + operation + ".");
        return result;
    }

    /**
     * Test direct database connection to n8n
     */
    public Map<String, Object> testDatabaseConnection() {
        String n8nDbHost = System.getenv().getOrDefault("N8N_DB_HOST", "n8n-db");
        String n8nDbPort = System.getenv().getOrDefault("N8N_DB_PORT", "5432");
        String n8nDbName = System.getenv().getOrDefault("N8N_DB_NAME", "n8n");
        String n8nDbUser = System.getenv().getOrDefault("N8N_DB_USER", "n8n");
        String n8nDbPassword = System.getenv().getOrDefault("N8N_DB_PASSWORD", "n8n_password");

        Map<String, Object> result = new HashMap<>();
        result.put("host", n8nDbHost);
        result.put("port", n8nDbPort);
        result.put("database", n8nDbName);

        try {
            String url = String.format("jdbc:postgresql://%s:%s/%s", n8nDbHost, n8nDbPort, n8nDbName);
            Properties props = new Properties();
            props.setProperty("user", n8nDbUser);
            props.setProperty("password", n8nDbPassword);
            props.setProperty("connectTimeout", "5");

            try (Connection conn = DriverManager.getConnection(url, props);
                 PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    result.put("connected", true);
                    result.put("message", "Successfully connected to n8n PostgreSQL database");
                }
            }
        } catch (Exception e) {
            result.put("connected", false);
            result.put("message", "Failed to connect to n8n database: " + e.getMessage());
        }

        return result;
    }

    // ========== Configuration Helpers ==========

    private String getN8nBaseUrl() {
        // Replace with your actual configuration retrieval logic
        return System.getenv().getOrDefault("N8N_BASE_URL", "http://localhost:5678");
    }

    private String getN8nJwtSecret() {
        // Replace with your actual configuration retrieval logic
        return System.getenv().getOrDefault("N8N_JWT_SECRET", "");
    }

    private String getN8nApiKey() {
        // Replace with your actual configuration retrieval logic
        return System.getenv().getOrDefault("N8N_API_KEY", "");
    }

    @Override
    public void close() {
        // HttpClient doesn't need explicit closing in Java 11+
        // but this method is here for AutoCloseable compatibility
    }

    // Custom HTTP Exception class
    public static class HttpException extends Exception {
        public HttpException(String message) {
            super(message);
        }
    }
}