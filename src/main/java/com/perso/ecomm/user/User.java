package com.perso.ecomm.user;


import com.perso.ecomm.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity(name = "users")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UniqueEmailAndUsername",
                        columnNames = {"email", "username"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private Role role;

    private String imageUrl;


    public User(
            String email,
            String password,
            String fullName,
            String username,
            String imageUrl) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
    }
}
