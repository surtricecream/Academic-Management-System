package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Test;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateTestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.TestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.TestRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.repository.UcRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private UcRepository ucRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Uc fetchUcOrThrow(long id) {
        return ucRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_UC, Long.toString(id)));
    }

    private Person fetchPersonOrThrow(long id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
    }

    private Test fetchTestOrThrow(long id) {
        return testRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_TEST, Long.toString(id)));
    }

    private void authorizeTestChange(Uc uc, long requesterId) {
        Person requester = fetchPersonOrThrow(requesterId);
        if (requester.getType() != Person.PersonType.ADMINISTRATOR && requesterId != uc.getRegente().getId()) {
            throw new DEIException(ErrorMessage.NOT_AUTHORIZED);
        }
    }

    private void validateWeightBudget(Uc uc, double newWeight, Long excludeTestId) {
        double existingTestWeight = testRepository.findByUcId(uc.getId()).stream()
                .filter(t -> excludeTestId == null || !t.getId().equals(excludeTestId))
                .mapToDouble(Test::getWeight).sum();
        double existingProjectWeight = projectRepository.findByUcId(uc.getId()).stream()
                .mapToDouble(Project::getWeight).sum();

        if (existingTestWeight + existingProjectWeight + newWeight > 1.0001) { // float rounding
            throw new DEIException(ErrorMessage.WEIGHT_BUDGET_EXCEEDED);
        }
    }

    public List<TestDto> getTestsByUc(long ucId) {
        fetchUcOrThrow(ucId);

        return testRepository.findByUcId(ucId).stream().map(TestDto::new).toList();
    }

    public TestDto getTest(long id) {
        return new TestDto(fetchTestOrThrow(id));
    }

    public TestDto createTest(long ucId, CreateTestDto dto, long requesterId) {
        Uc uc = fetchUcOrThrow(ucId);
        if (dto.date() == null || dto.date().isBefore(LocalDate.now())) {
            throw new DEIException(ErrorMessage.INVALID_TEST_DATA, "data não pode ser no passado");
        }

        authorizeTestChange(uc, requesterId);
        validateWeightBudget(uc, dto.weight(), null);

        Test test = new Test(dto.title(), dto.date(), dto.weight(), uc);
        return new TestDto(testRepository.save(test));
    }

    public TestDto updateTest(long id, CreateTestDto dto, long requesterId) {
        Uc uc = fetchUcOrThrow(dto.ucId());
        if (dto.date() == null || dto.date().isBefore(LocalDate.now())) {
            throw new DEIException(ErrorMessage.INVALID_TEST_DATA, "data não pode ser no passado");
        }

        authorizeTestChange(uc, requesterId);

        Test test = fetchTestOrThrow(id);
        if (!test.getUc().getId().equals(uc.getId())) {
            throw new DEIException(ErrorMessage.NO_SUCH_TEST);
        }

        validateWeightBudget(uc, dto.weight(), id);

        test.setTitle(dto.title());
        test.setDate(dto.date());
        test.setWeight(dto.weight());
        test.setUc(uc);
        
        return new TestDto(testRepository.save(test));
    }

    public void deleteTest(long id, long requesterId) {
        Test test = fetchTestOrThrow(id);
        Uc uc = test.getUc();
        authorizeTestChange(uc, requesterId);
        testRepository.deleteById(id);
    }
}