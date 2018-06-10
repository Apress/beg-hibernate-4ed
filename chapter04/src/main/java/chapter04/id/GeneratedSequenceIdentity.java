package chapter04.id;

import javax.persistence.*;

@Entity
public class GeneratedSequenceIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    @Column
    String value;

    public GeneratedSequenceIdentity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
