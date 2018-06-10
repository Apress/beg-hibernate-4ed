package chapter12.hibernate;

import chapter12.Person;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.ogm.OgmSessionFactory;
import org.hibernate.ogm.boot.OgmSessionFactoryBuilder;
import org.hibernate.ogm.cfg.OgmProperties;
import org.hibernate.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.DatabaseRetrievalMethod;
import org.hibernate.search.query.ObjectLookupMethod;
import org.hibernate.search.query.dsl.QueryBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public abstract class BaseHibernateOGMTest {
    abstract String getConfigurationName();

    private final OgmSessionFactory factory;

    public BaseHibernateOGMTest() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting(OgmProperties.ENABLED, true)
                //assuming you are using JTA in a non container environment
                .applySetting(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jta")
                //assuming JBoss TransactionManager in standalone mode
                .applySetting(AvailableSettings.JTA_PLATFORM, "JBossTS")
                //assuming Infinispan as the backend, using the default settings
                .applySetting(OgmProperties.DATASTORE_PROVIDER, getConfigurationName())
                .build();
        factory = new MetadataSources(registry)
                .addAnnotatedClass(Person.class)
                .buildMetadata()
                .getSessionFactoryBuilder()
                .unwrap(OgmSessionFactoryBuilder.class)
                .build();
    }

    protected Session getSession() {
        return factory.openSession();
    }

    @Test
    public void testQueryLucene() {
        Map<Integer, Person> people = new HashMap<>();
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            for (int i = 7; i < 9; i++) {
                people.put(i, new Person("user " + i, i));
                session.save(people.get(i));
            }
            tx.commit();
        }

        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            FullTextSession ftem = Search.getFullTextSession(session);

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

            Set<Person> resultList = new HashSet<>(ftQuery.list());
            System.out.println(resultList);
            // lucene can return multiple results for a given query!
            Set<Person> personSet = new HashSet<>();
            personSet.addAll(resultList);
            assertEquals(personSet.size(), 1);
            tx.commit();
        }
    }

    //@Test
    public void testQueryHQL() {
        Map<Integer, Person> people = new HashMap<>();
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            for (int i = 4; i < 7; i++) {
                people.put(i, new Person("user " + i, i));
                session.save(people.get(i));
            }
            tx.commit();
        }
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            Query query = session.createQuery("from Person p where p.balance = :balance");
            query.setParameter("balance", 4);
            System.out.println(query.list());
            Person p = (Person) query.uniqueResult();
            assertEquals(p, people.get(4));

            query = session.createQuery("from Person p where p.balance > :balance");
            query.setParameter("balance", 4);
            Set<Person> peopleList = new HashSet<Person>(query.list());
            assertEquals(peopleList.size(), 2);

            query = session.createQuery("from Person p where p.balance = :balance and p.name=:name");
            query.setParameter("balance", 4);
            query.setParameter("name", "user 4");
            p = (Person) query.uniqueResult();
            assertEquals(p, people.get(4));
            tx.commit();
        }
    }

    //@Test
    public void testCR() {
        Person person, p2;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            person = new Person("user 1", 1);
            session.save(person);
            tx.commit();
        }
        System.out.println(person);
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            p2 = session.byId(Person.class).load(person.getId());
            tx.commit();
        }
        System.out.println(p2);
        assertNotNull(p2);
        assertEquals(p2, person);
    }

    //@Test
    public void testUpdate() {
        Person person, p2, p3;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            person = new Person("user 2", 1);
            session.save(person);
            tx.commit();
        }
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            p2 = session.load(Person.class, person.getId());
            p2.setBalance(2);
            tx.commit();
        }

        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            p3 = session.load(Person.class, person.getId());
            tx.commit();
        }
        assertEquals(p3, p2);
    }

    //@Test
    public void testDelete() {
        Person person, p2, p3;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            person = new Person("user 3", 1);
            session.save(person);
            tx.commit();
        }
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            p2 = session.load(Person.class, person.getId());
            session.delete(p2);
            tx.commit();
        }
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            p3 = session.load(Person.class, person.getId());
            tx.commit();
        }
        assertNull(p3);
    }

}
