package com.stech.authentication.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.stech.authentication.dto.request.UserRequest;
import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.repository.PermissionRepository;
import com.stech.authentication.repository.RoleRepository;
import com.stech.authentication.repository.UserRepository;
import com.stech.authentication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    UserServiceImpl(UserRepository userRepository, 
                   PermissionRepository permissionRepository,
                   RoleRepository roleRepository,
                   PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Set<PermissionEntity> getAllPermissions(Long userId) {
        Set<PermissionEntity> rolePermissions = userRepository.findPermissionsByRoles(userId);
        Set<PermissionEntity> directPermissions = userRepository.findDirectPermissions(userId);

        Set<PermissionEntity> allPermissions = new HashSet<>();
        allPermissions.addAll(rolePermissions);
        allPermissions.addAll(directPermissions);

        return allPermissions;
    }

    public void assignDirectPermission(Long userId, Long permissionId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomResourceNotFoundException("User not found"));
        PermissionEntity permission = permissionRepository.findById(permissionId).orElseThrow(() -> new CustomResourceNotFoundException("Permission not found"));
        user.getDirectPermissions().add(permission);
        userRepository.save(user);
    }

    @Override
    public UserEntity createUser(UserRequest request) {
        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .isActive(request.isActive())
                .userStatus(request.getUserStatus())
                .build();
        
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<RoleEntity> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                    .orElseThrow(() -> new CustomResourceNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        user.setRoles(null);
        return userRepository.save(user);
    }

    @Override
    public Page<UserEntity> getAllUsers(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserEntity> users = userRepository.findAllWithSearch(search, pageable);
        users.forEach(user -> user.getRoles().forEach(role -> {role.setPermissions(null); role.setUsers(null); }));
        return users;
    }

    @Override
    public UserEntity getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomResourceNotFoundException("User not found with id: " + id));
        user.getRoles().forEach(role -> {role.setPermissions(null); role.setUsers(null); });
        return user;
    }

    @Override
    public UserEntity updateUser(Long id, UserRequest request) {
        UserEntity user = getUserById(id);
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setActive(request.isActive());
        user.setUserStatus(request.getUserStatus());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getRoles() != null) {
            Set<RoleEntity> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                    .orElseThrow(() -> new CustomResourceNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userRepository.save(user);
        user.setRoles(null);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity user = getUserById(id);
        userRepository.delete(user);
    }
}
