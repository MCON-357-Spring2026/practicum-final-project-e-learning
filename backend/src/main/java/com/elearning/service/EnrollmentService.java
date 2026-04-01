package com.elearning.service;

import java.util.List;
import java.util.Optional;
import com.elearning.model.Course;
import com.elearning.model.Enrollment;
import com.elearning.model.Grade;
import com.elearning.model.Person;
import com.elearning.model.Quiz;
import com.elearning.model.User;
import com.elearning.errors.CourseNotFoundException;
import com.elearning.errors.EnrollmentNotFoundException;
import com.elearning.errors.QuizNotFoundException;
import com.elearning.errors.StudentNotFoundException;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.PersonRepository;
import com.elearning.repository.QuizRepository;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService implements ServiceInterface<Enrollment> {
    private final EnrollmentRepository repo;
    private final CourseRepository courseRepo;
    private final PersonRepository personRepo;
    private final QuizRepository quizRepo;

    public EnrollmentService(EnrollmentRepository repo, CourseRepository courseRepo, PersonRepository personRepo, QuizRepository quizRepo) {
        this.repo = repo;
        this.courseRepo = courseRepo;
        this.personRepo = personRepo;
        this.quizRepo = quizRepo;
    }

    public List<Enrollment> getByStudentId(String studentId) {
        return repo.findByStudentId(studentId);
    }

    public List<Enrollment> getByCourseId(String courseId) {
        return repo.findByCourseId(courseId);
    }

    /**
     * Creates an enrollment after verifying both the course and student exist.
     * Also adds the enrollment to the User's enrollments ArrayList.
     */
    public Enrollment create(Enrollment enrollment) {
        // Verify the course exists
        courseRepo.findById(enrollment.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + enrollment.getCourseId()));

        // Verify the student exists and is a User
        Person person = personRepo.findById(enrollment.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + enrollment.getStudentId()));

        if (!(person instanceof User)) {
            throw new StudentNotFoundException("Person with id " + enrollment.getStudentId() + " is not a student");
        }

        // Save the enrollment to the enrollments collection
        Enrollment savedEnrollment = repo.save(enrollment);

        // Sync: add the enrollment ID to the User's enrollmentIds list
        User student = (User) person;
        if (student.getEnrollmentIds() == null) {
            student.setEnrollmentIds(new java.util.ArrayList<>());
        }
        student.addEnrollmentId(savedEnrollment.getId());
        personRepo.save(student);

        return savedEnrollment;
    }

    public Optional<Enrollment> getById(String id) {
        return repo.findById(id);
    }

    public Optional<Enrollment> update(String id, Enrollment enrollment) {
        Optional<Enrollment> existingEnrollment = repo.findById(id);
        if (existingEnrollment.isPresent()) {
            Enrollment updatedEnrollment = existingEnrollment.get();
            if (enrollment.getStudentId() != null) {
                updatedEnrollment.setStudentId(enrollment.getStudentId());
            }
            if (enrollment.getCourseId() != null) {
                updatedEnrollment.setCourseId(enrollment.getCourseId());
            }
            if (enrollment.getProgress() != null && !enrollment.getProgress().isEmpty()) {
                updatedEnrollment.setProgress(enrollment.getProgress());
            }
            return Optional.of(repo.save(updatedEnrollment));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Deletes an enrollment by ID and removes it from the User's enrollments ArrayList.
     */
    public boolean delete(String id) {
        Optional<Enrollment> enrollmentOpt = repo.findById(id);
        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();

            // Sync: remove the enrollment from the User's enrollments ArrayList
            removeEnrollmentFromUser(enrollment.getStudentId(), enrollment);

            repo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Enrollment> getByStudentIdAndCourseId(String studentId, String courseId) {
        return Optional.ofNullable(repo.findByStudentIdAndCourseId(studentId, courseId));
    }

    /**
     * Deletes an enrollment by student and course ID, and removes it from the User's enrollments ArrayList.
     */
    public boolean deleteByStudentIdAndCourseId(String studentId, String courseId) {
        Enrollment enrollment = repo.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollment != null) {
            // Sync: remove the enrollment from the User's enrollments ArrayList
            removeEnrollmentFromUser(studentId, enrollment);

            repo.delete(enrollment);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Enrollment> replace(String id, Enrollment enrollment) {
        if (repo.existsById(id)) {
            enrollment.setId(id);
            return Optional.of(repo.save(enrollment));
        } else {
            return Optional.empty();
        }
    }

    public List<Enrollment> getAll() {
        return repo.findAll();
    }

    /**
     * Helper: removes an enrollment ID from the User's enrollmentIds list and saves the user.
     */
    public Grade submitQuiz(String enrollmentId, String quizId, java.util.ArrayList<Integer> answers) {
        Enrollment enrollment = repo.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found with id: " + enrollmentId));

        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + quizId));

        Course course = courseRepo.findById(enrollment.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + enrollment.getCourseId()));

        double score = quiz.calculateScore(answers);
        Grade grade = new Grade(quizId, answers, score);

        enrollment.markQuizAsCompleted(quizId, course, grade);
        repo.save(enrollment);

        return grade;
    }

    private void removeEnrollmentFromUser(String studentId, Enrollment enrollment) {
        personRepo.findById(studentId).ifPresent(person -> {
            if (person instanceof User) {
                User student = (User) person;
                if (student.getEnrollmentIds() != null) {
                    student.removeEnrollmentId(enrollment.getId());
                    personRepo.save(student);
                }
            }
        });
    }
}
