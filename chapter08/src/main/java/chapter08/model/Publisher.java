package chapter08.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
}
