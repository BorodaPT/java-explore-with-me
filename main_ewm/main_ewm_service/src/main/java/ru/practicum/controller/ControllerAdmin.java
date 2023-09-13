package ru.practicum.controller;

import categories.dto.CategoryDto;
import categories.dto.NewCategoryDto;
import compilation.dto.CompilationDto;
import compilation.dto.NewCompilationDto;
import events.dto.EventFullDto;
import events.model.UpdateEventAdminRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.services.categories.ServiceCategory;
import ru.practicum.services.compilations.ServiceCompilation;
import ru.practicum.services.events.ServiceEvent;
import ru.practicum.services.users.ServiceUser;
import users.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class ControllerAdmin {

    private final ServiceUser serviceUser;

    private final ServiceCategory serviceCategory;

    private final ServiceEvent serviceEvent;

    private final ServiceCompilation serviceCompilation;

    //categories
    @PostMapping("/categories")
    public CategoryDto create(@RequestBody NewCategoryDto newCategoryDto) {
        return serviceCategory.create(newCategoryDto);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto edit(@PathVariable("catId") Long id,
                            @RequestBody NewCategoryDto newCategoryDto) {
        return serviceCategory.edit(id, newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void delete(@PathVariable("catId") Long id) {
        serviceCategory.delete(id);
    }

    //events
    @GetMapping("/events")
    public List<EventFullDto> findEvents(@RequestParam(name = "users", required = false) List<Integer> users,
                                         @RequestParam(name = "states", required = false) List<String> states,
                                         @RequestParam(name = "categories", required = false) List<Integer> categories,
                                         @RequestParam(name = "rangeStart") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(name = "rangeEnd") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                         @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return serviceEvent.getEventForAdmin(users, states, categories, rangeStart, rangeEnd, start, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto editEvent(@PathVariable("eventId") Long id,
                                  @RequestBody UpdateEventAdminRequest request) {
        return serviceEvent.editEventAdmin(id, request);
    }

    //users
    @GetMapping("/users")
    public List<UserDto> findUsers(@RequestParam(name = "ids", required = false) List<Integer> idUsers,
                                   @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                   @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return serviceUser.get(idUsers, start, size);
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return serviceUser.create(userDto);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable("eventId") Long id) {
        serviceUser.delete(id);
    }

    //compilations
    @PostMapping("compilations")
    public CompilationDto createCompilations(@RequestBody NewCompilationDto newCompilationDto) {
        return serviceCompilation.create(newCompilationDto);
    }

    @PatchMapping("compilations/{compId}")
    public CompilationDto editCompilations(@PathVariable("eventId") Long id,
                                           @RequestBody NewCompilationDto newCompilationDto) {
        return serviceCompilation.edit(id, newCompilationDto);
    }

    @DeleteMapping("compilations/{compId}")
    public void deleteCompilations(@PathVariable("eventId") Long id) {
        serviceCompilation.delete(id);
    }
}
