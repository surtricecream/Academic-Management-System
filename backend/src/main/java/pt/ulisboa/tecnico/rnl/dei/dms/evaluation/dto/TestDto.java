package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.time.LocalDate;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Test;

public record TestDto(long id, String title, LocalDate date, Double weight, long ucId, String ucName) {
    public TestDto(Test test) {
        this(test.getId(), test.getTitle(), test.getDate(), test.getWeight(), 
                test.getUc().getId(), test.getUc().getName());
    }
}
