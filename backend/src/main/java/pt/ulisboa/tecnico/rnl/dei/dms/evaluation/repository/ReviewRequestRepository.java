package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.ReviewRequest;

@Repository
@Transactional
public interface ReviewRequestRepository extends JpaRepository<ReviewRequest, Long> {
    List<ReviewRequest> findByStudentId(Long studentId);
    List<ReviewRequest> findByTest_UcId(Long ucId);
}