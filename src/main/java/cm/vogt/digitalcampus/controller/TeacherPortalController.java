package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.dto.student.GradeResponse;
import cm.vogt.digitalcampus.dto.teacher.GradeEntryRequest;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import cm.vogt.digitalcampus.service.TeacherPortalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherPortalController {

    private final TeacherPortalService teacherPortalService;

    @GetMapping("/profile")
    public ApiResponse<?> profile(@AuthenticationPrincipal VogtUserDetails principal) {
        return ApiResponse.ok(teacherPortalService.profile(principal.getUser().getId()));
    }

    @GetMapping("/courses")
    public ApiResponse<?> myCourses(@AuthenticationPrincipal VogtUserDetails principal) {
        return ApiResponse.ok(teacherPortalService.myCourses(principal.getUser().getId()));
    }

    @GetMapping("/students")
    public ApiResponse<?> myStudents(@AuthenticationPrincipal VogtUserDetails principal) {
        return ApiResponse.ok(teacherPortalService.myStudents(principal.getUser().getId()));
    }

    @GetMapping("/course-options")
    public ApiResponse<?> myCourseOptions(@AuthenticationPrincipal VogtUserDetails principal) {
        return ApiResponse.ok(teacherPortalService.myCourseOptions(principal.getUser().getId()));
    }

    @PostMapping("/grades")
    public ApiResponse<GradeResponse> enterGrade(@AuthenticationPrincipal VogtUserDetails principal,
                                                  @Valid @RequestBody GradeEntryRequest request) {
        return ApiResponse.ok("Note enregistree.",
                teacherPortalService.enterGrade(principal.getUser().getId(), request));
    }
}
