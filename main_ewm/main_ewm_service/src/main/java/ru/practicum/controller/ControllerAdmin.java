package ru.practicum.controller;

import categories.dto.CategoryDto;
import categories.dto.NewCategoryDto;
import compilation.dto.CompilationDto;
import compilation.dto.NewCompilationDto;
import events.dto.EventFullDto;
import ru.practicum.services.events.model.StatusEvent;
import ru.practicum.services.events.model.UpdateEventAdminRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.services.categories.ServiceCategory;
import ru.practicum.services.compilations.ServiceCompilation;
import ru.practicum.services.events.ServiceEvent;
import ru.practicum.services.users.ServiceUser;
import users.dto.UserDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Validated
public class ControllerAdmin {

    private final ServiceUser serviceUser;

    private final ServiceCategory serviceCategory;

    private final ServiceEvent serviceEvent;

    private final ServiceCompilation serviceCompilation;

    //categories
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/categories")
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return serviceCategory.create(newCategoryDto);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto edit(@PathVariable("catId") Long id,
                            @Valid @RequestBody NewCategoryDto newCategoryDto) {
        return serviceCategory.edit(id, newCategoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/categories/{catId}")
    public void delete(@PathVariable("catId") Long id) {
        serviceCategory.delete(id);
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
        return serviceEvent.getEventForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto editEvent(@PathVariable("eventId") Long id,
                                  @Valid @RequestBody UpdateEventAdminRequest request) {
        return serviceEvent.editEventAdmin(id, request);
    }

    //users
    @GetMapping("/users")
    public List<UserDto> findUsers(@RequestParam(name = "ids", required = false) List<Long> idUsers,
                                   @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                   @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return serviceUser.get(idUsers, start, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return serviceUser.create(userDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable("userId") Long id) {
        serviceUser.delete(id);
    }

    //compilations
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("compilations")
    public CompilationDto createCompilations(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return serviceCompilation.create(newCompilationDto);
    }

    @PatchMapping("compilations/{compId}")
    public CompilationDto editCompilations(@PathVariable("compId") Long id,
                                           @Valid @RequestBody NewCompilationDto newCompilationDto) {
        return serviceCompilation.edit(id, newCompilationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("compilations/{compId}")
    public void deleteCompilations(@PathVariable("compId") Long id) {
        serviceCompilation.delete(id);
    }
}
