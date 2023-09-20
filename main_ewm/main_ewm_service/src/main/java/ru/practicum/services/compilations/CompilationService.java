package ru.practicum.services.compilations;

import compilation.dto.CompilationDto;
import compilation.dto.CompilationDtoEdit;
import compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    //admin
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto edit(Long id, CompilationDtoEdit compilationDto);

    void delete(Long id);

    //public
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationsById(Long compId);

}
