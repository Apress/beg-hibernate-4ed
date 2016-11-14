package chapter06.naturalid;

import com.redhat.osas.hibernate.util.SessionUtil;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class NaturalIdTest {
    @Test
    public void testSimpleNaturalId() {
        Integer id = createSimpleEmployee("Sorhed", 5401).getId();

        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        SimpleNaturalIdEmployee employee =
                (SimpleNaturalIdEmployee) session
                        .byId(SimpleNaturalIdEmployee.class)
                        .load(id);
        assertNotNull(employee);
        SimpleNaturalIdEmployee badgedEmployee =
                (SimpleNaturalIdEmployee) session
                        .bySimpleNaturalId(SimpleNaturalIdEmployee.class)
                        .load(5401);
        assertEquals(badgedEmployee, employee);

        tx.commit();
        session.close();
    }

    @Test
    public void testLoadByNaturalId() {
        Employee initial = createEmployee("Arrowroot", 11, 291);
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        Employee arrowroot = (Employee) session
                .byNaturalId(Employee.class)
                .using("section", 11)
                .using("department", 291)
                .load();
        assertNotNull(arrowroot);
        assertEquals(initial, arrowroot);

        tx.commit();
        session.close();
    }

@Test
public void testGetByNaturalId() {
	Employee initial = createEmployee("Eorwax", 11, 292);
	Session session = SessionUtil.getSession();
	Transaction tx = session.beginTransaction();

	Employee eorwax = (Employee) session
			.byNaturalId(Employee.class)
			.using("section", 11)
			.using("department", 292)
			.getReference();
	assertEquals(initial, eorwax);

	tx.commit();
	session.close();
}

    @Test
    public void testLoadById() {
        Integer id = createEmployee("Legolam", 10, 289).getId();
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        Employee boggit = (Employee) session.byId(Employee.class).load(id);
        assertNotNull(boggit);

         /*
        load successful, let's delete it for the second half of the test
        */
        session.delete(boggit);

        tx.commit();
        session.close();

        session = SessionUtil.getSession();
        tx = session.beginTransaction();

        boggit = (Employee) session.byId(Employee.class).load(id);
        assertNull(boggit);

        tx.commit();
        session.close();
    }

    @Test
    public void testGetById() {
        Integer id = createEmployee("Eorache", 10, 290).getId();
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        Employee boggit = (Employee) session.byId(Employee.class).getReference(id);
        assertNotNull(boggit);

         /*
        load successful, let's delete it for the second half of the test
        */
        session.delete(boggit);

        tx.commit();
        session.close();

        session = SessionUtil.getSession();
        tx = session.beginTransaction();

        try {
            boggit = (Employee) session.byId(Employee.class).getReference(id);

            // trigger object initialization - which, with a nonexistent object,
            // will blow up.
            boggit.getDepartment();
            fail("Should have had an exception thrown!");
        } catch (ObjectNotFoundException ignored) {
        }

        tx.commit();
        session.close();
    }

    private Employee createEmployee(String name, int section, int department) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setDepartment(department);
        employee.setSection(section);
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.save(employee);
        tx.commit();
        session.close();
        return employee;
    }

    private SimpleNaturalIdEmployee createSimpleEmployee(String name, int badge) {
        SimpleNaturalIdEmployee employee = new SimpleNaturalIdEmployee();
        employee.setName(name);
        employee.setBadge(badge);

        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.save(employee);
        tx.commit();
        session.close();
        return employee;
    }

}
