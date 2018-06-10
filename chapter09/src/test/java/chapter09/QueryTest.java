package chapter09;

import chapter09.model.Product;
import chapter09.model.Software;
import chapter09.model.Supplier;
import com.autumncode.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class QueryTest {
    Session session;
    Transaction tx;

    @BeforeMethod
    public void populateData() {
        try(Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();

            Supplier supplier = new Supplier("Hardware, Inc.");
            supplier.getProducts().add(
                    new Product(supplier, "Optical Wheel Mouse", "Mouse", 5.00));
            supplier.getProducts().add(
                    new Product(supplier, "Trackball Mouse", "Mouse", 22.00));
            session.save(supplier);

            supplier = new Supplier("Supplier 2");
            supplier.getProducts().add(
                    new Software(supplier, "SuperDetect", "Antivirus", 14.95, "1.0"));
            supplier.getProducts().add(
                    new Software(supplier, "Wildcat", "Browser", 19.95, "2.2"));
            supplier.getProducts().add(
                    new Product(supplier, "AxeGrinder", "Gaming Mouse", 42.00));

            session.save(supplier);
            tx.commit();
        }

        this.session = SessionUtil.getSession();
        this.tx = this.session.beginTransaction();
    }

    @AfterMethod
    public void closeSession() {
        session.createQuery("delete from Product").executeUpdate();
        session.createQuery("delete from Supplier").executeUpdate();
        if (tx.isActive()) {
            tx.commit();
        }
        if (session.isOpen()) {
            session.close();
        }
    }

    @Test
    public void testNamedQuery() {
        Query<Supplier> query = session.getNamedQuery("supplier.findAll");
        List<Supplier> suppliers = query.list();
        assertEquals(suppliers.size(), 2);
    }
    @Test
    public void testJoin() {
        Query<Object[]> query = session.getNamedQuery("product.findProductAndSupplier");
        List<Object[]> suppliers = query.list();
        for(Object[] o:suppliers) {
            Assert.assertTrue(o[0] instanceof Product);
            Assert.assertTrue(o[1] instanceof Supplier);
        }
        assertEquals(suppliers.size(), 5);
    }
    @Test
    public void testSimpleQuery() {
        Query<Product> query = session.createQuery("from Product", Product.class);

        query.setComment("This is only a query for product");
        List<Product> products = query.list();
        assertEquals(products.size(), 5);
    }

    @Test
    public void testSimpleProjection() {
        Query<String> query = session.createQuery("select p.name from Product p", String.class);
        List<String> suppliers = query.list();
        for(String s:suppliers) {
            System.out.println(s);
        }
        assertEquals(suppliers.size(), 5);
    }

    @Test
    public void testProjection() {
        Query query = session.getNamedQuery("supplier.findAverage");
        List<Object[]> suppliers = query.list();
        for (Object[] o : suppliers) {
            System.out.println(Arrays.toString(o));
        }
        assertEquals(suppliers.size(), 2);
    }

    @Test
    public void testLikeQuery() {
        Query<Product> query = session.createQuery(
                "from Product p "
                        + " where p.price > :minprice"
                        + " and p.description like :desc", Product.class);
        query.setParameter("desc", "Mou%");
        query.setParameter("minprice", 15.0);
        List<Product> products = query.list();
        assertEquals(products.size(), 1);
    }

    @DataProvider
    Object[][] queryTypesProvider() {
        return new Object[][]{
                {"Supplier", 2},
                {"Product", 5},
                {"Software", 2},
        };
    }

    @Test(dataProvider = "queryTypesProvider")
    public void testQueryTypes(String type, Integer count) {
        Query query = session.createQuery("from " + type);
        List list = query.list();
        assertEquals(list.size(), count.intValue());
    }

    @Test
    public void searchForProduct() {
        Query query = session.getNamedQuery("product.searchByPhrase");
        query.setParameter("text", "%Mou%");
        List<Product> products = query.list();
        assertEquals(products.size(), 3);
    }
}
