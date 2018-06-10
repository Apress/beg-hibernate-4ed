package chapter06.mappedsuperclass;

import javax.persistence.Entity;

@Entity
public class ComputerBook extends BookSuperclass {
    String language;

    public ComputerBook() {
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
