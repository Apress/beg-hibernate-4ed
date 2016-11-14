package chapter11;

import chapter11.model.User;
import com.redhat.osas.hibernate.util.SessionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class FilterTests {
    Session session;
    Transaction tx;
    Query query;
    List<User> users;

    @BeforeMethod
    public void setupTest() {
        session = SessionUtil.getSession();
        tx = session.beginTransaction();
        User user = User.builder().name("user1").active(true).build();
        user.addGroups("group1", "group2");
        session.save(user);
        user = User.builder().name("user2").active(true).build();
        user.addGroups("group2", "group3");
        session.save(user);
        user = User.builder().name("user3").active(false).build();
        user.addGroups("group3", "group4");
        session.save(user);
        user = User.builder().name("user4").active(true).build();
        user.addGroups("group4", "group5");
        session.save(user);
        tx.commit();
        session.close();

        session = SessionUtil.getSession();
        tx = session.beginTransaction();
        query = session.createQuery("from User");
    }

    @AfterMethod
    public void endTest() {
        if (session.isOpen()) {
            if (tx.isActive()) {
                tx.commit();
            }
            tx = session.beginTransaction();
            session.disableFilter("byGroup");
            session.disableFilter("byStatus");
            session.createSQLQuery("DELETE FROM USER_GROUPS").executeUpdate();
            session.createSQLQuery("DELETE FROM USER").executeUpdate();
            tx.commit();
            session.close();
        }
        session = null;
        tx = null;
        query = null;
        users = null;
    }

    @Test
    public void testSimpleQuery() {
        users = (List<User>) query.list();
        assertEquals(users.size(), 4);
    }

    @Test
    public void testActivesFilter() {
        session.enableFilter("byStatus").setParameter("status", Boolean.TRUE);
        users = (List<User>) query.list();
        assertEquals(users.size(), 3);
    }

    @Test
    public void testInactivesFilter() {
        session.enableFilter("byStatus").setParameter("status", Boolean.FALSE);
        users = (List<User>) query.list();
        assertEquals(users.size(), 1);
    }

    @Test
    public void testGroupFilter() {
        session.enableFilter("byGroup").setParameter("group", "group4");
        users = (List<User>) query.list();
        assertEquals(users.size(), 2);
        session.enableFilter("byGroup").setParameter("group", "group1");
        users = (List<User>) query.list();
        assertEquals(users.size(), 1);
        // should be user 1
        assertEquals(users.get(0).getName(), "user1");
    }

    @Test
    public void testBothFilters() {
        session.enableFilter("byGroup").setParameter("group", "group4");
        session.enableFilter("byStatus").setParameter("status", Boolean.TRUE);
        List<User> users = query.list();
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getName(), "user4");
    }

    @Test
    public void testNoParameterFilter() {
        session.enableFilter("userEndsWith1");
        List<User> users = query.list();
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getName(), "user1");
    }
}

