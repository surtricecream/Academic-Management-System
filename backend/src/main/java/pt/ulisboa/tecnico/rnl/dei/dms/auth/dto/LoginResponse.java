package pt.ulisboa.tecnico.rnl.dei.dms.auth.dto;

public record LoginResponse(String token, long personId, String name, String type) {}