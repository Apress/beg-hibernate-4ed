package chapter07.validated;

import lombok.*;
import lombok.experimental.Builder;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ValidatedSimplePerson {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    @NotNull
    @Size(min = 2, max = 60)
    @Getter
    @Setter
    String fname;
    @Column
    @NotNull
    @Size(min = 2, max = 60)
    @Getter
    @Setter
    String lname;
    @Column
    @Min(value = 13)
    @Getter
    @Setter
    Integer age;
}
