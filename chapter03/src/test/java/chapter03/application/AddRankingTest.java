package chapter03.application;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AddRankingTest {
    RankingService service = new HibernateRankingService();

    @Test
    public void addRanking() {
        service.addRanking("J. C. Smell", "Drew Lombardo", "Mule", 8);
        assertEquals(service.getRankingFor("J. C. Smell", "Mule"), 8);
    }
}
