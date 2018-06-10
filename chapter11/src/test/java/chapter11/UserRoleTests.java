package chapter11;

import chapter11.userrole.Role1;
import chapter11.userrole.User1;
import com.autumncode.hibernate.util.SessionUtil;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserRoleTests {
    Map<String, Integer> roleMap = new HashMap<>();

    @BeforeTest
    void setupRoles() {
        SessionUtil.doWithSession(session -> {
            String[] names = {"role1", "role2", "role3"};
            for (String name : names) {
                Role1 role1 = new Role1(name);
                session.save(role1);
                roleMap.put(name, role1.getId());
            }
        });
    }

    @Test
    void testUserWithRole() {
        SessionUtil.doWithSession(session -> {
            User1 user = new User1("foo", true);
            Set<Role1> roles = new HashSet<>();
            roles.add(session.byId(Role1.class).load(roleMap.get("role1")));
            user.setRoles(roles);
            session.save(user);
        });

        SessionUtil.doWithSession(session -> {
            System.out.println(session.createQuery("from User1 u").list());
        });
    }
}
