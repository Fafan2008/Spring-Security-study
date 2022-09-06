package com.ivo.my.models.entities;

import com.ivo.my.utils.validations.PasswordMatches;
import com.ivo.my.utils.validations.ValidPassword;
import lombok.*;
import org.jboss.aerogear.security.otp.api.Base32;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Date;


@Entity
@Table(name = "user")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Login name is required.")
    String loginName;
    String firstName;
    String secondName;
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Email is required.")
    String email;
    @NotEmpty(message = "Password is required.")
    @ValidPassword
    String password;
    @Transient
    @NotEmpty(message = "Password confirmation is required.")
    String passwordConfirmation;
    @Column(nullable = false)
    @NotEmpty(message = "Secret is required.")
    private String secret =  Base32.random();;
    @Column(nullable = false)
    Date createdAt = new Date();
    @Column
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;
}
