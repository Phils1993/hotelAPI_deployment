package app.security;

import app.config.HibernateConfig;
import app.entities.Role;
import app.entities.User;
import app.exceptions.EntityNotFoundException;
import app.exceptions.ValidationException;

public class Main {
    public static void main(String[] args) {
        // TODO: Make this more clean: maybe a loader?

        // FIXME Der skal laves en user først med create. Ændre til update

        ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());

        User user = dao.createUser("Philip TEST", "pass12345");
        System.out.println(user.getUserName()+": "+user.getPassword());
        Role role = dao.createRole("User");

        try {
            User updatedUser = dao.addUserRole("Philip TEST", "User");
            System.out.println(updatedUser);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        try {
            User validatedUser = dao.getVerifiedUser("Philip TEST", "pass12345");
            System.out.println("User was validated: "+validatedUser.getUserName());
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
