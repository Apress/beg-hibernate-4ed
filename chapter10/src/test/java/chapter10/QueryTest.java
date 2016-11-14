package chapter10;

import chapter10.model.Product;
import chapter10.model.Software;
import chapter10.model.Supplier;
import com.redhat.osas.hibernate.util.SessionUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class QueryTest {
    Session session;
    Transaction tx;

    @BeforeMethod
    public void populateData() {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        Supplier supplier = new Supplier("Hardware, Inc.");
        supplier.getProducts().add(
                new Product(supplier, "Optical Wheel Mouse", "Mouse", 5.00));
        supplier.getProducts().add(
                new Product(supplier, "Trackball Mouse", "Mouse", 22.00));
        session.save(supplier);

        supplier = new Supplier("Hardware Are We");
        supplier.getProducts().add(
                new Software(supplier, "SuperDetect", "Antivirus", 14.95, "1.0"));
        supplier.getProducts().add(
                new Software(supplier, "Wildcat", "Browser", 19.95, "2.2"));
        supplier.getProducts().add(
                new Product(supplier, "AxeGrinder", "Gaming Mouse", 42.00));

        session.save(supplier);
        tx.commit();
        session.close();

        this.session = SessionUtil.getSession();
        this.tx = this.session.beginTransaction();
    }

    @AfterMethod
    public void closeSession() {
        session.createQuery("delete from Software").executeUpdate();
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
    public void testSimpleCriteriaQuery() {
        Criteria crit = session.createCriteria(Product.class);
        assertEquals(crit.list().size(), 5);
    }

    @Test
    public void testSimpleEQQuery() {
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.eq("description", "Mouse"));
        assertEquals(crit.list().size(), 2);
    }

    @Test
    public void testSimpleNEQuery() {
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.ne("description", "Mouse"));
        assertEquals(crit.list().size(), 3);
    }

    @Test
    public void testSimpleILikeQuery() {
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.ilike("description", "mou%"));
        assertEquals(crit.list().size(), 2);
    }

    @Test
    public void testILikeMatchModeQuery() {
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.ilike("description", "ser", MatchMode.END));
        assertEquals(crit.list().size(), 1);
    }

    @Test
    public void testSimpleLikeQuery() {
        // should return none, based on case
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.like("description", "mou%"));
        assertEquals(crit.list().size(), 0);

        // should return two since case matches
        crit = session.createCriteria(Product.class);
        crit.add(Restrictions.like("description", "Mou%"));
        assertEquals(crit.list().size(), 2);
    }

    @Test
    public void testGTLTQuery() {
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.gt("price", 25.0));
        assertEquals(crit.list().size(), 1);

        crit = session.createCriteria(Product.class);
        crit.add(Restrictions.lt("price", 25.0));
        assertEquals(crit.list().size(), 4);
    }

    @Test
    public void testANDQuery() {
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.lt("price", 10.0));
        crit.add(Restrictions.ilike("description", "mouse", MatchMode.ANYWHERE));
        assertEquals(crit.list().size(), 1);
    }

    @Test
    public void testORQuery() {
        Criteria crit = session.createCriteria(Product.class);
        Criterion priceLessThan = Restrictions.lt("price", 10.0);
        Criterion mouse = Restrictions.ilike("description", "mouse", MatchMode.ANYWHERE);
        LogicalExpression orExp = Restrictions.or(priceLessThan, mouse);
        crit.add(orExp);
        assertEquals(crit.list().size(), 3);
    }

    @Test
    public void testORQueryPlusGaming() {
        Criteria crit = session.createCriteria(Product.class);
        Criterion priceLessThan = Restrictions.gt("price", 20.0);
        Criterion mouse = Restrictions.ilike("description", "mouse", MatchMode.ANYWHERE);
        LogicalExpression orExp = Restrictions.or(priceLessThan, mouse);
        crit.add(orExp);
        crit.add(Restrictions.ilike("description", "gaming", MatchMode.ANYWHERE));
        assertEquals(crit.list().size(), 1);
    }

    @Test
    public void testSQLRestrictionQuery() {
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.sqlRestriction("{alias}.description like 'Mou%'"));
        assertEquals(crit.list().size(), 2);
    }

    @Test
    public void testDisjunctionQuery() {
        Criteria crit = session.createCriteria(Product.class);
        Criterion priceLessThan = Restrictions.lt("price", 10.0);
        Criterion mouse = Restrictions.ilike("description", "mouse", MatchMode.ANYWHERE);
        Criterion browser = Restrictions.ilike("description", "browser", MatchMode.ANYWHERE);
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(priceLessThan);
        disjunction.add(mouse);
        disjunction.add(browser);
        crit.add(disjunction);
        assertEquals(crit.list().size(), 4);
    }

    @Test
    public void testOrderedQuery() {
        Criteria crit = session.createCriteria(Product.class);
        crit.add(Restrictions.gt("price", 10.0));
        crit.addOrder(Order.desc("price"));
        List<Product> results = crit.list();
        assertEquals(results.size(), 4);
        double current = results.get(0).getPrice();
        for (Product p : results) {
            if (p.getPrice() > current) {
                fail("Product list not ordered in descending order\n" +
                        "Product: " + p + "\nPrevious price: " + current);
            }
            current = p.getPrice();
        }
    }

    @Test
    public void testCriteriaAssociation() {
        Criteria crit = session.createCriteria(Supplier.class);
        Criteria prodCrit = crit.createCriteria("products");
        prodCrit.add(Restrictions.gt("price", 25.0));
        assertEquals(crit.list().size(), 1);
    }

    @Test
    public void testOwnerAssociation() {
        Criteria crit = session.createCriteria(Product.class);
        Criteria supplierCrit = crit.createCriteria("supplier");
        supplierCrit.add(Restrictions.eq("name", "Hardware Are We"));
        assertEquals(crit.list().size(), 3);
    }

    @Test
    public void testProjectionRowCount() {
        Criteria crit = session.createCriteria(Product.class);
        crit.setProjection(Projections.rowCount());
        List<Long> results = crit.list();
        assertEquals(results.size(), 1);
        assertEquals(results.get(0).intValue(), 5);
    }

    @Test
    public void testProjectionAggregates() {
        Criteria crit = session.createCriteria(Product.class);
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.max("price"));
        projList.add(Projections.min("price"));
        projList.add(Projections.avg("price"));
        projList.add(Projections.countDistinct("description"));
        crit.setProjection(projList);
        List<Object[]> results = crit.list();

        assertEquals(results.size(), 1);
        assertEquals(42.0, (Double) results.get(0)[0]);
        assertEquals(5.0, (Double) results.get(0)[1]);
        assertEquals(20.78, (Double) results.get(0)[2]);
        assertEquals(4, ((Long) results.get(0)[3]).longValue());
    }

    @Test
    public void testProjectionGroupBy() {
        Criteria crit = session.createCriteria(Product.class);
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("name"));
        projList.add(Projections.groupProperty("price"));
        crit.setProjection(projList);
        crit.addOrder(Order.asc("price"));
        List<Object[]> results = crit.list();
        assertEquals(results.size(), 5);
        for (Object[] p : results) {
            System.out.printf("%-25s $%6.2f%n",
                    p[0].toString(), (Double) p[1]);
        }
    }

    @Test
    public void testQBE() {
        Criteria crit = session.createCriteria(Supplier.class);
        Supplier supplier = new Supplier("Hardware Are We");
        crit.add(Example.create(supplier));
        List<Supplier> results = crit.list();
        assertEquals(results.size(), 1);
    }

    //@Test
    public void testJoin() {
        Query query = session.getNamedQuery("product.findProductAndSupplier");
        List<Object[]> suppliers = query.list();
        for (Object[] o : suppliers) {
            Assert.assertTrue(o[0] instanceof Product);
            Assert.assertTrue(o[1] instanceof Supplier);
        }
        assertEquals(suppliers.size(), 5);
    }

    //@Test
    public void testSimpleQuery() {
        Query query = session.createQuery("from Product");
        query.setComment("This is only a query for product");
        List<Product> products = query.list();
        assertEquals(products.size(), 5);
    }

    //@Test
    public void testSimpleProjection() {
        Query query = session.createQuery("select p.name from Product p");
        List<String> suppliers = query.list();
        for (String s : suppliers) {
            System.out.println(s);
        }
        assertEquals(suppliers.size(), 5);
    }

    //@Test
    public void testLikeQuery() {
        Query query = session.createQuery(
                "from Product p "
                        + " where p.price > :minprice"
                        + " and p.description like :desc");
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

    //@Test(dataProvider = "queryTypesProvider")
    public void testQueryTypes(String type, Integer count) {
        Query query = session.createQuery("from " + type);
        List list = query.list();
        assertEquals(list.size(), count.intValue());
    }

    //@Test
    public void searchForProduct() {
        Query query = session.getNamedQuery("product.searchByPhrase");
        query.setParameter("text", "%Mou%");
        List<Product> products = query.list();
        assertEquals(products.size(), 3);
    }
}
