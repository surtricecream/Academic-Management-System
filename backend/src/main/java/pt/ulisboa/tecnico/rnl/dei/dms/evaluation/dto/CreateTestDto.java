package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.time.LocalDate;

public record CreateTestDto(String title, LocalDate date, Double weight, long ucId) {}
