package chapter04.general;

import chapter04.model.SimpleObject;
import com.redhat.osas.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class MergeRefreshTest {
    @Test
    public void testMerge() {
        Long id;
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        SimpleObject simpleObject = new SimpleObject();

        simpleObject.setKey("testMerge");
        simpleObject.setValue(1L);

        session.save(simpleObject);

        id = simpleObject.getId();

        tx.commit();
        session.close();

        SimpleObject so = validateSimpleObject(id, 1L);

        so.setValue(2L);

        session = SessionUtil.getSession();
        tx = session.beginTransaction();

        session.merge(so);

        tx.commit();
        session.close();

        validateSimpleObject(id, 2L);
    }

    @Test
    public void testRefresh() {
        Long id;
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        SimpleObject simpleObject = new SimpleObject();

        simpleObject.setKey("testMerge");
        simpleObject.setValue(1L);

        session.save(simpleObject);

        id = simpleObject.getId();

        tx.commit();
        session.close();

        SimpleObject so = validateSimpleObject(id, 1L);

        so.setValue(2L);

        session = SessionUtil.getSession();
        tx = session.beginTransaction();

        session.refresh(so);

        tx.commit();
        session.close();

        validateSimpleObject(id, 1L);
    }

    private SimpleObject validateSimpleObject(Long id, Long value) {
        Session session;
        Transaction tx;// validate the database values
        session = SessionUtil.getSession();
        tx = session.beginTransaction();

        SimpleObject so = (SimpleObject) session.load(SimpleObject.class, id);

        assertEquals(so.getKey(), "testMerge");
        assertEquals(so.getValue(), value);

        tx.commit();
        session.close();

        return so;
    }
}
