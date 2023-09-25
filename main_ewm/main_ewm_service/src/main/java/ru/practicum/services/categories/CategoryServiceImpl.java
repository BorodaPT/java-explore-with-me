package ru.practicum.services.categories;

import categories.dto.CategoryDto;
import categories.dto.NewCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmException;
import ru.practicum.services.categories.mapper.MapperCategory;
import ru.practicum.services.categories.model.Category;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto categoryDto) {

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new EwmException("Наименование не уникально", "Category double", HttpStatus.CONFLICT);
        }

        return MapperCategory.toDTO(categoryRepository.save(MapperCategory.toCategory(categoryDto)));
    }

    @Transactional
    @Override
    public CategoryDto edit(Long id, NewCategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EwmException("Категория для изменения не найдена", "Category not found", HttpStatus.NOT_FOUND));

        Category categoryName = categoryRepository.findByNameAndNotId(categoryDto.getName(), id);
        if (categoryName != null) {
            throw new EwmException("Наименование не уникально", "Category double", HttpStatus.CONFLICT);
        }
        category.setName(categoryDto.getName());
        return MapperCategory.toDTO(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        try {
            Category category = categoryRepository.findById(id).orElseThrow(() -> new EwmException("Категория не найдена", "Category not found", HttpStatus.NOT_FOUND));
            Category categoryEvent = categoryRepository.findEventCategory(id);
            if (categoryEvent != null) {
                throw new EwmException("У категории есть эвенты", "Category have event", HttpStatus.CONFLICT);
            }
            categoryRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new EwmException("Не удалось удалить категорию", "Cant delete category", HttpStatus.CONFLICT);
        }
    }

    //user
    @Override
    public List<CategoryDto> getForUser(Integer from, Integer size) {
        return MapperCategory.toDTO(categoryRepository.findAllCat(PageRequest.of(from / size, size)).getContent());
    }

    @Override
    public CategoryDto getForUserById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EwmException("Катекория " + id + "не найденa", "Category not found", HttpStatus.NOT_FOUND));
        return MapperCategory.toDTO(category);
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EwmException("Катекория " + id + "не найденa", "Category not found", HttpStatus.NOT_FOUND));
    }
}
