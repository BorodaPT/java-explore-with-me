package ru.practicum.services.compilations;

import compilation.dto.CompilationDto;
import compilation.dto.CompilationDtoEdit;
import compilation.dto.NewCompilationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmException;
import ru.practicum.services.compilations.mapper.MapperCompilation;
import ru.practicum.services.compilations.model.Compilation;
import ru.practicum.services.events.EventService;
import ru.practicum.services.events.model.Event;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventService eventService;

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {

        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }

        List<Event> events = eventService.getListEventForCompilation(newCompilationDto.getEvents());
        Compilation compilation = MapperCompilation.toCompilation(newCompilationDto, events);
        compilationRepository.save(compilation);
        return MapperCompilation.toResultDto(compilation, eventService.getListEventDtoForCompilation(events));
    }

    @Transactional
    @Override
    public CompilationDto edit(Long id, CompilationDtoEdit compilationDto) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> new EwmException("Подборка не найдена", "Compilation not found", HttpStatus.NOT_FOUND));

        List<Event> events = eventService.getListEventForCompilation(compilationDto.getEvents());
        if (events.size() != 0) {
            compilation.setEvents(events);
        }
        if (compilationDto.getTitle() != null && !compilationDto.getTitle().isBlank()) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }

        compilationRepository.save(compilation);

        return MapperCompilation.toResultDto(compilation, eventService.getListEventDtoForCompilation(events));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> new EwmException("Подборка не найдена", "Compilation not found", HttpStatus.NOT_FOUND));
        compilationRepository.deleteById(id);
    }

    //public
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = (pinned == null) ? compilationRepository.findAll(PageRequest.of(from / size, size)).getContent() : compilationRepository.findAllByPinned(pinned, PageRequest.of(from / size, size));
        List<CompilationDto> results = new ArrayList<>();
        if (compilations.size() != 0) {
            for (Compilation compilation : compilations) {
                results.add(MapperCompilation.toResultDto(compilation, eventService.getListEventDtoForCompilation(compilation.getEvents())));
            }
        }
        return results;
    }

    @Override
    public CompilationDto getCompilationsById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new EwmException("Подборка не найдена", "Compilation not found", HttpStatus.NOT_FOUND));

        return MapperCompilation.toResultDto(compilation, eventService.getListEventDtoForCompilation(compilation.getEvents()));
    }
}
