package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Grade;

@Repository
@Transactional
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByTestId(Long testId);
    List<Grade> findByProjectId(Long projectId);
    Optional<Grade> findByTestIdAndPersonId(Long testId, Long personId);
    Optional<Grade> findByProjectIdAndPersonId(Long projectId, Long personId);
    Optional<Grade> findByProjectIdAndGroupId(Long projectId, Long groupId);
}