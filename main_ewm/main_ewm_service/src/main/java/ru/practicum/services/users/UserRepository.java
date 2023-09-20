package ru.practicum.services.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.services.users.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select u from User as u " +
            "WHERE (coalesce(:ids, null) is null or (u.id in :ids))")
    Page<User> findById(List<Long> ids, Pageable pageable);
}
