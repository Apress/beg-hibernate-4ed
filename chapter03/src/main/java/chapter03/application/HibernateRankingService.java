package chapter03.application;

import chapter03.hibernate.Person;
import chapter03.hibernate.Ranking;
import chapter03.hibernate.Skill;
import com.redhat.osas.hibernate.util.SessionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HibernateRankingService implements RankingService {
    @Override
    public int getRankingFor(String subject, String skill) {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        int average = getRankingFor(session, subject, skill);
        tx.commit();
        session.close();
        return average;
    }

    private int getRankingFor(Session session, String subject,
                              String skill) {
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
        return count == 0 ? 0 : sum / count;
    }

    @Override
    public void addRanking(String subjectName, String observerName,
                           String skillName, int rank) {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        addRanking(session, subjectName, observerName, skillName, rank);

        tx.commit();
        session.close();
    }

    private void addRanking(Session session, String subjectName, String observerName, String skillName, int rank) {
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

    @Override
    public void updateRanking(String subject, String observer, String skill, int rank) {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        Ranking ranking = findRanking(session, subject, observer, skill);
        if (ranking == null) {
            addRanking(session, subject, observer, skill, rank);
        } else {
            ranking.setRanking(rank);
        }
        tx.commit();
        session.close();

    }

    @Override
    public void removeRanking(String subject, String observer, String skill) {
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        removeRanking(session, subject, observer, skill);

        tx.commit();
        session.close();
    }

    @Override
    public Map<String, Integer> findRankingsFor(String subject) {
        Map<String, Integer> results;
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        results = findRankingsFor(session, subject);

        tx.commit();
        session.close();

        return results;
    }

    @Override
    public Person findBestPersonFor(String skill) {
        Person person = null;
        Session session = SessionUtil.getSession();
        Transaction tx = session.beginTransaction();

        person = findBestPersonFor(session, skill);

        tx.commit();
        session.close();
        return person;
    }

    private Person findBestPersonFor(Session session, String skill) {
        Query query = session.createQuery("select r.subject.name, avg(r.ranking)"
                + " from Ranking r where "
                + "r.skill.name=:skill "
                + "group by r.subject.name "
                + "order by avg(r.ranking) desc");
        query.setParameter("skill", skill);
        List<Object[]> result = query.list();
        if (result.size() > 0) {
            return findPerson(session, (String) result.get(0)[0]);
        }
        return null;
    }

    private Map<String, Integer> findRankingsFor(Session session, String subject) {
        Map<String, Integer> results = new HashMap<>();

        Query query = session.createQuery("from Ranking r where "
                + "r.subject.name=:subject order by r.skill.name");
        query.setParameter("subject", subject);
        List<Ranking> rankings = query.list();
        String lastSkillName = "";
        int sum = 0;
        int count = 0;
        for (Ranking r : rankings) {
            if (!lastSkillName.equals(r.getSkill().getName())) {
                sum = 0;
                count = 0;
                lastSkillName = r.getSkill().getName();
            }
            sum += r.getRanking();
            count++;
            results.put(lastSkillName, sum / count);
        }
        return results;
    }

    private void removeRanking(Session session, String subject, String observer, String skill) {
        Ranking ranking = findRanking(session, subject, observer, skill);
        if (ranking != null) {
            session.delete(ranking);
        }
    }

    private Ranking findRanking(Session session, String subject,
                                String observer, String skill) {
        Query query = session.createQuery("from Ranking r where "
                + "r.subject.name=:subject and "
                + "r.observer.name=:observer and "
                + "r.skill.name=:skill");
        query.setParameter("subject", subject);
        query.setParameter("observer", observer);
        query.setParameter("skill", skill);
        Ranking ranking = (Ranking) query.uniqueResult();
        return ranking;
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

}
