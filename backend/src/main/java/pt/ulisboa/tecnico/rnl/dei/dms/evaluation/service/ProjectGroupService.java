package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.ProjectGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.ProjectGroupDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateProjectGroupDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.ProjectGroupRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.domain.UcMembership;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.repository.UcMembershipRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class ProjectGroupService {

    @Autowired
    private ProjectGroupRepository groupRepository;

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private UcMembershipRepository membershipRepository;

    private Person fetchPersonOrThrow(long id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
    }

    private Project fetchProjectOrThrow(long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PROJECT, Long.toString(id)));
    }

    private ProjectGroup fetchGroupOrThrow(long id) {
        return groupRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_GROUP, Long.toString(id)));
    }

    private void authorizeProjectGroupChange(Project project, long requesterId) {
        Person requester = fetchPersonOrThrow(requesterId);
        if (requester.getType() != Person.PersonType.ADMINISTRATOR && requesterId != project.getUc().getRegente().getId()) {
            throw new DEIException(ErrorMessage.NOT_AUTHORIZED);
        }
    }

    public List<ProjectGroupDto> getGroups(long projectId) {
        fetchProjectOrThrow(projectId);

        return groupRepository.findByProjectId(projectId).stream().map(ProjectGroupDto::new).toList();
    }

    public ProjectGroupDto createGroup(long projectId, CreateProjectGroupDto dto, long requesterId) {
        Project project = fetchProjectOrThrow(projectId);
        authorizeProjectGroupChange(project, requesterId);

        if (!Boolean.TRUE.equals(project.getIsGroupProject())) {
            throw new DEIException(ErrorMessage.GROUP_NOT_ALLOWED);
        }

        List<Person> members = personRepository.findAllById(dto.memberIds());
        if (members.size() != dto.memberIds().size()) {
            throw new DEIException(ErrorMessage.NO_SUCH_PERSON);
        }

        if (dto.memberIds().size() > project.getMaxGroupSize()) {
            throw new DEIException(ErrorMessage.GROUP_SIZE_EXCEEDED);
        }

        long ucId = project.getUc().getId();
        for (Long memberId : dto.memberIds()) {
            UcMembership membership = membershipRepository.findByUcIdAndPersonId(ucId, memberId)
                    .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_MEMBERSHIP, Long.toString(memberId)));
            if (membership.getRole() != UcMembership.MembershipRole.ALUNO) {
                throw new DEIException(ErrorMessage.MEMBER_NOT_STUDENT, Long.toString(memberId));
            }
        }

        ProjectGroup projectGroup = new ProjectGroup(project, members);
        return new ProjectGroupDto(groupRepository.save(projectGroup));
    }

    public void deleteGroup(long groupId, long requesterId) {
        ProjectGroup group = fetchGroupOrThrow(groupId);
        authorizeProjectGroupChange(group.getProject(), requesterId);
        groupRepository.deleteById(groupId);
    }

    public List<ProjectGroupDto> autoAssignGroups(long projectId, long requesterId) {
        Project project = fetchProjectOrThrow(projectId);
        authorizeProjectGroupChange(project, requesterId);

        if (!Boolean.TRUE.equals(project.getIsGroupProject())) {
            throw new DEIException(ErrorMessage.GROUP_NOT_ALLOWED);
        }

        List<ProjectGroup> existingGroups = groupRepository.findByProjectId(projectId);
        if (!existingGroups.isEmpty()) {
            throw new DEIException(ErrorMessage.GROUPS_ALREADY_EXIST);
        }

        List<UcMembership> studentMemberships = membershipRepository.findByUcIdAndRole(
            project.getUc().getId(), 
            UcMembership.MembershipRole.ALUNO
        );

        if (studentMemberships.isEmpty()) {
            throw new DEIException(ErrorMessage.NO_STUDENTS_IN_UC);
        }

        List<Person> students = studentMemberships.stream()
            .map(UcMembership::getPerson)
            .collect(Collectors.toList());

        Collections.shuffle(students);

        int maxGroupSize = project.getMaxGroupSize();
        List<ProjectGroupDto> createdGroups = new ArrayList<>();

        for (int i = 0; i < students.size(); i += maxGroupSize) {
            int end = Math.min(i + maxGroupSize, students.size());
            List<Person> groupMembers = students.subList(i, end);

            ProjectGroup projectGroup = new ProjectGroup(project, groupMembers);
            createdGroups.add(new ProjectGroupDto(groupRepository.save(projectGroup)));
        }

        return createdGroups;
    }
}
