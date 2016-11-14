package chapter06.twotables;

import javax.persistence.*;

@Entity
@Table(
        name = "customer",
        uniqueConstraints = {@UniqueConstraint(columnNames = "name")}
)
@SecondaryTable(name = "customer_details")
public class Customer {
    @Id
    public int id;
    public String name;
    @Column(table = "customer_details")
    public String address;

    public Customer() {
    }
}
