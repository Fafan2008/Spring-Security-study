package com.ivo.my.models.entities;

import com.ivo.my.models.entities.User;
import com.ivo.my.utils.validations.PasswordMatches;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "verification_token")
@Builder
@Data
@AllArgsConstructor
public class VerificationToken {

    private static final int EXPIRATION = 60 * 24;

    public VerificationToken() {
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

    static private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime()
                .getTime());
    }
}
