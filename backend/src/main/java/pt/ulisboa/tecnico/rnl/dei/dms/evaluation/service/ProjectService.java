package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.ProjectDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateProjectDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.repository.UcRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UcRepository ucRepository;

    @Autowired
    private PersonRepository personRepository;

    private Uc fetchUcOrThrow(long id) {
        return ucRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_UC, Long.toString(id)));
    }

    private Person fetchPersonOrThrow(long id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
    }

    private Project fetchProjectOrThrow(long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PROJECT, Long.toString(id)));
    }

    private void authorizeProjectChange(Uc uc, long requesterId) {
        Person requester = fetchPersonOrThrow(requesterId);
        if (requester.getType() != Person.PersonType.ADMINISTRATOR && requesterId != uc.getRegente().getId()) {
            throw new DEIException(ErrorMessage.NOT_AUTHORIZED);
        }
    }

    public List<ProjectDto> getProjectsByUc(long ucId) {
        fetchUcOrThrow(ucId);

        return projectRepository.findByUcId(ucId).stream().map(ProjectDto::new).toList();
    }

    public ProjectDto getProject(long id) {
        return new ProjectDto(fetchProjectOrThrow(id));
    }

    public ProjectDto createProject(long ucId, CreateProjectDto dto, long requesterId) {
        Uc uc = fetchUcOrThrow(ucId);
        authorizeProjectChange(uc, requesterId);

        if (Boolean.TRUE.equals(dto.isGroupProject()) && dto.maxGroupSize() == null) {
            throw new DEIException(ErrorMessage.INVALID_GROUP_SIZE);
        }

        Project project = new Project(dto.title(), dto.deadline(), dto.weight(),
                            dto.isGroupProject(), dto.maxGroupSize(), uc);
        return new ProjectDto(projectRepository.save(project));
    }

    public ProjectDto updateProject(long id, CreateProjectDto dto, long requesterId) {
        Uc uc = fetchUcOrThrow(dto.ucId());
        authorizeProjectChange(uc, requesterId);

        Project project = fetchProjectOrThrow(id);
        if (!project.getUc().getId().equals(uc.getId())) {
            throw new DEIException(ErrorMessage.NO_SUCH_PROJECT);
        }

        project.setTitle(dto.title());
        project.setDeadline(dto.deadline());
        project.setWeight(dto.weight());
        project.setIsGroupProject(dto.isGroupProject());
        project.setMaxGroupSize(dto.maxGroupSize());
        project.setUc(uc);

        return new ProjectDto(projectRepository.save(project));
    }

    public void deleteProject(long id, long requesterId) {
        Project project = fetchProjectOrThrow(id);
        Uc uc = project.getUc();
        authorizeProjectChange(uc, requesterId);
        projectRepository.deleteById(id);
    }
}
