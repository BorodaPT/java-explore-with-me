package ru.practicum.services.compilations;

import compilation.dto.CompilationDto;
import compilation.dto.NewCompilationDto;

import java.util.List;

public interface ServiceCompilation {

    //admin
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto edit(Long id, NewCompilationDto newCompilationDto);

    void delete(Long id);

    //public
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationsById(Long compId);

}
