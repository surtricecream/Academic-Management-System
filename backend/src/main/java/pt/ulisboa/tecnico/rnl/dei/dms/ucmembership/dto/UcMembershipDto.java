package pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.domain.UcMembership;

public record UcMembershipDto(long id, long personid, String personName, String istId, String email, String role) {
    public UcMembershipDto(UcMembership ucMembership) {
        this(ucMembership.getId(), ucMembership.getPerson().getId(), ucMembership.getPerson().getName(), 
                ucMembership.getPerson().getIstId(), ucMembership.getPerson().getEmail(), ucMembership.getRole().toString());
    }    
}
