package ru.practicum.services.compilations;

import compilation.dto.CompilationDto;
import compilation.dto.NewCompilationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmException;
import ru.practicum.services.compilations.mapper.MapperCompilation;
import ru.practicum.services.compilations.model.Compilation;
import ru.practicum.services.events.ServiceEvent;
import ru.practicum.services.events.model.Event;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceCompilationImpl implements ServiceCompilation {

    private final RepositoryCompilation repositoryCompilation;

    private final ServiceEvent serviceEvent;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = serviceEvent.getListEventForCompilation(newCompilationDto.getEvents());
        Compilation compilation = MapperCompilation.toCompilation(newCompilationDto, events);
        repositoryCompilation.save(compilation);
        return MapperCompilation.toResultDto(compilation, serviceEvent.getListEventDtoForCompilation(events));
    }

    @Override
    public CompilationDto edit(Long id, NewCompilationDto newCompilationDto) {
        Compilation compilation = repositoryCompilation.findById(id).orElseThrow(() -> new EwmException("Подборка не найдена", "Compilation not found", HttpStatus.NOT_FOUND));
        List<Event> events = serviceEvent.getListEventForCompilation(newCompilationDto.getEvents());
        compilation.setEvents(events);
        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }

        repositoryCompilation.save(compilation);

        return MapperCompilation.toResultDto(compilation, serviceEvent.getListEventDtoForCompilation(events));
    }

    @Override
    public void delete(Long id) {
        Compilation compilation = repositoryCompilation.findById(id).orElseThrow(() -> new EwmException("Подборка не найдена", "Compilation not found", HttpStatus.NOT_FOUND));
        repositoryCompilation.deleteById(id);
    }

    //public
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = repositoryCompilation.findByPinned(pinned, PageRequest.of(from, size)).getContent();
        List<CompilationDto> results = new ArrayList<>();
        if (compilations.size() != 0) {
            for (Compilation compilation : compilations) {
                results.add(MapperCompilation.toResultDto(compilation, serviceEvent.getListEventDtoForCompilation(compilation.getEvents())));
            }
        }
        return results;
    }

    @Override
    public CompilationDto getCompilationsById(Long compId) {
        Compilation compilation = repositoryCompilation.findById(compId).orElseThrow(() -> new EwmException("Подборка не найдена", "Compilation not found", HttpStatus.NOT_FOUND));
        return MapperCompilation.toResultDto(compilation, serviceEvent.getListEventDtoForCompilation(compilation.getEvents()));
    }
}
