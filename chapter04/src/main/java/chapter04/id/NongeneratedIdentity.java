package chapter04.id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class NongeneratedIdentity {
    @Id
    Long id;
    @Column
    String value;

    public NongeneratedIdentity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
