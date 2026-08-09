package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Test;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.ProjectGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Grade;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.GradeDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.StudentGradesDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateGradeDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.GradeRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.ProjectGroupRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.TestRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.repository.UcRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.domain.UcMembership;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.repository.UcMembershipRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;


@Service
@Transactional
public class GradeService {

    @Autowired 
    private GradeRepository gradeRepository;
    
    @Autowired
    private TestRepository testRepository;
    
    @Autowired 
    private ProjectRepository projectRepository;
    
    @Autowired 
    private PersonRepository personRepository;
    
    @Autowired 
    private ProjectGroupRepository groupRepository;
    
    @Autowired 
    private UcMembershipRepository membershipRepository;

    @Autowired
    private UcRepository ucRepository;

    private Test fetchTestOrThrow(long id) {
        return testRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_TEST, Long.toString(id)));
    }

    private Project fetchProjectOrThrow(long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PROJECT, Long.toString(id)));
    }

    private Person fetchPersonOrThrow(long id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
    }

    private ProjectGroup fetchGroupOrThrow(long id) {
        return groupRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_GROUP, Long.toString(id)));
    }

    private Uc fetchUcOrThrow(long id) {
        return ucRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_UC, Long.toString(id)));
    }

    private void authorizeGrading(Uc uc, long requesterId) {
        Person requester = fetchPersonOrThrow(requesterId);
        if (requester.getType() == Person.PersonType.ADMINISTRATOR) return;
        if (requesterId == uc.getRegente().getId()) return;

        boolean isAssistant = membershipRepository.findByUcIdAndPersonId(uc.getId(), requesterId)
                .map(m -> m.getRole() == UcMembership.MembershipRole.ASSISTENTE)
                .orElse(false);
        if (!isAssistant) throw new DEIException(ErrorMessage.NOT_AUTHORIZED);
    }

    private void authorizeViewGrades(Uc uc, long studentId, long requesterId) {
        if (requesterId == studentId) return;
        
        authorizeGrading(uc, requesterId);
    }

    private void validateScore(Double score) {
        if (score == null || score < 0 || score > 20) {
            throw new DEIException(ErrorMessage.INVALID_SCORE);
        }
    }

    private boolean belongsToUc(Grade g, long ucId) {
        if (g.getTest() != null) return g.getTest().getUc().getId().equals(ucId);
        if (g.getProject() != null) return g.getProject().getUc().getId().equals(ucId);
        return false;
    }

    public GradeDto gradeTest(long testId, CreateGradeDto dto, long requesterId) {
        Test test = fetchTestOrThrow(testId);
        authorizeGrading(test.getUc(), requesterId);
        if (dto.personId() == null || dto.groupId() != null) {
            throw new DEIException(ErrorMessage.INVALID_GRADE_SUBJECT);
        }

        Person person = fetchPersonOrThrow(dto.personId());
        Person grader = fetchPersonOrThrow(requesterId);
        UcMembership membership = membershipRepository.findByUcIdAndPersonId(test.getUc().getId(), person.getId())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_MEMBERSHIP, Long.toString(person.getId())));

        if (membership.getRole() != UcMembership.MembershipRole.ALUNO) {
            throw new DEIException(ErrorMessage.MEMBER_NOT_STUDENT, Long.toString(person.getId()));
        }

        validateScore(dto.score());

        Grade grade = gradeRepository.findByTestIdAndPersonId(testId, person.getId())
            .map(existingGrade -> { // update
                existingGrade.setScore(dto.score());
                existingGrade.setGradedBy(grader);
                return existingGrade;
            })
            .orElseGet(() -> { // create new grade
                return new Grade(test, null, person, null, dto.score(), grader);
            });

        return new GradeDto(gradeRepository.save(grade));
    }

    public GradeDto gradeIndividualProject(long projectId, CreateGradeDto dto, long requesterId) {
        Project project = fetchProjectOrThrow(projectId);
        authorizeGrading(project.getUc(), requesterId);

        if (Boolean.TRUE.equals(project.getIsGroupProject())) {
            throw new DEIException(ErrorMessage.INDIVIDUAL_GRADE_NOT_ALLOWED);
        }

        if (dto.personId() == null || dto.groupId() != null) {
            throw new DEIException(ErrorMessage.INVALID_GRADE_SUBJECT);
        }

        Person person = fetchPersonOrThrow(dto.personId());
        Person grader = fetchPersonOrThrow(requesterId);
        UcMembership membership = membershipRepository.findByUcIdAndPersonId(project.getUc().getId(), person.getId())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_MEMBERSHIP, Long.toString(person.getId())));

        if (membership.getRole() != UcMembership.MembershipRole.ALUNO) {
            throw new DEIException(ErrorMessage.MEMBER_NOT_STUDENT, Long.toString(person.getId()));
        }

        validateScore(dto.score());

        Grade grade = gradeRepository.findByProjectIdAndPersonId(projectId, person.getId())
            .map(existingGrade -> {
                existingGrade.setScore(dto.score());
                existingGrade.setGradedBy(grader);
                return existingGrade;
            })
            .orElseGet(() -> {
                return new Grade(null, project, person, null, dto.score(), grader);
            });

        return new GradeDto(gradeRepository.save(grade));
    }

    public GradeDto gradeGroupProject(long projectId, CreateGradeDto dto, long requesterId) {
        Project project = fetchProjectOrThrow(projectId);
        authorizeGrading(project.getUc(), requesterId);

        if (!Boolean.TRUE.equals(project.getIsGroupProject())) {
            throw new DEIException(ErrorMessage.GROUP_NOT_ALLOWED);
        }

        if (dto.personId() != null || dto.groupId() == null) {
            throw new DEIException(ErrorMessage.INVALID_GRADE_SUBJECT);
        }

        ProjectGroup group = fetchGroupOrThrow(dto.groupId());
        if (group.getProject().getId() != projectId) {
            throw new DEIException(ErrorMessage.INVALID_GROUP);
        }
        
        validateScore(dto.score());
        
        Person grader = fetchPersonOrThrow(requesterId);
        Grade grade = gradeRepository.findByProjectIdAndGroupId(projectId, dto.groupId())
            .map(existingGrade -> {
                existingGrade.setScore(dto.score());
                existingGrade.setGradedBy(grader);
                return existingGrade;
            })
            .orElseGet(() -> {
                return new Grade(null, project, null, group, dto.score(), grader);
            });
        
        return new GradeDto(gradeRepository.save(grade));
    }

    public StudentGradesDto getStudentGrades(long ucId, long personId, long requesterId) {
        Uc uc = fetchUcOrThrow(ucId);
        Person person = fetchPersonOrThrow(personId);

        authorizeViewGrades(uc, personId, requesterId);

        List<Grade> directGrades = gradeRepository.findByPersonId(personId);
        List<Grade> groupGrades = gradeRepository.findByGroup_MembersId(personId);

        List<Grade> allGrades = new ArrayList<>();
        allGrades.addAll(directGrades);
        allGrades.addAll(groupGrades);

        List<Grade> ucGrades = allGrades.stream()
                .filter(g -> belongsToUc(g, ucId))
                .toList();

        // weighted average: sum(score * weight) / sum(weight)
        double weightedSum = 0;
        double totalWeight = 0;
        for (Grade g : ucGrades) {
            double weight = g.getTest() != null ? g.getTest().getWeight() : g.getProject().getWeight();
            weightedSum += g.getScore() * weight;
            totalWeight += weight;
        }
        Double average = totalWeight > 0 ? weightedSum / totalWeight : null;

        List<GradeDto> gradeDtos = ucGrades.stream().map(GradeDto::new).toList();
        return new StudentGradesDto(person.getId(), person.getName(), uc.getId(), uc.getName(), gradeDtos, average);
    }   
}
