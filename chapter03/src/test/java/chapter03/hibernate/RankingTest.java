package chapter03.hibernate;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RankingTest {
    SessionFactory factory;

    @BeforeMethod
    public void setup() {
        Configuration configuration = new Configuration();
        configuration.configure();
        ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
        srBuilder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
        factory = configuration.buildSessionFactory(serviceRegistry);
    }

    @AfterMethod
    public void shutdown() {
        factory.close();
    }

    @Test
    public void testSaveRanking() {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        Person subject = savePerson(session, "J. C. Smell");
        Person observer = savePerson(session, "Drew Lombardo");
        Skill skill = saveSkill(session, "Java");

        Ranking ranking = new Ranking();
        ranking.setSubject(subject);
        ranking.setObserver(observer);
        ranking.setSkill(skill);
        ranking.setRanking(8);
        session.save(ranking);

        tx.commit();
        session.close();
    }

    @Test
    public void testRankings() {
        populateRankingData();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        Query query = session.createQuery("from Ranking r "
                + "where r.subject.name=:name "
                + "and r.skill.name=:skill");
        query.setString("name", "J. C. Smell");


        query.setString("skill", "Java");
        int sum = 0;
        int count = 0;
        for (Ranking r : (List<Ranking>) query.list()) {
            count++;
            sum += r.getRanking();
            System.out.println(r);
        }
        int average = sum / count;
        tx.commit();
        session.close();
        assertEquals(count, 3);
        assertEquals(average, 7);
    }

    @Test
    public void changeRanking() {
        populateRankingData();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("from Ranking r "
                + "where r.subject.name=:subject and "
                + "r.observer.name=:observer and "
                + " r.skill.name=:skill");
        query.setString("subject", "J. C. Smell");
        query.setString("observer", "Gene Showrama");
        query.setString("skill", "Java");
        Ranking ranking = (Ranking) query.uniqueResult();
        assertNotNull(ranking, "Could not find matching ranking");
        ranking.setRanking(9);
        tx.commit();
        session.close();
        assertEquals(getAverage("J. C. Smell", "Java"), 8);
    }

    @Test
    public void removeRanking() {
        populateRankingData();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        Ranking ranking = findRanking(session, "J. C. Smell",
                "Gene Showrama", "Java");
        assertNotNull(ranking, "Ranking not found");
        session.delete(ranking);
        tx.commit();
        session.close();
        assertEquals(getAverage("J. C. Smell", "Java"), 7);
    }

    private int getAverage(String subject, String skill) {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        Query query = session.createQuery("from Ranking r "
                + "where r.subject.name=:name "
                + "and r.skill.name=:skill");
        query.setString("name", subject);
        query.setString("skill", skill);
        int sum = 0;
        int count = 0;
        for (Ranking r : (List<Ranking>) query.list()) {
            count++;
            sum += r.getRanking();
            System.out.println(r);
        }
        int average = sum / count;
        tx.commit();
        session.close();
        return average;
    }

    private void populateRankingData() {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        createData(session, "J. C. Smell", "Gene Showrama", "Java", 6);
        createData(session, "J. C. Smell", "Scottball Most", "Java", 7);
        createData(session, "J. C. Smell", "Drew Lombardo", "Java", 8);
        tx.commit();
        session.close();
    }

    private void createData(Session session, String subjectName,
                            String observerName, String skillName, int rank) {
        Person subject = savePerson(session, subjectName);
        Person observer = savePerson(session, observerName);
        Skill skill = saveSkill(session, skillName);

        Ranking ranking = new Ranking();
        ranking.setSubject(subject);
        ranking.setObserver(observer);
        ranking.setSkill(skill);
        ranking.setRanking(rank);
        session.save(ranking);
    }

    private Person findPerson(Session session, String name) {
        Query query = session.createQuery("from Person p where p.name=:name");
        query.setParameter("name", name);
        Person person = (Person) query.uniqueResult();
        return person;
    }

    private Skill findSkill(Session session, String name) {
        Query query = session.createQuery("from Skill s where s.name=:name");
        query.setParameter("name", name);
        Skill skill = (Skill) query.uniqueResult();
        return skill;
    }

    private Skill saveSkill(Session session, String skillName) {
        Skill skill = findSkill(session, skillName);
        if (skill == null) {
            skill = new Skill();
            skill.setName(skillName);
            session.save(skill);
        }
        return skill;
    }

    private Person savePerson(Session session, String name) {
        Person person = findPerson(session, name);
        if (person == null) {
            person = new Person();
            person.setName(name);
            session.save(person);
        }
        return person;
    }

    private Ranking findRanking(Session session, String subject, String observer, String skill) {
        Query query = session.createQuery("from Ranking r "
                + "where r.subject.name=:subject and "
                + "r.observer.name=:observer and "
                + " r.skill.name=:skill");
        query.setString("subject", subject);
        query.setString("observer", observer);
        query.setString("skill", skill);
        Ranking ranking = (Ranking) query.uniqueResult();
        return ranking;
    }
}
