package mk.ukim.finki.wp.kol2022.g2.service.impl;

import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidStudentIdException;
import mk.ukim.finki.wp.kol2022.g2.repository.CourseRepository;
import mk.ukim.finki.wp.kol2022.g2.repository.StudentRepository;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StudentServiceImpl implements StudentService, UserDetailsService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Student> listAll() {
        return this.studentRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return this.studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
    }

    @Override
    public Student create(String name, String email, String password, StudentType type, List<Long> courseId, LocalDate enrollmentDate) {
        List<Course> courses = this.courseRepository.findAllById(courseId);
        String encodedPassword = this.passwordEncoder.encode(password);
        Student student = new Student(name,email,encodedPassword,type,courses,enrollmentDate);
        return this.studentRepository.save(student);
    }

    @Override
    public Student update(Long id, String name, String email, String password, StudentType type, List<Long> coursesId, LocalDate enrollmentDate) {
        Student student = this.findById(id);
        student.setName(name);
        student.setEmail(email);
        student.setPassword(password);
        student.setType(type);
        student.setEnrollmentDate(enrollmentDate);

        List<Course> courses = this.courseRepository.findAllById(coursesId);
        student.setCourses(courses);
        return this.studentRepository.save(student);
    }

    @Override
    public Student delete(Long id) {
        Student student = this.findById(id);
        this.studentRepository.delete(student);
        return student;
    }

    @Override
    public List<Student> filter(Long courseId, Integer yearsOfStudying) {
        Course course = courseId!=null ? this.courseRepository.findById(courseId).orElse((Course) null) : null;
        LocalDate date = yearsOfStudying!=null ? LocalDate.now().minusYears(yearsOfStudying) : null;
        if(course != null && date != null){
            return this.studentRepository.findAllByCoursesContainsAndEnrollmentDateBefore(course,date);
        }else if(course != null){
            return this.studentRepository.findAllByCoursesContains(course);
        }else if(date != null){
            return this.studentRepository.findAllByEnrollmentDateBefore(date);
        }else{
            return studentRepository.findAll();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = this.studentRepository.findByEmail(email);
        UserDetails userDetails = new User(
                student.getEmail(),
                student.getPassword(),
                Stream.of(new SimpleGrantedAuthority("ROLE_"+student.getType())).collect(Collectors.toList()));
        return userDetails;
    }
}
