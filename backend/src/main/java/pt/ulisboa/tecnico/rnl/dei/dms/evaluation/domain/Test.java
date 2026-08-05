package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;

@Data
@Entity
@Table(name = "tests")
public class Test {

    @Id
	@GeneratedValue
	private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double weight;

    @ManyToOne
    @JoinColumn(name = "uc_id", nullable = false)
    private Uc uc;

    protected Test() {}

    public Test(String title, LocalDate date, Double weight, Uc uc) {
        this.title = title;
        this.date = date;
        this.weight = weight;
        this.uc = uc;
    }
}