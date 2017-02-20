package chapter12;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;

@Entity
@Indexed
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Field(name = "person_id")
    Integer id;
    @Field(analyze = Analyze.NO)
    @Column
    String name;
    @Field(analyze = Analyze.NO)
    @Column
    Integer balance;

    public Person() {
    }

    public Person(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (getBalance() != null ? !balance.equals(person.getBalance()) : person.getBalance() != null) return false;
        if (getId() != null ? !getId().equals(person.getId()) : person.getId() != null) return false;
        if (getName() != null ? !getName().equals(person.getName()) : person.getName() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getBalance() != null ? getBalance().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Person{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", balance=").append(balance);
        sb.append('}');
        return sb.toString();
    }
}
