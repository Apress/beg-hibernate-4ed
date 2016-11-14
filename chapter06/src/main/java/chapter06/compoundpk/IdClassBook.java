package chapter06.compoundpk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity
@IdClass(IdClassBook.EmbeddedISBN.class)
public class IdClassBook {
    @Id
    @Column(name = "group_number")
    int group;
    @Id
    int publisher;
    @Id
    int title;
    @Id
    int checkdigit;
    String name;

    public IdClassBook() {
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getPublisher() {
        return publisher;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getCheckdigit() {
        return checkdigit;
    }

    public void setCheckdigit(int checkdigit) {
        this.checkdigit = checkdigit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    static class EmbeddedISBN implements Serializable {
        int group;
        int publisher;
        int title;
        int checkdigit;

        public EmbeddedISBN() {
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }

        public int getPublisher() {
            return publisher;
        }

        public void setPublisher(int publisher) {
            this.publisher = publisher;
        }

        public int getTitle() {
            return title;
        }

        public void setTitle(int title) {
            this.title = title;
        }

        public int getCheckdigit() {
            return checkdigit;
        }

        public void setCheckdigit(int checkdigit) {
            this.checkdigit = checkdigit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ISBN)) return false;

            ISBN isbn = (ISBN) o;

            if (checkdigit != isbn.checkdigit) return false;
            if (group != isbn.group) return false;
            if (publisher != isbn.publisher) return false;
            if (title != isbn.title) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = group;
            result = 31 * result + publisher;
            result = 31 * result + title;
            result = 31 * result + checkdigit;
            return result;
        }
    }
}
