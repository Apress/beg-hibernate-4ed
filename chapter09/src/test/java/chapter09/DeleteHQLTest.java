package chapter09;

import chapter09.model.Product;
import chapter09.model.Supplier;
import com.autumncode.hibernate.util.SessionUtil;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class DeleteHQLTest {
    @Test
    public void testDeleteHQL() {
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("delete from Supplier").executeUpdate();
            // create a set of suppliers, then delete them
            Supplier supplier = new Supplier("DSupplier 1");
            session.save(supplier);
            supplier.getProducts().add(
                    new Product(supplier, "foo", "bar", 10.00)
            );

            session.save(new Supplier("DSupplier 2"));
            tx.commit();

            tx = session.beginTransaction();
            Query<Supplier> queryAll = session.createQuery("from Supplier",
                    Supplier.class);
            queryAll.setReadOnly(true);
            List<Supplier> suppliers = queryAll.list();
            for (Supplier s : suppliers) {
                System.out.println(s);
            }
            assertEquals(suppliers.size(), 2);

            session.createQuery("delete from Product").executeUpdate();

            Query deleteAll = session.createQuery("delete from Supplier");
            deleteAll.executeUpdate();
            tx.commit();
            assertEquals(queryAll.list().size(), 0);
        }
    }
}
