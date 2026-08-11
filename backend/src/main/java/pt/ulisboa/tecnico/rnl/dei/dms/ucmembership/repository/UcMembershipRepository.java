package pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.domain.UcMembership;

// Repository interface for managing Uc Membership entities
@Repository
@Transactional
public interface UcMembershipRepository extends JpaRepository<UcMembership, Long> {
    List<UcMembership> findByUcId(Long ucId);
    List<UcMembership> findByUcIdAndRole(long ucId, UcMembership.MembershipRole role);
    List<UcMembership> findByPersonIdAndRole(Long personId, UcMembership.MembershipRole role);
    Optional<UcMembership> findByUcIdAndPersonId(Long ucId, Long personId);
}
