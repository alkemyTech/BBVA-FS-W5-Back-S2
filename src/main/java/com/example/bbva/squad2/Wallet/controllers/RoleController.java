package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @Operation(summary = "Obtener todos los roles")
    public List<Role> getAllRoles(){
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener los roles por Id")
    public Role getRoleById(@PathVariable Long id){
        return roleService.getRoleById(id);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo rol")
    public Role createRole(@RequestBody Role role){
        return roleService.createRole(role);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol por Id")
    public Role updateRole(@PathVariable Long id, @RequestBody Role role){
        return roleService.updateRole(id,role);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol por Id")
    public void deleteRole(@PathVariable Long id){
        roleService.deleteRole(id);
    }

}
