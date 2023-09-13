package ru.practicum.services.compilations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.services.compilations.model.Compilation;


@Repository
public interface RepositoryCompilation extends JpaRepository<Compilation, Long> {

    @Query(value = "select c.* from compilations as c where c.pinned = ?1 ", nativeQuery = true)
    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);


}
