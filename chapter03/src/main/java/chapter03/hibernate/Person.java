package chapter03.hibernate;

import javax.persistence.*;

@Entity
public class Person {
    @Column(unique = true)
    String name;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Person() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
