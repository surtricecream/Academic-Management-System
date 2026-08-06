package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Test;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateTestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.TestDto;
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

    public List<TestDto> getTestsByUc(long ucId) {
        fetchUcOrThrow(ucId);

        return testRepository.findByUcId(ucId).stream().map(TestDto::new).toList();
    }

    public TestDto getTest(long id) {
        return new TestDto(fetchTestOrThrow(id));
    }

    public TestDto createTest(long ucId, CreateTestDto dto, long requesterId) {
        Uc uc = fetchUcOrThrow(ucId);
        authorizeTestChange(uc, requesterId);

        Test test = new Test(dto.title(), dto.date(), dto.weight(), uc);
        return new TestDto(testRepository.save(test));
    }

    public TestDto updateTest(long id, CreateTestDto dto, long requesterId) {
        Uc uc = fetchUcOrThrow(dto.ucId());
        authorizeTestChange(uc, requesterId);

        Test test = fetchTestOrThrow(id);
        if (!test.getUc().getId().equals(uc.getId())) {
            throw new DEIException(ErrorMessage.NO_SUCH_TEST);
        }

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