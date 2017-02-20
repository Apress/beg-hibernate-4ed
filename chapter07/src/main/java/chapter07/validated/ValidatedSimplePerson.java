package chapter07.validated;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class ValidatedSimplePerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    @NotNull
    @Size(min = 2, max = 60)
    String fname;
    @Column
    @NotNull
    @Size(min = 2, max = 60)
    String lname;
    @Column
    @Min(value = 13)
    Integer age;
}
