package ru.practicum.services.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.services.categories.model.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "select c.* from categories c " +
                   "where c.name = ?1 AND c.id <> ?2", nativeQuery = true)
    Category findByNameAndNotId(String name, Long id);

    @Query(value = "select c from Category c")
    Page<Category> findAllCat(Pageable pageable);

    Boolean existsByName(String name);

    @Query(value = "select c.* from categories as c " +
            "join events e on e.category_id = c.id " +
            "where c.id = ?1 " +
            "limit 1", nativeQuery = true)
    Category findEventCategory(Long idCat);

}
