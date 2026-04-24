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

/**
 * Service for managing {@link Enrollment} entities.
 * Handles CRUD operations, quiz submissions, and maintains bidirectional
 * references between enrollments and students.
 */
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

    /**
     * Finds all enrollments for a given student.
     *
     * @param studentId the student's ID
     * @return list of enrollments for the student
     */
    public List<Enrollment> getByStudentId(String studentId) {
        return repo.findByStudentId(studentId);
    }

    /**
     * Finds all enrollments for a given course.
     *
     * @param courseId the course ID
     * @return list of enrollments for the course
     */
    public List<Enrollment> getByCourseId(String courseId) {
        return repo.findByCourseId(courseId);
    }

    /**
     * Creates an enrollment after verifying both the course and student exist.
     * Also adds the enrollment to the User's enrollments ArrayList.
     *
     * @param enrollment the enrollment to create
     * @return the saved enrollment
     * @throws CourseNotFoundException   if the course is not found
     * @throws StudentNotFoundException if the student is not found or is not a User
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

    /** {@inheritDoc} */
    public Optional<Enrollment> getById(String id) {
        return repo.findById(id);
    }

    /** {@inheritDoc} */
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
     *
     * @param id the enrollment ID
     * @return true if the enrollment was deleted, false if not found
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

    /**
     * Finds an enrollment by student ID and course ID.
     *
     * @param studentId the student's ID
     * @param courseId  the course ID
     * @return an Optional containing the enrollment if found
     */
    public Optional<Enrollment> getByStudentIdAndCourseId(String studentId, String courseId) {
        return Optional.ofNullable(repo.findByStudentIdAndCourseId(studentId, courseId));
    }

    /**
     * Deletes an enrollment by student and course ID, and removes it from the User's enrollments ArrayList.
     *
     * @param studentId the student's ID
     * @param courseId  the course ID
     * @return true if the enrollment was deleted, false if not found
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

    /** {@inheritDoc} */
    public Optional<Enrollment> replace(String id, Enrollment enrollment) {
        if (repo.existsById(id)) {
            enrollment.setId(id);
            return Optional.of(repo.save(enrollment));
        } else {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} */
    public List<Enrollment> getAll() {
        return repo.findAll();
    }

    /**
     * Submits a quiz for an enrollment. Grades the responses, saves the grade
     * to the enrollment's completed quizzes map, and returns the grade.
     *
     * @param enrollmentId the enrollment ID
     * @param quizId       the quiz ID
     * @param answers      the student's selected answer indices
     * @return the calculated Grade
     * @throws EnrollmentNotFoundException if the enrollment is not found
     * @throws QuizNotFoundException       if the quiz is not found
     * @throws CourseNotFoundException     if the enrollment's course is not found
     * @throws IllegalArgumentException    if the number of answers doesn't match the number of questions
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

    /**
     * Removes an enrollment ID from the User's enrollmentIds list and saves the user.
     *
     * @param studentId  the student's ID
     * @param enrollment the enrollment to remove
     */
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
