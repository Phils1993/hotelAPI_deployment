package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.routes.HotelRoutes;
import app.security.SecurityRoutes;
import app.services.Loader;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        Loader.load(emf);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .checkSecurityRoles() // check for role when route is called
                .setRoute(new HotelRoutes().getRoutes())
                .setRoute(new SecurityRoutes().getSecurityRoutes())
                .setRoute(SecurityRoutes.getSecuredRoutes())

                .setRoute(() -> {
                    path("/index", () -> {
                        get("/", ctx -> ctx.render("index.html"));
                    });
                })
                .startServer(7007)
                .setCORS()
                .setGeneralExceptionHandling();



//
    }


}


