package chapter12;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.DatabaseRetrievalMethod;
import org.hibernate.search.query.ObjectLookupMethod;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class BaseOGMTest {
    @Test
    public void testQueryLucene() {
        Map<Integer, Person> people = new HashMap<>();
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        for (int i = 7; i < 9; i++) {
            people.put(i, new Person("user " + i, i));
            em.persist(people.get(i));
        }

        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.getTransaction().begin();
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);

        //Optionally use the QueryBuilder to simplify Query definition:
        QueryBuilder b = ftem.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Person.class)
                .get();
        org.apache.lucene.search.Query lq =
                b.keyword().onField("id").matching(people.get(7).getId()).createQuery();

        //Transform the Lucene Query in a JPA Query:
        FullTextQuery ftQuery = ftem.createFullTextQuery(lq, Person.class);
        //This is a requirement when using Hibernate OGM instead of ORM:
        ftQuery.initializeObjectsWith(ObjectLookupMethod.SKIP,
                DatabaseRetrievalMethod.FIND_BY_ID);

        Set<Person> resultList = new HashSet<Person>(ftQuery.getResultList());
        System.out.println(resultList);
        // lucene can return multiple results for a given query!
        Set<Person> personSet = new HashSet<>();
        personSet.addAll(resultList);
        assertEquals(personSet.size(), 1);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testQueryHQL() {
        Map<Integer, Person> people = new HashMap<>();
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        for (int i = 4; i < 7; i++) {
            people.put(i, new Person("user " + i, i));
            em.persist(people.get(i));
        }

        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.getTransaction().begin();
        Session session = em.unwrap(Session.class);
        Query q = session.createQuery("from Person p where p.balance = :balance");
        q.setInteger("balance", 4);
        System.out.println(q.list());
        Person p = (Person) q.uniqueResult();
        System.out.println(p);
        assertEquals(p, people.get(4));
        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.getTransaction().begin();
        session = em.unwrap(Session.class);
        q = session.createQuery("from Person p where p.balance > :balance");
        q.setInteger("balance", 4);
        // we use a Set because NoSQL's concept of uniqueness is.. difficult
        Set<Person> peopleList = new HashSet<Person>(q.list());
        assertEquals(peopleList.size(), 2);
        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.getTransaction().begin();
        session = em.unwrap(Session.class);
        q = session.createQuery("from Person p where p.balance = :balance and p.name=:name");
        q.setInteger("balance", 4);
        q.setString("name", "user 4");
        p = (Person) q.uniqueResult();
        assertEquals(p, people.get(4));
        em.getTransaction().commit();
        em.close();
    }

    abstract String getPersistenceUnitName();

    EntityManagerFactory factory = null;

    public synchronized EntityManager getEntityManager() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(getPersistenceUnitName());
        }
        return factory.createEntityManager();
    }

    @Test
    public void testCR() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Person person = new Person("user 1", 1);
        em.persist(person);
        em.getTransaction().commit();
        em.close();
        System.out.println(person);
        em = getEntityManager();
        Person p2 = em.find(Person.class, person.getId());
        em.close();
        System.out.println(p2);
        assertNotNull(p2);
        assertEquals(p2, person);
    }

    @Test
    public void testUpdate() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Person person = new Person("user 2", 1);
        em.persist(person);
        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.getTransaction().begin();
        Person p2 = em.find(Person.class, person.getId());
        p2.setBalance(2);
        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.getTransaction().begin();
        Person p3 = em.find(Person.class, person.getId());
        em.getTransaction().commit();
        em.close();

        assertEquals(p3, p2);
    }

    @Test
    public void testDelete() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Person person = new Person("user 3", 1);
        em.persist(person);
        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.getTransaction().begin();
        Person p2 = em.find(Person.class, person.getId());
        em.remove(p2);
        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.getTransaction().begin();
        Person p3 = em.find(Person.class, person.getId());
        em.getTransaction().commit();
        em.close();

        assertNull(p3);
    }
}
