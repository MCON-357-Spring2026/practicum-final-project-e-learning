package com.elearning.config;

import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.model.*;
import com.elearning.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;

/**
 * Application startup component that seeds the MongoDB database with sample
 * users, courses, lessons, quizzes, and enrollments when the database is empty.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final CourseRepository courseRepo;
    private final LessonRepository lessonRepo;
    private final QuizRepository quizRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PersonRepository personRepo;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(CourseRepository courseRepo, LessonRepository lessonRepo,
                          QuizRepository quizRepo, EnrollmentRepository enrollmentRepo,
                          PersonRepository personRepo, PasswordEncoder passwordEncoder) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.quizRepo = quizRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.personRepo = personRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Seeds the database with sample data if no courses exist.
     *
     * @param args command-line arguments (unused)
     */
    @Override
    public void run(String... args) {
        // Only seed if the database is empty
        if (courseRepo.count() > 0) {
            System.out.println("Database already seeded. Skipping...");
            return;
        }

        System.out.println("Seeding database...");

        // ========== USERS (students and instructors) - created FIRST for referential integrity ==========
        User student1 = new User("Alice", "Johnson", new GregorianCalendar(2000, 5, 15).getTime(),
                Gender.FEMALE, new HomeAddress("123 Main St", "Springfield", "IL", "62704"),
                "student1", passwordEncoder.encode("password123"), null, Role.STUDENT);
        User student2 = new User("Bob", "Smith", new GregorianCalendar(1999, 8, 22).getTime(),
                Gender.MALE, new HomeAddress("456 Oak Ave", "Chicago", "IL", "60601"),
                "student2", passwordEncoder.encode("password456"), null, Role.STUDENT);

        Teacher instructor1 = new Teacher("Dr. Jane", "Doe", new GregorianCalendar(1980, 3, 10).getTime(),
                Gender.FEMALE, new HomeAddress("789 Elm St", "Urbana", "IL", "61801"),
                "instructor1", passwordEncoder.encode("instrpass1"), null, Role.TEACHER, "Computer Science");
        Teacher instructor2 = new Teacher("Prof. John", "Williams", new GregorianCalendar(1975, 11, 5).getTime(),
                Gender.MALE, new HomeAddress("321 Pine Rd", "Champaign", "IL", "61820"),
                "instructor2", passwordEncoder.encode("instrpass2"), null, Role.TEACHER, "Computer Science");
        Teacher instructor3 = new Teacher("Dr. Sarah", "Lee", new GregorianCalendar(1982, 7, 18).getTime(),
                Gender.FEMALE, new HomeAddress("654 Maple Dr", "Evanston", "IL", "60201"),
                "instructor3", passwordEncoder.encode("instrpass3"), null, Role.TEACHER, "Mathematics");

        student1 = personRepo.save(student1);
        student2 = personRepo.save(student2);
        instructor1 = personRepo.save(instructor1);
        instructor2 = personRepo.save(instructor2);
        instructor3 = personRepo.save(instructor3);

        // ========== COURSES (using real instructor IDs) ==========
        Course javaCourse = new Course("Introduction to Java", instructor1.getId(), "Computer Science", 3, 101, "Learn the fundamentals of Java programming including OOP, data structures, and algorithms.");
        javaCourse.setImage("https://upload.wikimedia.org/wikipedia/en/3/30/Java_programming_language_logo.svg");

        Course webDevCourse = new Course("Web Development", instructor2.getId(), "Computer Science", 4, 201, "Full-stack web development with HTML, CSS, JavaScript, and modern frameworks.");
        webDevCourse.setImage("https://upload.wikimedia.org/wikipedia/commons/6/61/HTML5_logo_and_wordmark.svg");

        Course dbCourse = new Course("Database Systems", instructor1.getId(), "Computer Science", 3, 301, "Relational and NoSQL database design, SQL, normalization, and query optimization.");
        dbCourse.setImage("https://upload.wikimedia.org/wikipedia/commons/2/29/Postgresql_elephant.svg");

        Course mathCourse = new Course("Calculus I", instructor3.getId(), "Mathematics", 4, 101, "Limits, derivatives, integrals, and the fundamental theorem of calculus.");

        Course physicsCourse = new Course("Physics I", instructor3.getId(), "Physics", 4, 101, "Classical mechanics, kinematics, Newton's laws, energy, and momentum.");

        // Save courses first to get IDs
        javaCourse = courseRepo.save(javaCourse);
        webDevCourse = courseRepo.save(webDevCourse);
        dbCourse = courseRepo.save(dbCourse);
        mathCourse = courseRepo.save(mathCourse);
        physicsCourse = courseRepo.save(physicsCourse);

        // ========== LESSONS (linked to courses) ==========

        // Java lessons
        Lesson javaLesson1 = createLesson("Java Basics", "Variables, data types, and operators in Java.", javaCourse.getId(), 30,
                new ArrayList<>(Arrays.asList("https://docs.oracle.com/javase/tutorial/java/nutsandbolts/")));
        Lesson javaLesson2 = createLesson("Object-Oriented Programming", "Classes, objects, inheritance, and polymorphism.", javaCourse.getId(), 45,
                new ArrayList<>(Arrays.asList("https://docs.oracle.com/javase/tutorial/java/concepts/")));
        Lesson javaLesson3 = createLesson("Collections Framework", "Lists, Sets, Maps, and iteration patterns.", javaCourse.getId(), 40,
                new ArrayList<>(Arrays.asList("https://docs.oracle.com/javase/tutorial/collections/")));

        javaLesson1 = lessonRepo.save(javaLesson1);
        javaLesson2 = lessonRepo.save(javaLesson2);
        javaLesson3 = lessonRepo.save(javaLesson3);

        // Web Dev lessons
        Lesson webLesson1 = createLesson("HTML & CSS Fundamentals", "Structure and style web pages.", webDevCourse.getId(), 35,
                new ArrayList<>(Arrays.asList("https://developer.mozilla.org/en-US/docs/Learn/HTML")));
        Lesson webLesson2 = createLesson("JavaScript Essentials", "DOM manipulation, events, and async programming.", webDevCourse.getId(), 50,
                new ArrayList<>(Arrays.asList("https://developer.mozilla.org/en-US/docs/Learn/JavaScript")));
        Lesson webLesson3 = createLesson("Vue.js Basics", "Component-based UI development with Vue 3.", webDevCourse.getId(), 45,
                new ArrayList<>(Arrays.asList("https://vuejs.org/guide/introduction.html")));
        Lesson webLesson4 = createLesson("REST APIs", "Building and consuming RESTful APIs.", webDevCourse.getId(), 40,
                new ArrayList<>(Arrays.asList("https://restfulapi.net/")));

        webLesson1 = lessonRepo.save(webLesson1);
        webLesson2 = lessonRepo.save(webLesson2);
        webLesson3 = lessonRepo.save(webLesson3);
        webLesson4 = lessonRepo.save(webLesson4);

        // DB lessons
        Lesson dbLesson1 = createLesson("Relational Database Design", "ER diagrams, normalization, and schema design.", dbCourse.getId(), 40,
                new ArrayList<>());
        Lesson dbLesson2 = createLesson("SQL Fundamentals", "SELECT, INSERT, UPDATE, DELETE, and JOINs.", dbCourse.getId(), 45,
                new ArrayList<>());
        Lesson dbLesson3 = createLesson("NoSQL with MongoDB", "Document databases, CRUD operations, and aggregation.", dbCourse.getId(), 35,
                new ArrayList<>(Arrays.asList("https://www.mongodb.com/docs/manual/")));

        dbLesson1 = lessonRepo.save(dbLesson1);
        dbLesson2 = lessonRepo.save(dbLesson2);
        dbLesson3 = lessonRepo.save(dbLesson3);

        // Math lessons
        Lesson mathLesson1 = createLesson("Limits and Continuity", "Understanding limits, one-sided limits, and continuity.", mathCourse.getId(), 50, new ArrayList<>());
        Lesson mathLesson2 = createLesson("Derivatives", "Rules of differentiation and applications.", mathCourse.getId(), 55, new ArrayList<>());

        mathLesson1 = lessonRepo.save(mathLesson1);
        mathLesson2 = lessonRepo.save(mathLesson2);

        // Physics lessons
        Lesson physicsLesson1 = createLesson("Kinematics", "Motion in 1D and 2D, velocity, and acceleration.", physicsCourse.getId(), 45, new ArrayList<>());
        Lesson physicsLesson2 = createLesson("Newton's Laws", "Forces, free body diagrams, and applications.", physicsCourse.getId(), 50, new ArrayList<>());

        physicsLesson1 = lessonRepo.save(physicsLesson1);
        physicsLesson2 = lessonRepo.save(physicsLesson2);

        // ========== UPDATE COURSES WITH LESSON IDs ==========
        javaCourse.setLessonIDs(new ArrayList<>(Arrays.asList(javaLesson1.getId(), javaLesson2.getId(), javaLesson3.getId())));
        webDevCourse.setLessonIDs(new ArrayList<>(Arrays.asList(webLesson1.getId(), webLesson2.getId(), webLesson3.getId(), webLesson4.getId())));
        dbCourse.setLessonIDs(new ArrayList<>(Arrays.asList(dbLesson1.getId(), dbLesson2.getId(), dbLesson3.getId())));
        mathCourse.setLessonIDs(new ArrayList<>(Arrays.asList(mathLesson1.getId(), mathLesson2.getId())));
        physicsCourse.setLessonIDs(new ArrayList<>(Arrays.asList(physicsLesson1.getId(), physicsLesson2.getId())));

        // ========== QUIZZES (linked to courses via courseId) ==========

        // Java quiz
        Quiz javaQuiz = new Quiz(javaCourse.getId(), "Java Basics Quiz");
        javaQuiz.addQuestion("What is the default value of an int in Java?", new String[]{"null", "0", "1", "undefined"}, 1);
        javaQuiz.addQuestion("Which keyword is used to inherit a class?", new String[]{"implements", "extends", "inherits", "super"}, 1);
        javaQuiz.addQuestion("Which collection allows duplicate elements?", new String[]{"Set", "Map", "List", "TreeSet"}, 2);
        javaQuiz = quizRepo.save(javaQuiz);

        // Web Dev quiz
        Quiz webQuiz = new Quiz(webDevCourse.getId(), "Web Development Quiz");
        webQuiz.addQuestion("What does HTML stand for?", new String[]{"Hyper Text Markup Language", "High Tech Modern Language", "Home Tool Markup Language", "Hyperlink Text Mode Language"}, 0);
        webQuiz.addQuestion("Which CSS property changes text color?", new String[]{"font-color", "text-color", "color", "foreground"}, 2);
        webQuiz.addQuestion("What is the Vue.js directive for two-way binding?", new String[]{"v-bind", "v-model", "v-on", "v-for"}, 1);
        webQuiz = quizRepo.save(webQuiz);

        // DB quiz
        Quiz dbQuiz = new Quiz(dbCourse.getId(), "Database Systems Quiz");
        dbQuiz.addQuestion("What does SQL stand for?", new String[]{"Structured Query Language", "Simple Query Language", "Standard Query Logic", "Structured Question Language"}, 0);
        dbQuiz.addQuestion("Which normal form eliminates transitive dependencies?", new String[]{"1NF", "2NF", "3NF", "BCNF"}, 2);
        dbQuiz = quizRepo.save(dbQuiz);

        // Math quiz
        Quiz mathQuiz = new Quiz(mathCourse.getId(), "Calculus I Quiz");
        mathQuiz.addQuestion("What is the derivative of x²?", new String[]{"x", "2x", "2x²", "x/2"}, 1);
        mathQuiz.addQuestion("What is the limit of sin(x)/x as x→0?", new String[]{"0", "1", "∞", "undefined"}, 1);
        mathQuiz = quizRepo.save(mathQuiz);

        // ========== UPDATE COURSES WITH LESSON + QUIZ IDs ==========
        javaCourse.setQuizIDs(new ArrayList<>(Arrays.asList(javaQuiz.getId())));
        webDevCourse.setQuizIDs(new ArrayList<>(Arrays.asList(webQuiz.getId())));
        dbCourse.setQuizIDs(new ArrayList<>(Arrays.asList(dbQuiz.getId())));
        mathCourse.setQuizIDs(new ArrayList<>(Arrays.asList(mathQuiz.getId())));
        physicsCourse.setQuizIDs(new ArrayList<>()); // no quiz yet

        // Save courses with lesson and quiz references
        courseRepo.save(javaCourse);
        courseRepo.save(webDevCourse);
        courseRepo.save(dbCourse);
        courseRepo.save(mathCourse);
        courseRepo.save(physicsCourse);

        // ========== SYNC TEACHER COURSE IDs ==========
        instructor1.setCourseIds(new ArrayList<>(Arrays.asList(javaCourse.getId(), dbCourse.getId())));
        instructor2.setCourseIds(new ArrayList<>(Arrays.asList(webDevCourse.getId())));
        instructor3.setCourseIds(new ArrayList<>(Arrays.asList(mathCourse.getId(), physicsCourse.getId())));
        personRepo.save(instructor1);
        personRepo.save(instructor2);
        personRepo.save(instructor3);

        // ========== ENROLLMENTS (referencing real student and course IDs) ==========
        Enrollment enrollment1 = new Enrollment(student1.getId(), javaCourse.getId());
        Enrollment enrollment2 = new Enrollment(student1.getId(), webDevCourse.getId());
        Enrollment enrollment3 = new Enrollment(student2.getId(), javaCourse.getId());
        Enrollment enrollment4 = new Enrollment(student2.getId(), dbCourse.getId());

        enrollment1 = enrollmentRepo.save(enrollment1);
        enrollment2 = enrollmentRepo.save(enrollment2);
        enrollment3 = enrollmentRepo.save(enrollment3);
        enrollment4 = enrollmentRepo.save(enrollment4);

        // Sync enrollment IDs to User objects
        student1.setEnrollmentIds(new ArrayList<>(Arrays.asList(enrollment1.getId(), enrollment2.getId())));
        student2.setEnrollmentIds(new ArrayList<>(Arrays.asList(enrollment3.getId(), enrollment4.getId())));
        personRepo.save(student1);
        personRepo.save(student2);

        System.out.println("Database seeded successfully!");
        System.out.println("  - 5 courses");
        System.out.println("  - 14 lessons");
        System.out.println("  - 4 quizzes");
        System.out.println("  - 4 enrollments");
        System.out.println("  - 2 students, 3 instructors");
    }

    private Lesson createLesson(String title, String description, String courseId, int minutes, ArrayList<String> resources) {
        Lesson lesson = new Lesson(title, description);
        lesson.setCourseId(courseId);
        lesson.setMinutes(minutes);
        lesson.setResources(resources);
        return lesson;
    }
}
