package chapter08;

import chapter08.model.Supplier;
import com.autumncode.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class QueryTest {
    @BeforeMethod
    public void populateData() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        Supplier supplier = new Supplier("Hardware, Inc.");
        session.save(supplier);

        supplier = new Supplier("Supplier 2");

        session.save(supplier);
        tx.commit();
        session.close();
    }

    @AfterMethod
    public void closeSession() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        session.createQuery("delete from Supplier").executeUpdate();
        tx.commit();
        session.close();
    }

    @Test
    public void testSuppliers() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        Supplier supplier = session.byId(Supplier.class).load(1);
        tx.commit();
        session.close();

        session = SessionUtil.getSession();
        tx = session.beginTransaction();

        supplier = session.byId(Supplier.class).load(1);

        tx.commit();
        session.close();
    }
}
