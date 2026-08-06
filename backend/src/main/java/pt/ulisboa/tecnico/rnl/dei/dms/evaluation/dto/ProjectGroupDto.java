package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.util.List;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.ProjectGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

public record ProjectGroupDto(long id, long projectId, String projectTitle, List<String> memberNames, List<Long> memberIds) {
    public ProjectGroupDto(ProjectGroup group) {
        this(group.getId(), group.getProject().getId(), group.getProject().getTitle(),
                group.getMembers().stream().map(Person::getName).toList(),
                group.getMembers().stream().map(Person::getId).toList());
    }
}