package app.entities;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "roles")
@EqualsAndHashCode
public class Role {

    @Id
    @Column(name = "role_name", nullable = false)
    private String role;


    @ManyToMany(mappedBy = "roles")
    private Set<User> users;


    public Role(String role) {
        this.role = role;
    }

    public Role() {
    }
}
