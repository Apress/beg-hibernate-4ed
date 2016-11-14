package chapter07.lifecycle;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@EntityListeners({UserAccountListener.class})
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    Integer id;
    @Getter
    @Setter
    String name;
    @Getter
    @Setter
    @Transient
    String password;
    @Getter
    @Setter
    Integer salt;
    @Getter
    @Setter
    Integer passwordHash;

    public boolean validPassword(String newPass) {
        return newPass.hashCode() * salt == getPasswordHash();
    }
}
