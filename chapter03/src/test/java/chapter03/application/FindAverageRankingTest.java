package chapter03.application;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FindAverageRankingTest {
    RankingService service = new HibernateRankingService();

    @Test
    public void validateRankingAverage() {
        service.addRanking("A", "B", "C", 4);
        service.addRanking("A", "B", "C", 5);
        service.addRanking("A", "B", "C", 6);
        assertEquals(service.getRankingFor("A", "C"), 5);
        service.addRanking("A", "B", "C", 7);
        service.addRanking("A", "B", "C", 8);
        assertEquals(service.getRankingFor("A", "C"), 6);
    }
}
