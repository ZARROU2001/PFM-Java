package com.perso.ecomm.user;


import com.perso.ecomm.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Entity(name = "users")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UniqueEmailAndUsername",
                        columnNames = {"email", "username"}
                )
        }
)
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false)
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private Role role;

    private String imageUrl;

    public User(
            String email,
            String password,
            String firstName,
            String lastName,
            String username,
            String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
    }

}
