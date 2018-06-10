package chapter07.lifecycle;

import javax.persistence.PrePersist;

public class UserAccountListener {
    @PrePersist
    void setPasswordHash(Object o) {
        UserAccount ua = (UserAccount) o;
        if (ua.getSalt() == null || ua.getSalt() == 0) {
            ua.setSalt((int) (Math.random() * 65535));
        }
        ua.setPasswordHash(
                ua.getPassword().hashCode() * ua.getSalt()
        );
    }
}
