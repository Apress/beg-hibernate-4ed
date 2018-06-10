package chapter12.jpa;

import org.hibernate.Query;
import org.hibernate.Session;
import org.testng.annotations.BeforeMethod;

import javax.persistence.EntityManager;

public class InfinispanTest extends BaseJPAOGMTest {
    @Override
    String getPersistenceUnitName() {
        return "chapter12-ispn";
    }

    @BeforeMethod
    public void clearInfinispan() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Session session = em.unwrap(Session.class);
        Query q = session.createQuery("from Person p");
        for (Object p : q.list()) {
            System.out.println("removing " + p);
            em.remove(p);
        }
        em.getTransaction().commit();
        em.close();
    }
}
