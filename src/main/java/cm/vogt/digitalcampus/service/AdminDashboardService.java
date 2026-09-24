package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/** Alimente l'ecran "Overview" du VOGT ADMIN — chiffres reels, jamais figes. */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;
    private final TeacherRepository teacherRepository;
    private final ProgramRepository programRepository;
    private final EventRepository eventRepository;

    public Map<String, Long> overview() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("students", studentRepository.count());
        stats.put("applications", applicationRepository.count());
        stats.put("teachers", teacherRepository.count());
        stats.put("programs", programRepository.count());
        stats.put("events", eventRepository.count());
        return stats;
    }
}
