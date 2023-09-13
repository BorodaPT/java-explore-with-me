package ru.practicum.services.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.services.categories.model.Category;


@Repository
public interface RepositoryCategory extends JpaRepository<Category, Long> {

    @Query(value = "select c from Category c")
    Page<Category> findAllCat(Pageable pageable);
}
