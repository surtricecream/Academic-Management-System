package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.time.LocalDate;

public record CreateProjectDto(String title, LocalDate deadline, Double weight, Boolean isGroupProject, Integer maxGroupSize, long ucId) {}
