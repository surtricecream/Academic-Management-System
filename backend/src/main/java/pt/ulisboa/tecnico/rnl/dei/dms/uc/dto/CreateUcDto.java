package pt.ulisboa.tecnico.rnl.dei.dms.uc.dto;

import java.util.List;
public record CreateUcDto(String code, String name, Integer semester, Integer ects, long regenteId, List<Long> courseIds) {}
