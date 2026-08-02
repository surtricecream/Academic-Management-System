package pt.ulisboa.tecnico.rnl.dei.dms.uc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;

// Repository interface for managing Uc entities
@Repository
@Transactional
public interface UcRepository extends JpaRepository<Uc, Long> {}
