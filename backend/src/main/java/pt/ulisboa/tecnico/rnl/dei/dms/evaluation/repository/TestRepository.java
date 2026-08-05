package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Test;

@Repository
@Transactional
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByUcId(Long ucId);
}