package app.security;

import app.entities.Role;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;


import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class SecurityRoutes {
    private final SecurityController securityController = new SecurityController();
    private static final ObjectMapper jsonMapper = new Utils().getObjectMapper();

    public EndpointGroup getSecurityRoutes () {
        return () -> {
            path("auth", () -> {
                post("login", securityController.login());
                get("healthcheck", securityController::healthCheck, Roles.ANYONE);
            });
        };
    }

    public static EndpointGroup getSecuredRoutes(){
        return () -> {
            path("/protected", ()->{
                get("/user_demo",(ctx)->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from USER Protected")),Roles.USER);
                get("/admin_demo",(ctx)->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from ADMIN Protected")),Roles.ADMIN);
            });
        };
    }

}
