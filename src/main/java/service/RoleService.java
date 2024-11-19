package service;

import modelo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.RolesRepository;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RolesRepository rolesRepository;

    public List<Role> getAllRoles(){
        return rolesRepository.findAll();
    }

    public Role getRoleById(Long id){
        return rolesRepository.findById(id).orElseThrow(()-> new RuntimeException("Role not found"));
    }

    public Role createRole(Role role){
        return rolesRepository.save(role);
    }

    public Role updateRole(Long id, Role role){
        Role existingRole = getRoleById(id);
        existingRole.setDescription(role.getDescription());
        return rolesRepository.save(existingRole);
    }

    public void deleteRole(Long id){
        rolesRepository.deleteById(id);
    }
}
