package ru.practicum.services.categories;

import categories.dto.CategoryDto;
import categories.dto.NewCategoryDto;
import ru.practicum.services.categories.model.Category;

import java.util.List;

public interface CategoryService {

    //admin
    CategoryDto create(NewCategoryDto categoryDto);

    CategoryDto edit(Long id, NewCategoryDto categoryDto);

    void delete(Long id);

    //user
    List<CategoryDto> getForUser(Integer from, Integer size);

    CategoryDto getForUserById(Long id);

    Category getById(Long id);
}
