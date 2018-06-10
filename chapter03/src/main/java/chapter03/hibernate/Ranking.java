package chapter03.hibernate;

import javax.persistence.*;

@Entity
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    Person subject;
    @ManyToOne
    Person observer;
    @ManyToOne
    Skill skill;
    @Column
    Integer ranking;

    public Ranking() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getSubject() {
        return subject;
    }

    public void setSubject(Person subject) {
        this.subject = subject;
    }

    public Person getObserver() {
        return observer;
    }

    public void setObserver(Person observer) {
        this.observer = observer;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    @Override
    public String toString() {
        return "Ranking{" +
                "id=" + id +
                ", subject=" + subject +
                ", observer=" + observer +
                ", skill=" + skill +
                ", ranking=" + ranking +
                '}';
    }
}