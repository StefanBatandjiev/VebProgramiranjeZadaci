package mk.ukim.finki.wp.kol2022.g2.web;

import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import mk.ukim.finki.wp.kol2022.g2.service.CourseService;

import java.time.LocalDate;
import java.util.List;

public class StudentController {

    private final StudentService service;
    private final CourseService courseService;

    public StudentController(StudentService service, CourseService courseService) {
        this.service = service;
        this.courseService = courseService;
    }

    /**
     * This method should use the "list.html" template to display all entities.
     * The method should be mapped on paths '/' and '/students'.
     * The arguments that this method takes are optional and can be 'null'.
     *
     * @return The view "list.html".
     */
    public String showList(Long courseId, Integer yearsOfStudying) {
        List<Student> students;
        if (courseId == null && yearsOfStudying == null) {
            students = this.service.listAll();
        } else {
            students = this.service.filter(courseId, yearsOfStudying);
        }
        return "";
    }

    /**
     * This method should display the "form.html" template.
     * The method should be mapped on path '/students/add'.
     *
     * @return The view "form.html".
     */
    public String showAdd() {
        return "";
    }

    /**
     * This method should display the "form.html" template.
     * However, in this case all 'input' elements should be filled with the appropriate value for the entity that is updated.
     * The method should be mapped on path '/students/[id]/edit'.
     *
     * @return The view "form.html".
     */
    public String showEdit(Long id) {
        return "";
    }

    /**
     * This method should create an entity given the arguments it takes.
     * The method should be mapped on path '/students'.
     * After the entity is created, the list of entities should be displayed.
     *
     * @return The view "list.html".
     */
    public String create(String name,
                         String email,
                         String password,
                         StudentType type,
                         List<Long> coursesId,
                         LocalDate enrollmentDate) {
        this.service.create(name, email, password, type, coursesId, enrollmentDate);
        return "";
    }

    /**
     * This method should update an entity given the arguments it takes.
     * The method should be mapped on path '/students/[id]'.
     * After the entity is updated, the list of entities should be displayed.
     *
     * @return The view "list.html".
     */
    public String update(Long id,
                         String name,
                         String email,
                         String password,
                         StudentType type,
                         List<Long> coursesId,
                         LocalDate enrollmentDate) {
        this.service.update(id, name, email, password, type, coursesId, enrollmentDate);
        return "";
    }

    /**
     * This method should delete the entities that has the appropriate identifier.
     * The method should be mapped on path '/students/[id]/delete'.
     * After the entity is deleted, the list of entities should be displayed.
     *
     * @return The view "list.html".
     */
    public String delete(Long id) {
        this.service.delete(id);
        return "";
    }
}
