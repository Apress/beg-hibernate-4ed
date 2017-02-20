package chapter11.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@FilterDefs({
        @FilterDef(name = "byStatus", parameters = @ParamDef(name = "status", type = "boolean")),
        @FilterDef(name = "byGroup", parameters = @ParamDef(name = "group", type = "string")),
        @FilterDef(name = "userEndsWith1")
})
@Filters({
        @Filter(name = "byStatus", condition = "active = :status"),
        @Filter(name = "byGroup", condition = ":group in ( select ug.groups from user_groups ug where ug.user_id = id)"),
        @Filter(name = "userEndsWith1", condition = "name like '%1'")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @Column(unique = true)
    String name;
    boolean active;
    @ElementCollection
    Set<String> groups;

    public User(String name, boolean active) {
        this.name=name;
        this.active=active;
    }

    public void addGroups(String... groupSet) {
        if (getGroups() == null) {
            setGroups(new HashSet<>());
        }
        getGroups().addAll(Arrays.asList(groupSet));

    }
}
