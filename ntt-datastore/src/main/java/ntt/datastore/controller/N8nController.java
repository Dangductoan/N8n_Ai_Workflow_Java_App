package ntt.datastore.controller;

import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import ntt.datastore.model.ProjectMemberRequest;
import ntt.datastore.model.TokenRequest;
import ntt.datastore.model.WorkflowShareRequest;
import ntt.datastore.properties.N8nProperties;
import ntt.datastore.service.N8nClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for n8n API operations
 */
@RestController
@RequestMapping("/api/n8n")
@CrossOrigin(origins = "*")
public class N8nController {

    private static final Logger logger = LoggerFactory.getLogger(N8nController.class);

    @Autowired
    N8nProperties n8nProperties;

    N8nClientService n8nClientService;

    @PostConstruct
    public void init() {
        String baseUrl = n8nProperties.getBaseUrl();
        String jwtSecret = n8nProperties.getJwtSecret();
        String apKey = null; //TODO: Refactor this value

        this.n8nClientService = new N8nClientService();
        this.n8nClientService.setBaseUrl(baseUrl);
        this.n8nClientService.setJwtSecret(jwtSecret);
        this.n8nClientService.setApiKey(apKey);
    }
    // ========== Health & Connection ==========

    /**
     * GET /api/n8n/health
     * Check n8n health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            Map<String, Object> health = n8nClientService.healthCheck();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Health check failed: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/test-connection
     * Test connection to n8n
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            Map<String, Object> result = n8nClientService.testConnection();
            boolean connected = (Boolean) result.getOrDefault("connected", false);

            if (connected) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
            }
        } catch (Exception e) {
            logger.error("Connection test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Connection test failed: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/test-connection/async
     * Test connection to n8n (async)
     */
    @GetMapping("/test-connection/async")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> testConnectionAsync(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer projectId) {

        return n8nClientService.testConnectionAsync(userId, projectId, null)
                .thenApply(connected -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("connected", connected);
                    result.put("message", connected ? "Connected successfully" : "Connection failed");
                    return ResponseEntity.ok(result);
                })
                .exceptionally(e -> {
                    logger.error("Async connection test failed", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(createErrorResponse("Async connection test failed: " + e.getMessage()));
                });
    }

    /**
     * GET /api/n8n/test-database
     * Test direct database connection to n8n
     */
    @GetMapping("/test-database")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        try {
            Map<String, Object> result = n8nClientService.testDatabaseConnection();
            boolean connected = (Boolean) result.getOrDefault("connected", false);

            if (connected) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
            }
        } catch (Exception e) {
            logger.error("Database connection test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Database connection test failed: " + e.getMessage()));
        }
    }

    // ========== JWT Token Management ==========

    /**
     * POST /api/n8n/token/generate
     * Generate JWT token for n8n authentication
     */
    @PostMapping("/token/generate")
    public ResponseEntity<Map<String, Object>> generateToken(@RequestBody TokenRequest request) {
        try {
            String token = n8nClientService.generateJwtToken(
                    request.getUserId(),
                    request.getProjectId(),
                    request.getPermissions()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", request.getUserId());
            response.put("projectId", request.getProjectId());
            response.put("permissions", request.getPermissions());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Token generation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Token generation failed: " + e.getMessage()));
        }
    }

    /**
     * POST /api/n8n/token/verify
     * Verify JWT token from n8n
     */
    @PostMapping("/token/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Token is required"));
            }

            Claims claims = n8nClientService.verifyJwtToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("claims", claims);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Token verification failed", e);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // ========== User Management ==========

    /**
     * POST /api/n8n/users
     * Create user in n8n
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> result = n8nClientService.createUser(userData);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create user: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/users/{userId}
     * Get user from n8n
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        try {
            Map<String, Object> user = n8nClientService.getUser(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Failed to get user", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Failed to get user: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/n8n/users/{userId}
     * Update user in n8n
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String userId,
            @RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> result = n8nClientService.updateUser(userId, userData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to update user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update user: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/n8n/users/{userId}
     * Delete user from n8n
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        try {
            Map<String, Object> result = n8nClientService.deleteUser(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to delete user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete user: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/users
     * List all users in n8n
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> listUsers() {
        try {
            List<Map<String, Object>> users = n8nClientService.listUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Failed to list users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== Project Management ==========

    /**
     * POST /api/n8n/projects
     * Create project in n8n
     */
    @PostMapping("/projects")
    public ResponseEntity<Map<String, Object>> createProject(@RequestBody Map<String, Object> projectData) {
        try {
            Map<String, Object> result = n8nClientService.createProject(projectData);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            logger.error("Failed to create project", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create project: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/projects/{projectId}
     * Get project from n8n
     */
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Map<String, Object>> getProject(@PathVariable String projectId) {
        try {
            Map<String, Object> project = n8nClientService.getProject(projectId);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            logger.error("Failed to get project", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Failed to get project: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/n8n/projects/{projectId}
     * Update project in n8n
     */
    @PutMapping("/projects/{projectId}")
    public ResponseEntity<Map<String, Object>> updateProject(
            @PathVariable String projectId,
            @RequestBody Map<String, Object> projectData) {
        try {
            Map<String, Object> result = n8nClientService.updateProject(projectId, projectData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to update project", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update project: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/n8n/projects/{projectId}
     * Delete project from n8n
     */
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Map<String, Object>> deleteProject(@PathVariable String projectId) {
        try {
            Map<String, Object> result = n8nClientService.deleteProject(projectId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to delete project", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete project: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/projects
     * List all projects in n8n
     */
    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> listProjects() {
        try {
            List<Map<String, Object>> projects = n8nClientService.listProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            logger.error("Failed to list projects", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== Project Members ==========

    /**
     * POST /api/n8n/projects/{projectId}/members
     * Add member to n8n project
     */
    @PostMapping("/projects/{projectId}/members")
    public ResponseEntity<Map<String, Object>> addProjectMember(
            @PathVariable String projectId,
            @RequestBody ProjectMemberRequest request) {
        try {
            Map<String, Object> result = n8nClientService.addProjectMember(
                    projectId,
                    request.getUserId(),
                    request.getRole()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            logger.error("Failed to add project member", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to add project member: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/n8n/projects/{projectId}/members/{userId}
     * Remove member from n8n project
     */
    @DeleteMapping("/projects/{projectId}/members/{userId}")
    public ResponseEntity<Map<String, Object>> removeProjectMember(
            @PathVariable String projectId,
            @PathVariable String userId) {
        try {
            Map<String, Object> result = n8nClientService.removeProjectMember(projectId, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to remove project member", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to remove project member: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/projects/{projectId}/members
     * List project members in n8n
     */
    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<Map<String, Object>>> listProjectMembers(@PathVariable String projectId) {
        try {
            List<Map<String, Object>> members = n8nClientService.listProjectMembers(projectId);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            logger.error("Failed to list project members", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== Workflow Management ==========

    /**
     * POST /api/n8n/workflows
     * Create workflow in n8n
     */
    @PostMapping("/workflows")
    public ResponseEntity<Map<String, Object>> createWorkflow(@RequestBody Map<String, Object> workflowData) {
        try {
            Map<String, Object> result = n8nClientService.createWorkflow(workflowData);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            logger.error("Failed to create workflow", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create workflow: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/workflows/{workflowId}
     * Get workflow from n8n
     */
    @GetMapping("/workflows/{workflowId}")
    public ResponseEntity<Map<String, Object>> getWorkflow(@PathVariable String workflowId) {
        try {
            Map<String, Object> workflow = n8nClientService.getWorkflow(workflowId);
            return ResponseEntity.ok(workflow);
        } catch (Exception e) {
            logger.error("Failed to get workflow", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Failed to get workflow: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/n8n/workflows/{workflowId}
     * Update workflow in n8n
     */
    @PutMapping("/workflows/{workflowId}")
    public ResponseEntity<Map<String, Object>> updateWorkflow(
            @PathVariable String workflowId,
            @RequestBody Map<String, Object> workflowData) {
        try {
            Map<String, Object> result = n8nClientService.updateWorkflow(workflowId, workflowData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to update workflow", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update workflow: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/n8n/workflows/{workflowId}
     * Delete workflow from n8n
     */
    @DeleteMapping("/workflows/{workflowId}")
    public ResponseEntity<Map<String, Object>> deleteWorkflow(@PathVariable String workflowId) {
        try {
            Map<String, Object> result = n8nClientService.deleteWorkflow(workflowId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to delete workflow", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete workflow: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/workflows
     * List workflows in n8n
     */
    @GetMapping("/workflows")
    public ResponseEntity<List<Map<String, Object>>> listWorkflows(
            @RequestParam(required = false) String projectId) {
        try {
            List<Map<String, Object>> workflows = n8nClientService.listWorkflows(projectId);
            return ResponseEntity.ok(workflows);
        } catch (Exception e) {
            logger.error("Failed to list workflows", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET /api/n8n/workflows/async
     * Get workflows (async)
     */
    @GetMapping("/workflows/async")
    public CompletableFuture<ResponseEntity<List<Map<String, Object>>>> getWorkflowsAsync(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String projectId) {

        return n8nClientService.getWorkflowsAsync(userId, projectId, null)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> {
                    logger.error("Failed to get workflows async", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                });
    }

    /**
     * POST /api/n8n/workflows/{workflowId}/activate
     * Activate workflow in n8n
     */
    @PostMapping("/workflows/{workflowId}/activate")
    public ResponseEntity<Map<String, Object>> activateWorkflow(@PathVariable String workflowId) {
        try {
            Map<String, Object> result = n8nClientService.activateWorkflow(workflowId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to activate workflow", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to activate workflow: " + e.getMessage()));
        }
    }

    /**
     * POST /api/n8n/workflows/{workflowId}/deactivate
     * Deactivate workflow in n8n
     */
    @PostMapping("/workflows/{workflowId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateWorkflow(@PathVariable String workflowId) {
        try {
            Map<String, Object> result = n8nClientService.deactivateWorkflow(workflowId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to deactivate workflow", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to deactivate workflow: " + e.getMessage()));
        }
    }

    /**
     * POST /api/n8n/workflows/{workflowId}/toggle
     * Toggle workflow status in n8n
     */
    @PostMapping("/workflows/{workflowId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleWorkflowStatus(
            @PathVariable String workflowId,
            @RequestParam boolean active) {
        try {
            Map<String, Object> result = n8nClientService.toggleWorkflowStatus(workflowId, active);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to toggle workflow status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to toggle workflow status: " + e.getMessage()));
        }
    }

    // ========== Workflow Sharing ==========

    /**
     * POST /api/n8n/workflows/{workflowId}/share
     * Share workflow with user in n8n
     */
    @PostMapping("/workflows/{workflowId}/share")
    public ResponseEntity<Map<String, Object>> shareWorkflow(
            @PathVariable String workflowId,
            @RequestBody WorkflowShareRequest request) {
        try {
            Map<String, Object> result = n8nClientService.shareWorkflow(
                    workflowId,
                    request.getUserId(),
                    request.getPermission()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            logger.error("Failed to share workflow", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to share workflow: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/n8n/workflows/{workflowId}/share/{userId}
     * Unshare workflow with user in n8n
     */
    @DeleteMapping("/workflows/{workflowId}/share/{userId}")
    public ResponseEntity<Map<String, Object>> unshareWorkflow(
            @PathVariable String workflowId,
            @PathVariable String userId) {
        try {
            Map<String, Object> result = n8nClientService.unshareWorkflow(workflowId, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to unshare workflow", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to unshare workflow: " + e.getMessage()));
        }
    }

    /**
     * GET /api/n8n/workflows/{workflowId}/shares
     * List workflow shares in n8n
     */
    @GetMapping("/workflows/{workflowId}/shares")
    public ResponseEntity<List<Map<String, Object>>> listWorkflowShares(@PathVariable String workflowId) {
        try {
            List<Map<String, Object>> shares = n8nClientService.listWorkflowShares(workflowId);
            return ResponseEntity.ok(shares);
        } catch (Exception e) {
            logger.error("Failed to list workflow shares", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== Helper Methods ==========

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}