package chapter04.id;

import com.autumncode.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import javax.persistence.PersistenceException;

public class IdentityTest {
    @Test
    public void testAutoIdentity() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        GeneratedAutoIdentity obj = new GeneratedAutoIdentity();

        session.persist(obj);

        tx.commit();
        session.close();

        System.out.println(obj.getId());
    }

    @Test(expectedExceptions = PersistenceException.class)
    public void testNongeneratedIdentityFailure() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        NongeneratedIdentity obj = new NongeneratedIdentity();

        session.persist(obj);

        tx.commit();
        session.close();

        System.out.println(obj.getId());
    }

    @Test
    public void testNongeneratedIdentity() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        NongeneratedIdentity obj = new NongeneratedIdentity();
        obj.setId(1l);
        session.persist(obj);

        tx.commit();
        session.close();

        System.out.println(obj.getId());
    }

    @Test
    public void testSequenceIdentity() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        GeneratedSequenceIdentity obj = new GeneratedSequenceIdentity();
        session.persist(obj);

        tx.commit();
        session.close();

        System.out.println(obj.getId());
    }

    @Test
    public void testTableIdentity() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        GeneratedTableIdentity obj = new GeneratedTableIdentity();
        session.persist(obj);

        tx.commit();
        session.close();

        System.out.println(obj.getId());
    }

    @Test
    public void testIdentityIdentity() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        GeneratedIdentityIdentity obj = new GeneratedIdentityIdentity();
        session.persist(obj);

        tx.commit();
        session.close();

        System.out.println(obj.getId());
    }
}
