package chapter13;

import chapter13.model.User;
import com.autumncode.hibernate.util.SessionUtil;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class EnversTest {
    int[] userId = {0};

    @Test
    public void createUser() {
        SessionUtil.doWithSession((session) -> {
            User user = new User("user name", true);
            user.setDescription("first description");
            user.addGroups("group1");
            session.save(user);
            userId[0] = user.getId();
        });
        SessionUtil.doWithSession((session) -> {
            User user = session.byId(User.class).load(userId[0]);
            assertTrue(user.isActive());
            assertEquals(user.getDescription(),
                    "first description");
        });
    }

    @Test(dependsOnMethods = "createUser")
    public void updateUser() {
        SessionUtil.doWithSession((session) -> {
            User user = session.byId(User.class).load(userId[0]);
            user.addGroups("group2");
            user.setDescription("other description");
        });

        SessionUtil.doWithSession((session) -> {
            User user = session.byId(User.class).load(userId[0]);
            user.setActive(false);
        });

        SessionUtil.doWithSession((session) -> {
            User user = session.byId(User.class).load(userId[0]);
            assertFalse(user.isActive());
            assertEquals(user.getDescription(), "other description");
        });
    }


    @Test(dependsOnMethods = "updateUser")
    public void validateRevisionData() {
        SessionUtil.doWithSession((session) -> {
            AuditReader reader = AuditReaderFactory.get(session);
            List<Number> revisions = reader.getRevisions(User.class, userId[0]);
            assertEquals(revisions.size(), 3);
            assertEquals(
                    reader.find(User.class, userId[0], 1).getDescription(),
                    "first description");
            System.err.println(reader.find(User.class, userId[0], 2));
            assertEquals(
                    reader.find(User.class, userId[0], 2).getDescription(),
                    "other description");
            assertFalse(
                    reader.find(User.class, userId[0], 3).isActive()
            );
        });
    }

    @Test(dependsOnMethods = "validateRevisionData")
    public void findLastActiveUserRevision() {
        SessionUtil.doWithSession((session) -> {
            AuditReader reader = AuditReaderFactory.get(session);
            AuditQuery query = reader.createQuery()
                    .forRevisionsOfEntity(User.class, true, true)
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .setMaxResults(1)
                    .add(AuditEntity.id().eq(userId[0]))
                    .add(AuditEntity.property("active").eq(true));

            User user = (User) query.getSingleResult();

            assertEquals(user.getDescription(), "other description");
        });
    }

    @Test(dependsOnMethods = "findLastActiveUserRevision")
    public void revertUserData() {
        SessionUtil.doWithSession((session) -> {
            AuditReader reader = AuditReaderFactory.get(session);
            AuditQuery query = reader.createQuery()
                    .forRevisionsOfEntity(User.class, true, true)
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .setMaxResults(1)
                    .add(AuditEntity.id().eq(userId[0]))
                    .add(AuditEntity.property("active").eq(true));

            User auditUser = (User) query.getSingleResult();
            assertEquals(auditUser.getDescription(), "other description");

            // now we copy the audit data into the "current user."
            User user = session.byId(User.class).load(userId[0]);
            assertFalse(user.isActive());
            user.setActive(auditUser.isActive());
            user.setDescription(auditUser.getDescription());
            user.setGroups(auditUser.getGroups());
        });

        // let's make sure the "current user" looks like what we expect
        SessionUtil.doWithSession((session) -> {
            User user = session.byId(User.class).load(userId[0]);
            assertTrue(user.isActive());
            assertEquals(user.getDescription(), "other description");
        });
    }
}
