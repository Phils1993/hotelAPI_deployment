package app.security;


import io.javalin.http.Context;
import io.javalin.http.Handler;
import dk.bugelhartmann.UserDTO;

public interface ISecurityController {
    Handler login();

    Handler login(Context ctx); // to get a token

    Handler register(Context ctx); // to get a user

    Handler authenticate(); // to verify roles inside token

    Handler authorize();


    String createToken(UserDTO user) throws Exception;


}
