package chapter04.general;

import chapter04.model.SimpleObject;
import com.autumncode.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class MergeRefreshTest {
    @Test
    public void testMerge() {
        Long id;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();

            SimpleObject simpleObject = new SimpleObject();

            simpleObject.setKey("testMerge");
            simpleObject.setValue(1L);

            session.save(simpleObject);

            id = simpleObject.getId();

            tx.commit();
        }

        SimpleObject so = validateSimpleObject(id, 1L);

        so.setValue(2L);

        try (Session session = SessionUtil.getSession()) {
            // merge is potentially an update, so we need a TX
            Transaction tx = session.beginTransaction();

            session.merge(so);

            tx.commit();
        }

        validateSimpleObject(id, 2L);
    }

    @Test
    public void testRefresh() {
        Long id;
        try (Session session = SessionUtil.getSession()) {
            Transaction tx = session.beginTransaction();

            SimpleObject simpleObject = new SimpleObject();

            simpleObject.setKey("testMerge");
            simpleObject.setValue(1L);

            session.save(simpleObject);

            id = simpleObject.getId();

            tx.commit();
        }

        SimpleObject so = validateSimpleObject(id, 1L);

        so.setValue(2L);

        try (Session session = SessionUtil.getSession()) {
            // note that refresh is a read,
            // so no TX is necessary unless an update occurs later
            session.refresh(so);
        }

        validateSimpleObject(id, 1L);
    }

    private SimpleObject validateSimpleObject(Long id, Long value) {
        SimpleObject so = null;
        try (Session session = SessionUtil.getSession()) {
            so = session.load(SimpleObject.class, id);

            assertEquals(so.getKey(), "testMerge");
            assertEquals(so.getValue(), value);
        }

        return so;
    }
}
