package chapter12.hibernate;

import chapter12.Person;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.ogm.datastore.infinispan.Infinispan;
import org.testng.annotations.BeforeMethod;

import java.util.List;

public class InfinispanTest extends BaseHibernateOGMTest {
    @Override String getConfigurationName() {
        return Infinispan.DATASTORE_PROVIDER_NAME;
    }

    @BeforeMethod
    public void clearInfinispan() {
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query=session.createQuery("from Person");
            for(Person p:(List<Person>)query.list()) {
                session.delete(p);
            }
            tx.commit();
        }
    }
}
