package chapter11;

import chapter11.model.User;
import com.autumncode.hibernate.util.SessionUtil;
import org.hibernate.query.Query;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class FilterTests {
    @BeforeMethod
    public void setupTest() {
        SessionUtil.doWithSession((session) -> {
            User user = new User("user1", true);
            user.addGroups("group1", "group2");
            session.save(user);
            user = new User("user2", true);
            user.addGroups("group2", "group3");
            session.save(user);
            user = new User("user3", false);
            user.addGroups("group3", "group4");
            session.save(user);
            user = new User("user4", true);
            user.addGroups("group4", "group5");
            session.save(user);
        });
    }

    @AfterMethod
    public void endTest() {
        SessionUtil.doWithSession((session) -> {
            session.disableFilter("byGroup");
            session.disableFilter("byStatus");

            // need to manually delete all of the Users since
            // HQL delete doesn't cascade over element collections
            Query<User> query = session.createQuery("from User", User.class);
            for (User user : query.list()) {
                session.delete(user);
            }
        });
    }

    @Test
    public void testSimpleQuery() {
        SessionUtil.doWithSession((session) -> {
            Query<User> query = session.createQuery("from User", User.class);
            List<User> users = query.list();
            assertEquals(users.size(), 4);
        });
    }

    @Test
    public void testActivesFilter() {
        SessionUtil.doWithSession((session) -> {
            Query<User> query = session.createQuery("from User", User.class);
            session.enableFilter("byStatus").setParameter("status", Boolean.TRUE);
            List<User> users = query.list();
            assertEquals(users.size(), 3);
        });
    }

    @Test
    public void testInactivesFilter() {
        SessionUtil.doWithSession((session) -> {
            Query<User> query = session.createQuery("from User", User.class);

            session.enableFilter("byStatus").setParameter("status", Boolean.FALSE);
            List<User> users = query.list();
            assertEquals(users.size(), 1);
        });
    }

    @Test
    public void testGroupFilter() {
        SessionUtil.doWithSession((session) -> {
            Query<User> query = session.createQuery("from User", User.class);

            session.enableFilter("byGroup").setParameter("group", "group4");
            List<User> users = query.list();
            assertEquals(users.size(), 2);
            session.enableFilter("byGroup").setParameter("group", "group1");
            users = (List<User>) query.list();
            assertEquals(users.size(), 1);
            // should be user 1
            assertEquals(users.get(0).getName(), "user1");
        });
    }

    @Test
    public void testBothFilters() {
        SessionUtil.doWithSession((session) -> {
            Query<User> query = session.createQuery("from User", User.class);

            session.enableFilter("byGroup").setParameter("group", "group4");
            session.enableFilter("byStatus").setParameter("status", Boolean.TRUE);
            List<User> users = query.list();
            assertEquals(users.size(), 1);
            assertEquals(users.get(0).getName(), "user4");
        });
    }

    @Test
    public void testNoParameterFilter() {
        SessionUtil.doWithSession((session) -> {
            Query<User> query = session.createQuery("from User", User.class);

            session.enableFilter("userEndsWith1");
            List<User> users = query.list();
            assertEquals(users.size(), 1);
            assertEquals(users.get(0).getName(), "user1");
        });
    }
}

