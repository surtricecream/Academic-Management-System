package pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.domain;

import jakarta.persistence.*;

import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;

// Domain class representing a uc membership in the system
@Data
@Entity
@Table(name = "uc_memberships", uniqueConstraints = @UniqueConstraint(columnNames = {"person_id", "uc_id"}))
public class UcMembership {

    public enum MembershipRole {
        ALUNO,
        ASSISTENTE
    }

    @Id
	@GeneratedValue
	private Long id;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
	private Person person;

    @ManyToOne
    @JoinColumn(name = "uc_id", nullable = false)
	private Uc uc;

    @Column(nullable = false)
	@Enumerated(EnumType.STRING)
    private MembershipRole role;

    protected UcMembership() {}

    public UcMembership(Person person, Uc uc, MembershipRole role) {
        this.person = person;
        this.uc = uc;
        this.role = role;
    }
}
