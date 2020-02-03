package magasumovn.deanery.controllers;

import magasumovn.deanery.model.Performance;
import magasumovn.deanery.repo.PerformanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("performance")
public class PerformanceController  {
    private final PerformanceRepo performanceRepo;

    @Autowired
    public PerformanceController(PerformanceRepo performanceRepo) {
        this.performanceRepo = performanceRepo;
    }

    @GetMapping
    public List<Performance> list(@RequestParam(required = false, defaultValue = "") String studentID)  {
        if (!studentID.isEmpty()) {
            return performanceRepo.getPerformancesByStudent_StudentID(Long.parseLong(studentID));
        }
        else {
             return performanceRepo.findAll();
        }
    }

    @PostMapping
    public Performance create(@RequestBody Performance performance) {
        Performance performanceFromDb = performanceRepo.getPerformanceByStudent_StudentIDAndSubject_SubjectIDAndSemesterNumber(
                performance.getStudent().getStudentID(), performance.getSubject().getSubjectID(), performance.getSemesterNumber());
        if (performanceFromDb != null) {
            return null;
        }
        return performanceRepo.save(performance);
    }

    @PutMapping
    public Performance update(
            @RequestParam String studentID,
            @RequestParam String subjectID,
            @RequestParam String semesterNumber,
            @RequestBody Performance performance) {
        Performance performanceFromDb = performanceRepo.getPerformanceByStudent_StudentIDAndSubject_SubjectIDAndSemesterNumber(
                Long.parseLong(studentID), Long.parseLong(subjectID), Integer.parseInt(semesterNumber));
        if (performanceFromDb != null) {
            performanceFromDb.setMark(performance.getMark());
            return performanceRepo.save(performanceFromDb);
        }
        return null;
    }

    @DeleteMapping
    public void delete(
            @RequestParam String studentID,
            @RequestParam String subjectID,
            @RequestParam String semesterNumber
    ) {
        Performance performanceFromDb = performanceRepo.getPerformanceByStudent_StudentIDAndSubject_SubjectIDAndSemesterNumber(
                Long.parseLong(studentID), Long.parseLong(subjectID), Integer.parseInt(semesterNumber));
        performanceRepo.delete(performanceFromDb);
    }
}
