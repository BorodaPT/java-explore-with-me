package ru.practicum.controller;

import categories.dto.CategoryDto;
import compilation.dto.CompilationDto;
import events.dto.EventFullDto;
import events.dto.EventShortDto;
import ru.practicum.services.events.enum_events.SortEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.services.categories.ServiceCategory;
import ru.practicum.services.compilations.ServiceCompilation;
import ru.practicum.services.events.ServiceEvent;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
public class ControllerPublic {

    private final ServiceCompilation serviceCompilation;

    private final ServiceEvent serviceEvent;

    private final ServiceCategory serviceCategory;

    //Compilation
    @GetMapping("/compilations")
    public List<CompilationDto> findCompilation(@RequestParam(name = "pinned", required = false, defaultValue = "true") Boolean pinned,
                                                @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                                @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return serviceCompilation.getCompilations(pinned, start, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto findCompilationId(@PathVariable("compId") Long id) {
        return serviceCompilation.getCompilationsById(id);
    }

    //categories
    @GetMapping("/categories")
    public List<CategoryDto> findCategories(@RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return serviceCategory.getForUser(start, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto findCategoriesId(@PathVariable("catId") Long id) {
        return serviceCategory.getForUserById(id);
    }

    //events
    @GetMapping("/events")
    public List<EventShortDto> findEvents(@RequestParam(name = "text", required = false) String text,
                                          @RequestParam(name = "categories", required = false) List<Long> categories,
                                          @RequestParam(name = "paid", required = false, defaultValue = "false") Boolean isPaid,
                                          @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean isOnlyAvailable,
                                          @RequestParam(name = "sort", required = false, defaultValue = "EVENT_DATE") SortEvent sort,
                                          @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                          @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                          HttpServletRequest request) {
        return serviceEvent.getEventsPublic(text, categories, isPaid, rangeStart, rangeEnd, isOnlyAvailable, sort, start, size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto findEventsId(@PathVariable("id") Long id, HttpServletRequest request) {
        return serviceEvent.getEventPublicForUserById(id, request);
    }

}
