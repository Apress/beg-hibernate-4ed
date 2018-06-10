package chapter04.id;

import javax.persistence.*;

@Entity
public class GeneratedAutoIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column
    String value;

    public GeneratedAutoIdentity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
