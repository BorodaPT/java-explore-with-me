package ru.practicum.services.categories.mapper;

import categories.dto.CategoryDto;
import categories.dto.NewCategoryDto;
import ru.practicum.services.categories.model.Category;

import java.util.ArrayList;
import java.util.List;

public class MapperCategory {

    public static CategoryDto toDTO(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static NewCategoryDto toShortDTO(Category category) {
        return new NewCategoryDto(category.getName());
    }

    public static Category toCategory(CategoryDto userDTO) {
        return new Category(userDTO.getId(), userDTO.getName());
    }

    public static Category toCategory(NewCategoryDto userDTO) {
        return new Category(userDTO.getName());
    }

    public static Category toShortCategory(NewCategoryDto userDTO) {
        return new Category(userDTO.getName());
    }

    public static List<CategoryDto> toDTO(Iterable<Category> categorys) {
        List<CategoryDto> result = new ArrayList<>();
        if (categorys != null) {
            for (Category category : categorys) {
                result.add(toDTO(category));
            }
        }
        return result;
    }
}
