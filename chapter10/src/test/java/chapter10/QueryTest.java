package chapter10;

import chapter10.model.*;
import com.autumncode.jpa.util.JPASessionUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.function.Consumer;

import static org.testng.Assert.*;

public class QueryTest {
    private void doWithEntityManager(Consumer<EntityManager> command) {
        EntityManager em = JPASessionUtil.getEntityManager("chapter10");
        em.getTransaction().begin();

        command.accept(em);
        if (em.getTransaction().isActive() &&
                !em.getTransaction().getRollbackOnly()) {
            em.getTransaction().commit();
        } else {
            em.getTransaction().rollback();
        }

        em.close();
    }

    @BeforeMethod
    public void populateData() {
        doWithEntityManager((em) -> {
            Supplier supplier = new Supplier("Hardware, Inc.");
            supplier.getProducts().add(
                    new Product(supplier, "Optical Wheel Mouse", "Mouse", 5.00));
            supplier.getProducts().add(
                    new Product(supplier, "Trackball Mouse", "Mouse", 22.00));
            em.persist(supplier);

            supplier = new Supplier("Hardware Are We");
            supplier.getProducts().add(
                    new Software(supplier, "SuperDetect", "Antivirus", 14.95, "1.0"));
            supplier.getProducts().add(
                    new Software(supplier, "Wildcat", "Browser", 19.95, "2.2"));
            supplier.getProducts().add(
                    new Product(supplier, "AxeGrinder", "Gaming Mouse", 42.00));
            supplier.getProducts().add(
                    new Product(supplier, "I5 Tablet", "Computer", 849.99));
            supplier.getProducts().add(
                    new Product(supplier, "I7 Desktop", "Computer", 1599.99));

            em.persist(supplier);
        });
    }

    @AfterMethod
    public void cleanup() {
        doWithEntityManager((em) -> {
            em.createQuery("delete from Software").executeUpdate();
            em.createQuery("delete from Product").executeUpdate();
            em.createQuery("delete from Supplier").executeUpdate();
        });
    }

    @Test
    public void testSimpleCriteriaQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            criteria.select(root);

            assertEquals(em.createQuery(criteria).getResultList().size(), 7);
        });
    }

    @Test
    public void testSimpleEQQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(
                    builder.equal(
                            root.get(Product_.description),
                            builder.parameter(String.class, "description")
                    )
            );

            criteria.select(root);

            assertEquals(em
                    .createQuery(criteria)
                    .setParameter("description", "Mouse")
                    .getResultList()
                    .size(), 2);
        });
    }

    @Test
    public void testSimpleNEQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(
                    builder.notEqual(
                            root.get(Product_.description),
                            builder.parameter(String.class, "description")
                    )
            );

            criteria.select(root);

            assertEquals(em
                    .createQuery(criteria)
                    .setParameter("description", "Mouse")
                    .getResultList()
                    .size(), 5);
        });
    }

    @Test
    public void testSimpleLikeQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(builder.like(
                    root.get(Product_.description),
                    builder.parameter(String.class, "description")));

            criteria.select(root);

            assertEquals(em.createQuery(criteria)
                    .setParameter("description", "%Mouse")
                    .getResultList().size(), 3);
        });
    }

    @Test
    public void testSimpleLikeIgnoreCaseQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(builder.like(
                    builder.lower(root.get(Product_.description)),
                    builder.lower(builder.parameter(String.class, "description")))
            );

            criteria.select(root);

            assertEquals(em.createQuery(criteria)
                    .setParameter("description", "%mOUse")
                    .getResultList().size(), 3);
        });
    }

    @Test
    public void testNotNullQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(builder.isNull(
                    builder.lower(root.get(Product_.description))));

            criteria.select(root);

            assertEquals(em.createQuery(criteria).getResultList().size(), 0);
        });
    }

    @Test
    public void testGTQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(builder.greaterThan(root.get(Product_.price),
                    builder.parameter(Double.class, "price")));

            criteria.select(root);

            assertEquals(em.createQuery(criteria)
                    .setParameter("price", 25.0)
                    .getResultList().size(), 3);
        });
    }

    @Test
    public void testLTEQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(builder.lessThanOrEqualTo(root.get(Product_.price),
                    builder.parameter(Double.class, "price")));

            criteria.select(root);

            assertEquals(em.createQuery(criteria)
                    .setParameter("price", 25.0)
                    .getResultList().size(), 4);
        });
    }

    @Test
    public void testANDQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(
                    builder.and(
                            builder.lessThanOrEqualTo(
                                    root.get(Product_.price),
                                    builder.parameter(Double.class, "price")
                            ),
                            builder.like(
                                    builder.lower(
                                            root.get(Product_.description)
                                    ),
                                    builder.lower(
                                            builder.parameter(String.class, "description")
                                    )
                            )
                    )
            );

            criteria.select(root);

            assertEquals(em.createQuery(criteria)
                    .setParameter("price", 10.0)
                    .setParameter("description", "%mOUse")
                    .getResultList().size(), 1);
        });
    }

    @Test
    public void testORQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(
                    builder.or(
                            builder.lessThanOrEqualTo(
                                    root.get(Product_.price),
                                    builder.parameter(Double.class, "price")
                            ),
                            builder.like(
                                    builder.lower(
                                            root.get(Product_.description)
                                    ),
                                    builder.lower(
                                            builder.parameter(String.class, "description")
                                    )
                            )
                    )
            );

            criteria.select(root);

            assertEquals(em.createQuery(criteria)
                    .setParameter("price", 10.0)
                    .setParameter("description", "%mOUse")
                    .getResultList().size(), 3);
        });
    }

    @Test
    public void testOrQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(
                    builder.or(
                            builder.lessThanOrEqualTo(
                                    root.get(Product_.price),
                                    15.00
                            ),
                            builder.like(
                                    builder.lower(
                                            root.get(Product_.description)
                                    ),
                                    "%mOUse".toLowerCase()
                            )
                    )
            );

            criteria.select(root);

            assertEquals(em.createQuery(criteria).getResultList().size(), 4);
        });
    }

    @Test
    public void testDisjunction() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);

            Metamodel m = em.getMetamodel();
            EntityType<Product> product = m.entity(Product.class);
            Root<Product> root = criteria.from(product);
            criteria.select(root);
            criteria.where(
                    builder.or(
                            builder.and(
                                    builder.lessThanOrEqualTo(
                                            root.get(Product_.price),
                                            builder.parameter(Double.class, "price_1")
                                    ),
                                    builder.like(
                                            builder.lower(
                                                    root.get(Product_.description)
                                            ),
                                            builder.lower(
                                                    builder.parameter(String.class, "desc_1")
                                            )
                                    )
                            ),
                            builder.and(
                                    builder.greaterThan(
                                            root.get(Product_.price),
                                            builder.parameter(Double.class, "price_2")
                                    ),
                                    builder.like(
                                            builder.lower(
                                                    root.get(Product_.description)
                                            ),
                                            builder.lower(
                                                    builder.parameter(String.class, "desc_2")
                                            )
                                    )
                            )
                    )
            );

            criteria.select(root);

            assertEquals(em.createQuery(criteria)
                    .setParameter("price_1", 25.00)
                    .setParameter("desc_1", "%mOUse")
                    .setParameter("price_2", 999.0)
                    .setParameter("desc_2", "Computer")
                    .getResultList().size(), 3);
        });
    }

    @Test
    public void testPagination() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            criteria.select(root);
            TypedQuery<Product> query = em.createQuery(criteria);
            query.setFirstResult(2);
            query.setMaxResults(2);

            assertEquals(query.getResultList().size(), 2);
        });
    }

    @Test
    public void testUniqueResult() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            criteria.select(root);
            criteria.where(builder.equal(root.get(Product_.price),
                    builder.parameter(Double.class, "price")));

            assertEquals(em.createQuery(criteria)
                    .setParameter("price", 14.95)
                    .getSingleResult().getName(), "SuperDetect");
        });
    }

    @Test(expectedExceptions = NonUniqueResultException.class)
    public void testUniqueResultWithException() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            criteria.select(root);
            criteria.where(builder.greaterThan(root.get(Product_.price),
                    builder.parameter(Double.class, "price")));

            Product p = em.createQuery(criteria)
                    .setParameter("price", 14.95)
                    .getSingleResult();
            fail("Should have thrown an exception");
        });
    }

    @Test
    public void testOrderedQuery() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            criteria.select(root);
            criteria.orderBy(
                    builder.asc(root.get(Product_.description)),
                    builder.asc(root.get(Product_.name))
            );
            List<Product> p = em.createQuery(criteria).getResultList();
            assertEquals(p.size(), 7);
            assertTrue(p.get(0).getPrice() < p.get(1).getPrice());
        });
    }

    @Test
    public void testGetSuppliersWithProductUnder7() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Supplier> criteria = builder.createQuery(Supplier.class);
            Root<Product> root = criteria.from(Product.class);
            criteria.select(root.get(Product_.supplier))
                    .distinct(true);
            criteria.where(builder.lessThanOrEqualTo(root.get(Product_.price),
                    builder.parameter(Double.class, "price")));

            assertEquals(em.createQuery(criteria)
                    .setParameter("price", 7.0)
                    .getResultList().size(), 1);
        });
    }

    @Test
    public void testGetProductsForSupplierFromProduct() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            criteria.select(root);
            criteria.where(builder.equal(
                    root.join(Product_.supplier).get(Supplier_.name),
                    builder.parameter(String.class, "supplier_name"))
            );

            List<Product> p = em.createQuery(criteria)
                    .setParameter("supplier_name", "Hardware Are We")
                    .getResultList();

            assertEquals(p.size(), 5);
        });
    }

    @Test
    public void testGetProductsForSupplierFromSupplier() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Supplier> root = criteria.from(Supplier.class);
            criteria.select(root.join(Supplier_.products));
            criteria.where(builder.equal(
                    root.get(Supplier_.name),
                    builder.parameter(String.class, "supplier_name"))
            );
            TypedQuery<Product> query = em.createQuery(criteria);
            query.setParameter("supplier_name", "Hardware Are We");

            List<Product> p = query.getResultList();

            assertEquals(p.size(), 5);
        });
    }

    @Test
    public void testProjection() {
        doWithEntityManager((em) -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<SupplierResult> criteria = builder.createQuery(SupplierResult.class);
            Root<Supplier> root = criteria.from(Supplier.class);
            criteria.select(builder.construct(
                    SupplierResult.class,
                    root.get(Supplier_.name),
                    builder.count(root.join(Supplier_.products))
            ))
                    .groupBy(root.get(Supplier_.name))
                    //
                    // .distinct(true)
                    .orderBy(builder.asc(root.get(Supplier_.name)));

            List<SupplierResult> supplierData = em.createQuery(criteria).getResultList();
            assertEquals(supplierData.get(0).count, 5);
            assertEquals(supplierData.get(0).name, "Hardware Are We");
            assertEquals(supplierData.get(1).count, 2);
            assertEquals(supplierData.get(1).name, "Hardware, Inc.");
        });
    }
    /*
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
        assertEquals(42.0, results.get(0)[0]);
        assertEquals(5.0, results.get(0)[1]);
        assertEquals(20.78, results.get(0)[2]);
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
                    p[0].toString(), p[1]);
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
            assertTrue(o[0] instanceof Product);
            assertTrue(o[1] instanceof Supplier);
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
    */
}
