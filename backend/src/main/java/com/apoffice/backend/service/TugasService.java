package com.apoffice.backend.service;

import com.apoffice.backend.dto.CreateTugasRequest;
import com.apoffice.backend.dto.CreateBulkTugasRequest;
import com.apoffice.backend.dto.VerifyTugasRequest;
import com.apoffice.backend.entity.Tugas;
import com.apoffice.backend.entity.JenisTugas;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.entity.PenugasanKaryawan;
import com.apoffice.backend.dto.UpdateTugasRequest;
import com.apoffice.backend.repository.TugasRepository;
import com.apoffice.backend.repository.JenisTugasRepository;
import com.apoffice.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TugasService {

    private final TugasRepository tugasRepository;
    private final UserRepository userRepository;
    private final JenisTugasRepository jenisTugasRepository;
    private final TugasIdGenerator tugasIdGenerator;

    public TugasService(
            TugasRepository tugasRepository,
            UserRepository userRepository,
            JenisTugasRepository jenisTugasRepository,
            TugasIdGenerator tugasIdGenerator
    ) {
        this.tugasRepository = tugasRepository;
        this.userRepository = userRepository;
        this.jenisTugasRepository = jenisTugasRepository;
        this.tugasIdGenerator = tugasIdGenerator;
    }

    public List<Tugas> getAllTugas(String username, String role) {
        if ("ADMIN".equals(role)) {
            return tugasRepository.findAll();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User tidak ditemukan"
                ));

        return tugasRepository.findAssignedTo(user);
    }

    @Transactional
    public Tugas createTugas(CreateTugasRequest request) {
        if (request == null || request.getUserId() == null || request.getTanggal() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Karyawan, nama pelanggan, jenis tugas, judul, lokasi, dan tanggal wajib diisi"
            );
        }
        String namaPelanggan = requiredText(request.getNamaPelanggan(), "Nama pelanggan", 150);
        String judul = requiredText(request.getJudul(), "Judul tugas", 150);
        String lokasi = requiredText(request.getLokasi(), "Lokasi", 255);
        String jenisTugasId = requiredText(request.getJenisTugasId(), "Jenis tugas", 20);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Karyawan tidak ditemukan"
                ));

        JenisTugas jenisTugas = jenisTugasRepository.findById(jenisTugasId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Jenis tugas tidak valid"
                ));

        if (user.getRole() != User.Role.EMPLOYEE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tugas hanya bisa diberikan kepada employee"
            );
        }

        Tugas tugas = new Tugas();
        tugas.setId(tugasIdGenerator.nextId(request.getTanggal()));
        tugas.setAssignees(new LinkedHashSet<>(List.of(user)));
        tugas.setJenisTugas(jenisTugas);
        tugas.setNamaPelanggan(namaPelanggan);
        tugas.setJudul(judul);
        tugas.setLokasi(lokasi);
        tugas.setLinkGmaps(optionalText(request.getLinkGmaps()));
        tugas.setDeskripsi(optionalText(request.getDeskripsi()));
        tugas.setTanggal(request.getTanggal());
        tugas.setStatus("PENDING");

        LocalDateTime now = LocalDateTime.now();
        tugas.setCreatedAt(now);
        tugas.setUpdatedAt(now);

        return tugasRepository.save(tugas);
    }

    @Transactional
    public Tugas createTugasForEmployees(CreateBulkTugasRequest request) {
        if (request == null || request.getUserIds() == null || request.getUserIds().isEmpty()
                || request.getUserIds().stream().anyMatch(id -> id == null)
                || new HashSet<>(request.getUserIds()).size() != request.getUserIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pilih minimal satu karyawan dan hindari pilihan karyawan ganda");
        }

        List<User> users = userRepository.findAllById(request.getUserIds());
        Map<Long, User> usersById = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        if (usersById.size() != request.getUserIds().size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Satu atau beberapa karyawan tidak ditemukan");
        }
        for (User user : users) {
            if (user.getRole() != User.Role.EMPLOYEE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tugas hanya bisa diberikan kepada employee");
            }
        }

        CreateTugasRequest single = new CreateTugasRequest();
        single.setUserId(request.getUserIds().get(0));
        single.setJenisTugasId(request.getJenisTugasId());
        single.setNamaPelanggan(request.getNamaPelanggan());
        single.setJudul(request.getJudul());
        single.setLokasi(request.getLokasi());
        single.setDeskripsi(request.getDeskripsi());
        single.setTanggal(request.getTanggal());
        single.setLinkGmaps(request.getLinkGmaps());

        Tugas tugas = createTugas(single);
        LinkedHashSet<User> assignees = request.getUserIds().stream()
                .map(usersById::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        tugas.setAssignees(assignees);
        return tugasRepository.save(tugas);
    }

    @Transactional
    public Tugas updateTugas(String id, UpdateTugasRequest request) {
        Tugas tugas = tugasRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tugas tidak ditemukan"));
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data tugas wajib diisi");
        }
        if (!"PENDING".equals(tugas.getStatus()) && !"REJECTED".equals(tugas.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Data hanya dapat diubah saat tugas berstatus PENDING atau REJECTED");
        }
        List<Long> userIds = request.getUserIds();
        if (userIds == null || userIds.isEmpty() || userIds.stream().anyMatch(java.util.Objects::isNull)
                || new HashSet<>(userIds).size() != userIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pilih minimal satu karyawan dan hindari pilihan karyawan ganda");
        }
        if (request.getTanggal() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tanggal tugas wajib diisi");
        }

        String namaPelanggan = requiredText(request.getNamaPelanggan(), "Nama pelanggan", 150);
        String judul = requiredText(request.getJudul(), "Judul tugas", 150);
        String lokasi = requiredText(request.getLokasi(), "Lokasi", 255);
        String jenisTugasId = requiredText(request.getJenisTugasId(), "Jenis tugas", 20);
        JenisTugas jenisTugas = jenisTugasRepository.findById(jenisTugasId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jenis tugas tidak valid"));

        List<User> employees = userRepository.findAllById(userIds);
        Map<Long, User> employeesById = employees.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        if (employeesById.size() != userIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Satu atau beberapa karyawan tidak ditemukan");
        }
        Set<Long> currentlyAssignedIds = tugas.getAssignees().stream()
                .map(User::getId).collect(Collectors.toSet());
        if (employees.stream().anyMatch(user -> user.getRole() != User.Role.EMPLOYEE
                || (user.getStatus() != User.Status.ACTIVE && !currentlyAssignedIds.contains(user.getId())))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tugas hanya bisa diberikan kepada employee aktif");
        }

        Set<Long> requestedIds = new HashSet<>(userIds);
        tugas.getPenugasanKaryawan().removeIf(assignment ->
                !requestedIds.contains(assignment.getKaryawan().getId()));
        Set<Long> existingIds = tugas.getPenugasanKaryawan().stream()
                .map(assignment -> assignment.getKaryawan().getId()).collect(Collectors.toSet());
        for (Long userId : userIds) {
            if (!existingIds.contains(userId)) {
                tugas.getPenugasanKaryawan().add(new PenugasanKaryawan(tugas, employeesById.get(userId)));
            }
        }

        tugas.setJenisTugas(jenisTugas);
        tugas.setNamaPelanggan(namaPelanggan);
        tugas.setJudul(judul);
        tugas.setLokasi(lokasi);
        tugas.setLinkGmaps(optionalText(request.getLinkGmaps()));
        tugas.setDeskripsi(optionalText(request.getDeskripsi()));
        tugas.setTanggal(request.getTanggal());
        tugas.setUpdatedAt(LocalDateTime.now());
        return tugasRepository.save(tugas);
    }

    @Transactional
    public void deleteTugas(String id) {
        Tugas tugas = tugasRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tugas tidak ditemukan"));
        if (!"PENDING".equals(tugas.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Hanya tugas PENDING yang dapat dihapus agar riwayat proses tetap tersimpan");
        }
        tugasRepository.delete(tugas);
    }

    private String requiredText(String value, String label, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " wajib diisi");
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() > maxLength) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    label + " maksimal " + maxLength + " karakter");
        }
        return normalized;
    }

    private String optionalText(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

    @Transactional
    public Tugas startTugas(String id, String username) {
        Tugas tugas = tugasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tugas tidak ditemukan"
                ));

        if (tugas.getAssignees().stream().noneMatch(user -> user.getUsername().equals(username))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Kamu tidak memiliki akses ke tugas ini"
            );
        }

        if (!"PENDING".equals(tugas.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tugas tidak dapat dimulai dengan status saat ini"
            );
        }

        tugas.setStatus("IN_PROGRESS");
        tugas.setUpdatedAt(LocalDateTime.now());

        
        return tugasRepository.save(tugas);
    }

    @Transactional
    public Tugas submitTugas(String id, String username) {
        Tugas tugas = tugasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tugas tidak ditemukan"
                ));

        if (tugas.getAssignees().stream().noneMatch(user -> user.getUsername().equals(username))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Kamu tidak memiliki akses ke tugas ini"
            );
        }

        if (!"IN_PROGRESS".equals(tugas.getStatus())
                && !"REJECTED".equals(tugas.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tugas belum dimulai atau sudah dikirim"
            );
        }

        tugas.setStatus("SUBMITTED");
        tugas.setUpdatedAt(LocalDateTime.now());

        return tugasRepository.save(tugas);
    }

    @Transactional
    public Tugas verifyTugas(String id, VerifyTugasRequest request) {
        Tugas tugas = tugasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tugas tidak ditemukan"
                ));

        String status = request.getStatus();

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status harus APPROVED atau REJECTED"
            );
        }

        if (!"SUBMITTED".equals(tugas.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Admin hanya bisa memeriksa tugas yang sudah dikirim"
            );
        }

        if ("APPROVED".equals(status)) {
            Map<Long, java.math.BigDecimal> nominals = request.getNominalByAssignee();
            java.util.Set<Long> assignedIds = tugas.getAssignees().stream()
                    .map(User::getId).collect(Collectors.toSet());
            if (nominals == null || !nominals.keySet().equals(assignedIds)
                    || nominals.values().stream().anyMatch(value -> value == null || value.signum() < 0)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Nominal wajib diisi untuk setiap karyawan dan tidak boleh negatif"
                );
            }
            tugas.getPenugasanKaryawan().forEach(assignment ->
                    assignment.setNominal(nominals.get(assignment.getKaryawan().getId())));
        } else {
            tugas.getPenugasanKaryawan().forEach(assignment -> assignment.setNominal(null));
        }

        tugas.setStatus(status);
        tugas.setUpdatedAt(LocalDateTime.now());

        return tugasRepository.save(tugas);
    }
}
