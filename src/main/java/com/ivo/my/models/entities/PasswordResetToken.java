package com.ivo.my.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "password_reset_token")
@Builder
@Data
@AllArgsConstructor
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    public PasswordResetToken() {
        super();
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Builder.Default
    private Date expiryDate = calculateExpiryDate(EXPIRATION);

    public Boolean isExpired() {
        final Calendar cal = Calendar.getInstance();
        return expiryDate.getTime() - cal.getTime().getTime() <= 0;
    }

    private static Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime()
                .getTime());
    }
}
