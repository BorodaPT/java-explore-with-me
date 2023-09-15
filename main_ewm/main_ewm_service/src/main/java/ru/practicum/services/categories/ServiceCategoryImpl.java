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
import ru.practicum.services.events.model.Event;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ServiceCategoryImpl implements ServiceCategory {

    private final RepositoryCategory repositoryCategory;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto categoryDto) {
        if (categoryDto.getName() == null) {
            throw new EwmException("Некорректное заполенние параметров катагории", "Category bad field", HttpStatus.NOT_FOUND);
        }

        if (repositoryCategory.existsByName(categoryDto.getName())) {
            throw new EwmException("Наименование не уникально", "Category double", HttpStatus.CONFLICT);
        }

        return MapperCategory.toDTO(repositoryCategory.save(MapperCategory.toCategory(categoryDto)));

    }

    @Transactional
    @Override
    public CategoryDto edit(Long id, NewCategoryDto categoryDto) {
        Category category = repositoryCategory.findById(id).orElseThrow(() -> new EwmException("Категория для изменения не найдена", "Category not found", HttpStatus.NOT_FOUND));
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new EwmException("Не указано наименование категории", "Category empty", HttpStatus.BAD_REQUEST);
        }
        try {
            category.setName(categoryDto.getName());
            return MapperCategory.toDTO(repositoryCategory.save(MapperCategory.toCategory(categoryDto)));
        } catch (RuntimeException e) {
            throw new EwmException("Наименование не уникально", "Category double", HttpStatus.CONFLICT);
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        try {
            Category category = repositoryCategory.findById(id).orElseThrow(() -> new EwmException("Категория не найдена", "Category not found", HttpStatus.NOT_FOUND));
            Category categoryEvent = repositoryCategory.findEventCategory(id);
            if (categoryEvent != null) {
                throw new EwmException("У категории есть эвенты", "Category have event", HttpStatus.CONFLICT);
            }
            repositoryCategory.deleteById(id);
        } catch (RuntimeException e) {
            throw new EwmException("Не удалось удалить категорию", "Cant delete category", HttpStatus.CONFLICT);
        }
    }

    //user
    @Override
    public List<CategoryDto> getForUser(Integer from, Integer size) {
        return MapperCategory.toDTO(repositoryCategory.findAllCat(PageRequest.of(from / size, size)).getContent());
    }

    @Override
    public CategoryDto getForUserById(Long id) {
        Category category = repositoryCategory.findById(id).orElseThrow(() -> new EwmException("Катекория " + id + "не найденa", "Category not found", HttpStatus.NOT_FOUND));
        return MapperCategory.toDTO(category);
    }

    @Override
    public Category getById(Long id) {
        return repositoryCategory.findById(id).orElseThrow(() -> new EwmException("Катекория " + id + "не найденa", "Category not found", HttpStatus.NOT_FOUND));
    }
}
