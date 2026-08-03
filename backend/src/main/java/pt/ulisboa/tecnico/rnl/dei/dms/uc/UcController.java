package pt.ulisboa.tecnico.rnl.dei.dms.uc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import pt.ulisboa.tecnico.rnl.dei.dms.uc.dto.CreateUcDto;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.dto.UcDto;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.service.UcService;

@RestController
public class UcController {

    @Autowired
    private UcService ucService;

    @GetMapping("/ucs")
    public List<UcDto> getUcs() {
        return ucService.getUcs();
    }

    @PostMapping("/ucs")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public UcDto createUc(@RequestBody CreateUcDto dto) {
        return ucService.createUc(dto);
    }

    @GetMapping("/ucs/{id}")
    public UcDto getUc(@PathVariable long id) {
        return ucService.getUc(id);
    }

    @PutMapping("/ucs/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public UcDto updateUc(@PathVariable long id, @RequestBody CreateUcDto dto) {
        return ucService.updateUc(id, dto);
    }

    @DeleteMapping("/ucs/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteUc(@PathVariable long id) {
        ucService.deleteUc(id);
    }
}