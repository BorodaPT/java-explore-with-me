package ru.practicum.controller;

import categories.dto.CategoryDto;
import categories.dto.NewCategoryDto;
import compilation.dto.CompilationDto;
import compilation.dto.CompilationDtoEdit;
import compilation.dto.NewCompilationDto;
import events.dto.EventFullDto;
import ru.practicum.services.events.model.StatusEvent;
import ru.practicum.services.events.model.UpdateEventAdminRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.services.categories.CategoryService;
import ru.practicum.services.compilations.CompilationService;
import ru.practicum.services.events.EventService;
import ru.practicum.services.users.UserService;
import users.dto.UserDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Validated
public class AdminController {

    private final UserService userService;

    private final CategoryService categoryService;

    private final EventService eventService;

    private final CompilationService compilationService;

    //categories
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/categories")
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.create(newCategoryDto);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto edit(@PathVariable("catId") Long id,
                            @Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.edit(id, newCategoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/categories/{catId}")
    public void delete(@PathVariable("catId") Long id) {
        categoryService.delete(id);
    }

    //events
    @GetMapping("/events")
    public List<EventFullDto> findEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                         @RequestParam(name = "states", required = false) List<StatusEvent> states,
                                         @RequestParam(name = "categories", required = false) List<Long> categories,
                                         @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                         @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return eventService.getEventForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto editEvent(@PathVariable("eventId") Long id,
                                  @Valid @RequestBody UpdateEventAdminRequest request) {
        return eventService.editEventAdmin(id, request);
    }

    //users
    @GetMapping("/users")
    public List<UserDto> findUsers(@RequestParam(name = "ids", required = false) List<Long> idUsers,
                                   @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                   @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return userService.get(idUsers, start, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable("userId") Long id) {
        userService.delete(id);
    }

    //compilations
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("compilations")
    public CompilationDto createCompilations(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    @PatchMapping("compilations/{compId}")
    public CompilationDto editCompilations(@PathVariable("compId") Long id,
                                           @Valid @RequestBody CompilationDtoEdit compilationDto) {
        return compilationService.edit(id, compilationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("compilations/{compId}")
    public void deleteCompilations(@PathVariable("compId") Long id) {
        compilationService.delete(id);
    }
}
