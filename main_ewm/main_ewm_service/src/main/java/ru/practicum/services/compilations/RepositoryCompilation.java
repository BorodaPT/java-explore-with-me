package ru.practicum.services.compilations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.services.compilations.model.Compilation;

import java.util.List;


@Repository
public interface RepositoryCompilation extends JpaRepository<Compilation, Long> {

    List<Compilation> findByPinned(Boolean pinned, Pageable pageable);


}
