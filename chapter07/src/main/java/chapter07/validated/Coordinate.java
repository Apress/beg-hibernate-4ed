package chapter07.validated;

import lombok.*;
import lombok.experimental.Builder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
@NoQuadrantIII
public class Coordinate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    Integer id;
    @Getter
    @Setter
    @NotNull
    Integer x;
    @Getter
    @Setter
    @NotNull
    Integer y;
}
