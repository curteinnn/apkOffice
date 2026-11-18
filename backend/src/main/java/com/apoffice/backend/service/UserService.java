package com.apoffice.backend.service;

import com.apoffice.backend.dto.ChangePasswordRequest;
import com.apoffice.backend.dto.CreateUserRequest;
import com.apoffice.backend.dto.UpdateUserRequest;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.entity.Jabatan;
import com.apoffice.backend.repository.JabatanRepository;
import com.apoffice.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JabatanRepository jabatanRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository userRepository,
        JabatanRepository jabatanRepository,
        PasswordEncoder passwordEncoder
            
        
    ) {
        this.userRepository = userRepository;
        this.jabatanRepository = jabatanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User createUser(CreateUserRequest request) {
        if (request == null) {
            throw badRequest("Data pengguna wajib diisi");
        }
        requireText(request.getUsername(), "Username");
        requireText(request.getPassword(), "Password");
        requireText(request.getNamaLengkap(), "Nama lengkap");
        requireText(request.getEmail(), "Email");
        User.Role role = parseRole(request.getRole());
        requireEmployeeManagementRole(role);
        Jabatan jabatan = resolveJabatan(request.getJabatanId(), role == User.Role.EMPLOYEE, null);

        User user = new User();

        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNamaLengkap(request.getNamaLengkap().trim());
        user.setEmail(request.getEmail().trim());
        user.setJabatanRef(jabatan);
        user.setRole(role);

        user.setStatus(User.Status.ACTIVE);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);

    }

    public User updateUser(Long id, UpdateUserRequest request) {
        if (request == null) {
            throw badRequest("Data pengguna wajib diisi");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
        if (user.getRole() != User.Role.EMPLOYEE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Akun ADMIN tidak dapat dikelola melalui modul karyawan");
        }
        requireText(request.getUsername(), "Username");
        requireText(request.getNamaLengkap(), "Nama lengkap");
        requireText(request.getEmail(), "Email");
        User.Role role = parseRole(request.getRole());
        requireEmployeeManagementRole(role);
        User.Status status = parseStatus(request.getStatus());

        Long existingJabatanId = user.getJabatanId();
        Jabatan jabatan = resolveJabatan(request.getJabatanId(), role == User.Role.EMPLOYEE,
                existingJabatanId);

        user.setUsername(request.getUsername().trim());
        user.setNamaLengkap(request.getNamaLengkap().trim());
        user.setEmail(request.getEmail().trim());
        user.setJabatanRef(jabatan);
        user.setRole(role);
        user.setStatus(status);

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    
        public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
        if (user.getRole() != User.Role.EMPLOYEE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Akun ADMIN tidak dapat dihapus melalui modul karyawan");
        }

        userRepository.delete(user);
        }
        
    public void changePassword(String username, ChangePasswordRequest request) {
        if (request == null) {
            throw badRequest("Data password wajib diisi");
        }
        requireText(request.getPasswordLama(), "Password lama");
        requireText(request.getPasswordBaru(), "Password baru");

        User user = userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "User tidak ditemukan"
                            )
                    );

        if (!passwordEncoder.matches(
                request.getPasswordLama(),
                user.getPassword()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password lama salah"
            );
        }

        if (request.getPasswordBaru().length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password baru minimal 6 karakter"
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.getPasswordBaru())
        );
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    private User.Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            throw badRequest("Role wajib diisi");
        }
        try {
            return User.Role.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw badRequest("Role tidak valid");
        }
    }

    private void requireEmployeeManagementRole(User.Role role) {
        if (role != User.Role.EMPLOYEE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Modul pengelolaan karyawan hanya dapat membuat atau mempertahankan role EMPLOYEE");
        }
    }

    private User.Status parseStatus(String value) {
        if (value == null || value.isBlank()) {
            throw badRequest("Status wajib diisi");
        }
        try {
            return User.Status.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw badRequest("Status tidak valid");
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw badRequest(fieldName + " wajib diisi");
        }
    }

    private Jabatan resolveJabatan(Long jabatanId, boolean required, Long existingJabatanId) {
        if (jabatanId == null) {
            if (required) {
                throw badRequest("Jabatan aktif wajib dipilih untuk karyawan");
            }
            return null;
        }

        Jabatan jabatan = jabatanRepository.findById(jabatanId)
                .orElseThrow(() -> badRequest("Jabatan tidak ditemukan"));
        if (jabatan.getStatus() != Jabatan.Status.ACTIVE
                && !jabatanId.equals(existingJabatanId)) {
            throw badRequest("Jabatan yang dipilih tidak aktif");
        }
        return jabatan;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
