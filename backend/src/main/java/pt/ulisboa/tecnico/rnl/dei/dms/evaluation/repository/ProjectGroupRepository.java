package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.ProjectGroup;

@Repository
@Transactional
public interface ProjectGroupRepository extends JpaRepository<ProjectGroup, Long> {
    List<ProjectGroup> findByProjectId(Long projectId);
}
