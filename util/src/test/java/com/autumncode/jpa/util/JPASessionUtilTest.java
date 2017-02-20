package com.autumncode.jpa.util;

import com.autumncode.util.model.Thing;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.testng.Assert.*;

public class JPASessionUtilTest {
@Test
public void getEntityManager() {
    EntityManager em = JPASessionUtil.getEntityManager("utiljpa");
    em.close();
}

@Test(expectedExceptions = {javax.persistence.PersistenceException.class})
public void nonexistentEntityManagerName() {
    JPASessionUtil.getEntityManager("nonexistent");
    fail("We shouldn't be able to acquire an EntityManager here");
}

@Test
public void getSession() {
    Session session = JPASessionUtil.getSession("utiljpa");
    session.close();
}

@Test(expectedExceptions = {javax.persistence.PersistenceException.class})
public void nonexistentSessionName() {
    JPASessionUtil.getSession("nonexistent");
    fail("We shouldn't be able to acquire a Session here");
}

@Test
public void testEntityManager() {
    EntityManager em = JPASessionUtil.getEntityManager("utiljpa");
    em.getTransaction().begin();
    Thing t = new Thing();
    t.setName("Thing 1");
    em.persist(t);
    em.getTransaction().commit();
    em.close();

    em = JPASessionUtil.getEntityManager("utiljpa");
    em.getTransaction().begin();
    TypedQuery<Thing> q = em.createQuery("from Thing t where t.name=:name", Thing.class);
    q.setParameter("name", "Thing 1");
    Thing result = q.getSingleResult();
    assertNotNull(result);
    assertEquals(result, t);
    em.remove(result);
    em.getTransaction().commit();
    em.close();
}

@Test
public void testSession() {
    Thing t=null;
    try(Session session = JPASessionUtil.getSession("utiljpa")) {
        Transaction tx = session.beginTransaction();
        t = new Thing();
        t.setName("Thing 2");
        session.persist(t);
        tx.commit();
    }

    try(Session session = JPASessionUtil.getSession("utiljpa")) {
        Transaction tx = session.beginTransaction();
        Query<Thing> q =
                session.createQuery("from Thing t where t.name=:name", Thing.class);
        q.setParameter("name", "Thing 2");
        Thing result = q.uniqueResult();
        assertNotNull(result);
        assertEquals(result, t);
        session.delete(result);
        tx.commit();
    }
}
}
