package chapter04.id;

import javax.persistence.*;

@Entity
public class GeneratedIdentityIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String value;

    public GeneratedIdentityIdentity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
