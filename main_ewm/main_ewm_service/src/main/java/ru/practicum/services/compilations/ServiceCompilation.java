package ru.practicum.services.compilations;

import compilation.dto.CompilationDto;
import compilation.dto.NewCompilationDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ServiceCompilation {

    @Transactional
    //admin
    CompilationDto create(NewCompilationDto newCompilationDto);

    @Transactional
    CompilationDto edit(Long id, NewCompilationDto newCompilationDto);

    @Transactional
    void delete(Long id);

    //public
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationsById(Long compId);

}
