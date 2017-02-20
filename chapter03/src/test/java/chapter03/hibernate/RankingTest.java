package chapter03.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RankingTest {
    private SessionFactory factory;

    @BeforeMethod
    public void setup() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @AfterMethod
    public void shutdown() {
        factory.close();
    }

    @Test
    public void testSaveRanking() {
        try (Session session = factory.openSession()) {
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
        }
    }

    @Test
    public void testRankings() {
        populateRankingData();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();

            Query<Ranking> query = session.createQuery("from Ranking r "
                    + "where r.subject.name=:name "
                    + "and r.skill.name=:skill", Ranking.class);
            query.setParameter("name", "J. C. Smell");


            query.setParameter("skill", "Java");

            IntSummaryStatistics stats = query.list()
                    .stream()
                    .collect(Collectors.summarizingInt(Ranking::getRanking));

            long count = stats.getCount();
            int average = (int) stats.getAverage();

            tx.commit();
            session.close();
            assertEquals(count, 3);
            assertEquals(average, 7);
        }
    }

    @Test
    public void changeRanking() {
        populateRankingData();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Query<Ranking> query = session.createQuery("from Ranking r "
                    + "where r.subject.name=:subject and "
                    + "r.observer.name=:observer and "
                    + "r.skill.name=:skill", Ranking.class);
            query.setParameter("subject", "J. C. Smell");
            query.setParameter("observer", "Gene Showrama");
            query.setParameter("skill", "Java");
            Ranking ranking = query.uniqueResult();
            assertNotNull(ranking, "Could not find matching ranking");
            ranking.setRanking(9);
            tx.commit();
        }
        assertEquals(getAverage("J. C. Smell", "Java"), 8);
    }

    @Test
    public void removeRanking() {
        populateRankingData();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Ranking ranking = findRanking(session, "J. C. Smell",
                    "Gene Showrama", "Java");
            assertNotNull(ranking, "Ranking not found");
            session.delete(ranking);
            tx.commit();
        }
        assertEquals(getAverage("J. C. Smell", "Java"), 7);
    }

    private int getAverage(String subject, String skill) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();

            Query<Ranking> query = session.createQuery("from Ranking r "
                    + "where r.subject.name=:name "
                    + "and r.skill.name=:skill", Ranking.class);
            query.setParameter("name", subject);
            query.setParameter("skill", skill);

            IntSummaryStatistics stats = query.list()
                    .stream()
                    .collect(Collectors.summarizingInt(Ranking::getRanking));

            int average = (int) stats.getAverage();
            tx.commit();
            return average;
        }
    }

    private void populateRankingData() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            createData(session, "J. C. Smell", "Gene Showrama", "Java", 6);
            createData(session, "J. C. Smell", "Scottball Most", "Java", 7);
            createData(session, "J. C. Smell", "Drew Lombardo", "Java", 8);
            tx.commit();
        }
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
        Query<Person> query = session.createQuery("from Person p where p.name=:name", Person.class);
        query.setParameter("name", name);
        Person person = query.uniqueResult();
        return person;
    }

    private Skill findSkill(Session session, String name) {
        Query<Skill> query = session.createQuery("from Skill s where s.name=:name", Skill.class);
        query.setParameter("name", name);
        Skill skill = query.uniqueResult();
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
        Query<Ranking> query = session.createQuery("from Ranking r "
                + "where r.subject.name=:subject and "
                + "r.observer.name=:observer and "
                + " r.skill.name=:skill", Ranking.class);
        query.setParameter("subject", subject);
        query.setParameter("observer", observer);
        query.setParameter("skill", skill);
        Ranking ranking = query.uniqueResult();
        return ranking;
    }
}
