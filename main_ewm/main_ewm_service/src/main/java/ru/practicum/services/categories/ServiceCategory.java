package ru.practicum.services.categories;

import categories.dto.CategoryDto;
import categories.dto.NewCategoryDto;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.services.categories.model.Category;

import java.util.List;

@Transactional(readOnly = true)
public interface ServiceCategory {

    @Transactional
    //admin
    CategoryDto create(NewCategoryDto categoryDto);

    @Transactional
    CategoryDto edit(Long id, NewCategoryDto categoryDto);

    @Transactional
    void delete(Long id);

    //user
    List<CategoryDto> getForUser(Integer from, Integer size);

    CategoryDto getForUserById(Long id);

    Category getById(Long id);
}
