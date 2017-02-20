package chapter07.lifecycle;

import com.autumncode.jpa.util.JPASessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class LifecycleTest {
    @Test
    public void testLifecycle() {
        Integer id;
        LifecycleThing thing1, thing2, thing3;
        try (Session session = JPASessionUtil.getSession("chapter07")) {
            Transaction tx = session.beginTransaction();
            thing1 = new LifecycleThing();
            thing1.setName("Thing 1");

            session.save(thing1);
            id = thing1.getId();
            System.out.println(thing1);
            tx.commit();
        }

        try (Session session = JPASessionUtil.getSession("chapter07")) {
            Transaction tx = session.beginTransaction();
            thing2 = session
                    .byId(LifecycleThing.class)
                    .load(-1);
            assertNull(thing2);

            Reporter.log("attempted to load nonexistent reference");

            thing2 = session.byId(LifecycleThing.class)
                    .getReference(id);
            assertNotNull(thing2);
            assertEquals(thing1, thing2);

            thing2.setName("Thing 2");

            tx.commit();
        }
        try (Session session = JPASessionUtil.getSession("chapter07")) {
            Transaction tx = session.beginTransaction();

            thing3 = session
                    .byId(LifecycleThing.class)
                    .getReference(id);
            assertNotNull(thing3);
            assertEquals(thing2, thing3);

            session.delete(thing3);

            tx.commit();
        }
        assertEquals(LifecycleThing.lifecycleCalls.nextClearBit(0), 7);
    }

    @Test
    public void persistenceErrorTests() {
        testPersistence("value1", FailingEntity.FailureStatus.NOFAILURE);
        testPersistence("value2", FailingEntity.FailureStatus.PREPERSIST);
        testPersistence("value3", FailingEntity.FailureStatus.POSTPERSIST);
    }

    @Test
    public void prepersistException() {
        Session session = JPASessionUtil.getSession("chapter07");

        Transaction tx = session.beginTransaction();
        session.createQuery("delete from FailingEntity").executeUpdate();
        tx.commit();

        tx = session.beginTransaction();
        FailingEntity fe = new FailingEntity();
        fe.setValue("FailingEntity");
        fe.setFailureStatus(FailingEntity.FailureStatus.PREPERSIST);
        try {
            session.persist(fe);
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
        tx.rollback();
        session.close();

        session = JPASessionUtil.getSession("chapter07");
        tx = session.beginTransaction();
        List e = session.createQuery("from FailingEntity fe").list();
        System.out.println(e);
        tx.commit();
        session.close();
    }

    public void testPersistence(String value,
                                FailingEntity.FailureStatus status) {
        Session session = JPASessionUtil.getSession("chapter07");
        Transaction tx = session.beginTransaction();

        FailingEntity failingEntity = new FailingEntity();
        failingEntity.setValue(value);
        failingEntity.setFailureStatus(status);
        try {
            session.persist(failingEntity);
            Integer id = failingEntity.getId();
            tx.commit();
            session.close();
            System.out.println(failingEntity);
            session = JPASessionUtil.getSession("chapter07");
            tx = session.beginTransaction();
            FailingEntity fe = session.load(FailingEntity.class, id);
            assertEquals(fe, failingEntity);
            session.delete(fe);
            tx.commit();
            session.close();
        } catch (RuntimeException exception) {
            if (FailingEntity.FailureStatus.NOFAILURE.equals(status)) {
                throw exception;
            }
            tx.rollback();
            session.close();
        }
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "postload failure")
    public void testPostLoad() {
        Integer id;
        Session session = JPASessionUtil.getSession("chapter07");
        Transaction tx = session.beginTransaction();

        FailingEntity failingEntity = new FailingEntity();
        failingEntity.setValue("postload");
        failingEntity.setFailureStatus(FailingEntity.FailureStatus.POSTLOAD);

        session.persist(failingEntity);
        tx.commit();
        session.close();

        session = JPASessionUtil.getSession("chapter07");
        tx = session.beginTransaction();
        FailingEntity e = session
                .byId(FailingEntity.class)
                .load(failingEntity.getId());
        System.out.println(e);
        tx.commit();
        session.close();
    }
}
