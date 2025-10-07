package app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import app.exceptions.ApiException;
import app.security.ISecurityController;
import app.security.SecurityController;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class ApplicationConfig {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static ApplicationConfig appConfig;
    private static JavalinConfig javalinConfig;
    private static Javalin app;
    private static final ISecurityController securityController = new SecurityController();

    private ApplicationConfig() {}

    public static ApplicationConfig getInstance() {
        if (appConfig == null) {
            appConfig = new ApplicationConfig();
        }
        return appConfig;
    }

    public ApplicationConfig initiateServer() {
        app = Javalin.create(config -> {
            javalinConfig = config;
            config.bundledPlugins.enableDevLogging(); // Ny måde
            config.staticFiles.add("/public");
            config.http.defaultContentType = "application/json";
            config.router.contextPath = "/api/v1";
            config.router.apiBuilder(() -> {}); // optional placeholder
            config.bundledPlugins.enableRouteOverview("/routes"); // Ny måde
        });
        return appConfig;
    }

    public ApplicationConfig setRoute(EndpointGroup route) {
        javalinConfig.router.apiBuilder(() -> {
            path("/", route);
        });
        return appConfig;
    }

    public ApplicationConfig setCORS() {
        app.before(ApplicationConfig::setCorsHeaders);
        app.options("/*", ApplicationConfig::setCorsHeaders);
        return appConfig;
    }

    private static void setCorsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    public ApplicationConfig checkSecurityRoles() {
        app.beforeMatched(securityController.authenticate());
        app.beforeMatched(securityController.authorize());
        return appConfig;
    }

    public ApplicationConfig startServer(int port) {
        app.start(port);
        return appConfig;
    }

    public ApplicationConfig stopServer() {
        app.stop();
        return appConfig;
    }

    public ApplicationConfig setGeneralExceptionHandling() {
        app.exception(Exception.class, (e, ctx) -> {
            int statusCode = (e instanceof ApiException apiEx) ? apiEx.getStatusCode() : 500;
            String message = (e instanceof ApiException) ? e.getMessage() : "Internal server error";

            logger.error("An exception occurred", e);

            ObjectNode on = jsonMapper
                    .createObjectNode()
                    .put("status", statusCode)
                    .put("msg", message);

            ctx.json(on);
            ctx.status(statusCode);
        });
        return appConfig;
    }

    public ApplicationConfig beforeFilter() {
        app.before(ctx -> {
            String pathInfo = ctx.req().getPathInfo();
            ctx.req().getHeaderNames().asIterator().forEachRemaining(System.out::println);
        });
        return appConfig;
    }
}
