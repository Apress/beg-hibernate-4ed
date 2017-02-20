package chapter04.orphan;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column
    String name;
    @OneToMany(orphanRemoval = true, mappedBy = "library")
    List<Book> books = new ArrayList<>();

    public Library() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
