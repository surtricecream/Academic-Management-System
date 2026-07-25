package pt.ulisboa.tecnico.rnl.dei.dms.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.rnl.dei.dms.auth.dto.LoginRequest;
import pt.ulisboa.tecnico.rnl.dei.dms.auth.dto.LoginResponse;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Person person = personRepository.findByEmail(request.email())
                .orElseThrow(() -> new DEIException(ErrorMessage.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), person.getPassword())) {
            throw new DEIException(ErrorMessage.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(person.getId(), person.getEmail(), person.getType().toString());

        return new LoginResponse(token, person.getId(), person.getName(), person.getType().toString());
    }
}