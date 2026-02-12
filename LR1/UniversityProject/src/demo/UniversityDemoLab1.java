package demo;

import models.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class UniversityDemoLab1 {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  ЛР1: OOP basics && The concept of encapsulation");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        System.out.println("1️. Создание университета:");
        System.out.println("─────────────────────────────────────────────────────────────");
        University university = new University("<Белорусский Государственный Университет Информатики и Радиоэлектроники",
                "ул. Гикало, 9", 1964);
        System.out.println(university);
        System.out.println();

        System.out.println("2️. Создание факультетов:");
        System.out.println("─────────────────────────────────────────────────────────────");
        Faculty facultyCSaN = new Faculty("Факультет компьютерных систем и сетей", "FCSaN");
        Faculty facultyCAD = new Faculty("Факультет компьютерного проектирования", "FCAD");

        university.addFaculty(facultyCSaN);
        university.addFaculty(facultyCAD);

        System.out.println("✓ " + facultyCSaN.getName());
        System.out.println("✓ " + facultyCAD.getName());
        System.out.println();

        System.out.println("3. Создание кафедр:");
        System.out.println("─────────────────────────────────────────────────────────────");
        Department deptCS = new Department("Кафедра информатики", "CS");
        Department deptACE = new Department("Кафедра ЭВМ", "ACE");

        facultyCSaN.addDepartment(deptCS);
        facultyCSaN.addDepartment(deptACE);

        System.out.println("✓ " + deptCS.getName());
        System.out.println("✓ " + deptACE.getName());
        System.out.println();

        System.out.println("Создание преподавателей:");
        System.out.println("─────────────────────────────────────────────────────────────");
        Teacher prof_Sirotko = new Teacher(
                "Сергей", "Сиротко", LocalDate.of(1965, 5, 15),
                "sergeyis@bsuir.by", "+375 (17) 293-80-94",
                "EMP001", "Доцент", LocalDate.of(1990, 9, 1), 150000
        );

        Teacher prof_Glamazdin = new Teacher(
                "Игорь", "Гламаздин", LocalDate.of(1975, 3, 20),
                "glamazdin@bsuir.by", "+375 (17) 293-86-66",
                "EMP002", "Доцент", LocalDate.of(2005, 9, 1), 100000
        );

        Teacher prof_Glecevich = new Teacher(
                "Иван", "Глецевич", LocalDate.of(1980, 7, 10),
                "glecevich@bsuir.by", "+375 (17) 293-85-87",
                "EMP003", "Ассистент", LocalDate.of(2000, 9, 1), 60000
        );

        deptCS.addTeacher(prof_Sirotko);
        deptCS.addTeacher(prof_Glamazdin);
        deptACE.addTeacher(prof_Glecevich);

        deptCS.setHead(prof_Sirotko);
        facultyCSaN.setDean(prof_Glecevich);

        System.out.println("✓ " + prof_Sirotko);
        System.out.println("✓ " + prof_Glamazdin);
        System.out.println("✓ " + prof_Glecevich);
        System.out.println();

        System.out.println("5. Создание аудиторий:");
        System.out.println("─────────────────────────────────────────────────────────────");
        Classroom room_104 = new Classroom("104", "Лекционный зал", 30);
        room_104.setHasProjector(true);
        room_104.setHasComputers(false);

        Classroom room_405 = new Classroom("405", "Аудитория", 50);
        room_405.setHasProjector(true);
        room_405.setHasComputers(true);

        Classroom room_111 = new Classroom("111", "Лаборатория", 25);
        room_111.setHasComputers(true);

        System.out.println("✓ " + room_104);
        System.out.println("✓ " + room_405);
        System.out.println("✓ " + room_111);
        System.out.println();

        System.out.println("6. Создание курсов:");
        System.out.println("─────────────────────────────────────────────────────────────");
        Course courseOS = new Course("PROG101", "Операционные среды", 4, 30);
        Course courseAlgo = new Course("ALGO201", "Алгоритмы и структуры данных", 4, 25);
        Course courseBCN = new Course("PROG102", "Основы компьютерных сетей", 3, 20);

        courseOS.assignTeacher(prof_Sirotko);
        courseAlgo.assignTeacher(prof_Glamazdin);
        courseBCN.assignTeacher(prof_Glecevich);

        deptCS.addCourse(courseOS);
        deptACE.addCourse(courseBCN);
        deptCS.addCourse(courseAlgo);

        Schedule schedOS = new Schedule("MONDAY", LocalTime.of(10, 0), LocalTime.of(12, 0), room_104);
        Schedule schedAlgo = new Schedule("WEDNESDAY", LocalTime.of(14, 0), LocalTime.of(16, 0), room_405);
        Schedule schedBSN = new Schedule("FRIDAY", LocalTime.of(9, 0), LocalTime.of(11, 0), room_111);

        courseOS.setSchedule(schedOS);
        courseAlgo.setSchedule(schedAlgo);
        courseBCN.setSchedule(schedBSN);

        System.out.println("✓ " + courseOS);
        System.out.println("✓ " + courseAlgo);
        System.out.println("✓ " + courseBCN);
        System.out.println();

        System.out.println("7. Создание студентов:");
        System.out.println("─────────────────────────────────────────────────────────────");
        Student student1 = new Student(
                "Александр", "Сидоров", LocalDate.of(2004, 1, 15),
                "sidorov@gmail.com", "+375 (29) 444-44-44",
                "STU001", 2022
        );
        student1.setCurrentYear(2);

        Student student2 = new Student(
                "Виктория", "Волкова", LocalDate.of(2003, 6, 20),
                "volkova@gmail.com", "+375 (33) 555-55-55",
                "STU002", 2021
        );
        student2.setCurrentYear(3);

        Student student3 = new Student(
                "Дмитрий", "Морозов", LocalDate.of(2004, 11, 8),
                "morozov@gmail.com", "+375 (25) 666-66-66",
                "STU003", 2022
        );
        student3.setCurrentYear(2);

        System.out.println("✓ " + student1);
        System.out.println("✓ " + student2);
        System.out.println("✓ " + student3);
        System.out.println();

        System.out.println("8. Запись студентов на курсы:");
        System.out.println("─────────────────────────────────────────────────────────────");

        try {
            student1.enrollInCourse(courseOS);
            System.out.println("✓ " + student1.getFullName() + " записан на " + courseOS.getName());

            student1.enrollInCourse(courseBCN);
            System.out.println("✓ " + student1.getFullName() + " записан на " + courseBCN.getName());

            student2.enrollInCourse(courseAlgo);
            System.out.println("✓ " + student2.getFullName() + " записан на " + courseAlgo.getName());

            student2.enrollInCourse(courseOS);
            System.out.println("✓ " + student2.getFullName() + " записан на " + courseOS.getName());

            student3.enrollInCourse(courseOS);
            System.out.println("✓ " + student3.getFullName() + " записан на " + courseOS.getName());

            student3.enrollInCourse(courseAlgo);
            System.out.println("✓ " + student3.getFullName() + " записан на " + courseAlgo.getName());

        } catch (Exception e) {
            System.err.println("✗ Ошибка при записи: " + e.getMessage());
        }
        System.out.println();

        System.out.println("9. Выставление оценок:");
        System.out.println("─────────────────────────────────────────────────────────────");

        for (Enrollment enrollment : student1.getEnrollments()) {
            Grade grade = new Grade(85);
            grade.setComments("Хорошая работа на семинарах");
            enrollment.setGrade(grade);
            System.out.println("✓ " + student1.getFullName() + " получил оценку " +
                    grade.getLetter() + " (" + grade.getValue() + ") по " +
                    enrollment.getCourse().getName());
        }

        for (Enrollment enrollment : student2.getEnrollments()) {
            Grade grade = new Grade(92);
            grade.setComments("Отличные результаты");
            enrollment.setGrade(grade);
            System.out.println("✓ " + student2.getFullName() + " получил оценку " +
                    grade.getLetter() + " (" + grade.getValue() + ") по " +
                    enrollment.getCourse().getName());
        }

        for (Enrollment enrollment : student3.getEnrollments()) {
            Grade grade = new Grade(78);
            grade.setComments("Средние результаты");
            enrollment.setGrade(grade);
            System.out.println("✓ " + student3.getFullName() + " получил оценку " +
                    grade.getLetter() + " (" + grade.getValue() + ") по " +
                    enrollment.getCourse().getName());
        }
        System.out.println();

        System.out.println("10. Общая информация:");
        System.out.println("─────────────────────────────────────────────────────────────");

        System.out.println("\nИнформация об университете:");
        System.out.println(university);

        System.out.println("\nИнформация о ФКСиС:");
        System.out.println(facultyCSaN);

        System.out.println("\nКафедра информатики:");
        System.out.println(deptCS);

        System.out.println("\nИнформация о курсах:");
        System.out.println("  • " + courseOS + " - занято: " + courseOS.getEnrolledCount() + "/" +
                courseOS.getMaxStudents());
        System.out.println("  • " + courseAlgo + " - занято: " + courseAlgo.getEnrolledCount() + "/" +
                courseAlgo.getMaxStudents());
        System.out.println("  • " + courseBCN + " - занято: " + courseBCN.getEnrolledCount() + "/" +
                courseBCN.getMaxStudents());

        System.out.println("\nУспеваемость студентов:");
        System.out.println("  • " + student1 + " - GPA: " + String.format("%.2f", student1.getGpa()));
        System.out.println("  • " + student2 + " - GPA: " + String.format("%.2f", student2.getGpa()));
        System.out.println("  • " + student3 + " - GPA: " + String.format("%.2f", student3.getGpa()));

        System.out.println("\nИнформация о преподавателях:");
        System.out.println("  • " + prof_Sirotko);
        System.out.println("  • " + prof_Glamazdin);
        System.out.println("  • " + prof_Glecevich);

        System.out.println("\nРасписание занятий:");
        System.out.println("  • OS: " + courseOS.getSchedule());
        System.out.println("  • Алгоритмы: " + courseAlgo.getSchedule());
        System.out.println("  • Сети: " + courseBCN.getSchedule());
    }
}