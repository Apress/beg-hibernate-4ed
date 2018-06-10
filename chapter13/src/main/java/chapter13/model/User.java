package chapter13.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @Audited
    @Column(unique = true)
    String name;
    @Audited
    boolean active;
    //@Audited
    @ElementCollection
    Set<String> groups;
    @Audited
    String description;

    public User(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    public void addGroups(String... groupSet) {
        if (getGroups() == null) {
            setGroups(new HashSet<>());
        }
        getGroups().addAll(Arrays.asList(groupSet));

    }
}
