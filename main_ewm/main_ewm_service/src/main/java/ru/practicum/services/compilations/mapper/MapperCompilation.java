package ru.practicum.services.compilations.mapper;

import compilation.dto.CompilationDto;
import compilation.dto.NewCompilationDto;
import events.dto.EventShortDto;
import ru.practicum.services.compilations.model.Compilation;
import ru.practicum.services.events.model.Event;

import java.util.List;

public class MapperCompilation {

    public static Compilation toCompilation(NewCompilationDto dto, List<Event> events) {
        return new Compilation(dto.getTitle(), dto.getPinned(), events);
    }

    public static CompilationDto toResultDto(Compilation compilation, List<EventShortDto> events) {
        return new CompilationDto(
                events,
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle());
    }


}
