package repository;

import modelo.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface RolesRepository extends JpaRepository<Role, Long> {
}
