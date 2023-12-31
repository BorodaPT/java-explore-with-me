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
import ru.practicum.services.categories.CategoryService;
import ru.practicum.services.compilations.CompilationService;
import ru.practicum.services.events.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
public class PublicController {

    private final CompilationService compilationService;

    private final EventService eventService;

    private final CategoryService categoryService;

    //Compilation
    @GetMapping("/compilations")
    public List<CompilationDto> findCompilation(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                                @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, start, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto findCompilationId(@PathVariable("compId") Long id) {
        return compilationService.getCompilationsById(id);
    }

    //categories
    @GetMapping("/categories")
    public List<CategoryDto> findCategories(@RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return categoryService.getForUser(start, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto findCategoriesId(@PathVariable("catId") Long id) {
        return categoryService.getForUserById(id);
    }

    //events
    @GetMapping("/events")
    public List<EventShortDto> findEvents(@RequestParam(name = "text", required = false) String text,
                                          @RequestParam(name = "categories", required = false) List<Long> categories,
                                          @RequestParam(name = "paid", required = false) Boolean isPaid,
                                          @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean isOnlyAvailable,
                                          @RequestParam(name = "sort", required = false, defaultValue = "EVENT_DATE") SortEvent sort,
                                          @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                          @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                          HttpServletRequest request) {
        return eventService.getEventsPublic(text, categories, isPaid, rangeStart, rangeEnd, isOnlyAvailable, sort, start, size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto findEventsId(@PathVariable("id") Long id, HttpServletRequest request) {
        return eventService.getEventPublicForUserById(id, request);
    }

}
