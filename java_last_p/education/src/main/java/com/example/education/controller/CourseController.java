package com.example.education.controller;

import com.example.education.model.Course;
import com.example.education.repository.ICourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("course")
public class CourseController {
    @Autowired
    private ICourseRepository iCourseRepository;

    @GetMapping
    public List<Course> getAllCourse() {
        return iCourseRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Course save(@RequestBody Course course) {
        return iCourseRepository.save(course);
    }
    @GetMapping("{id}")
    public Course findCourseById(@PathVariable("id") Long id){

        Optional<Course> course =iCourseRepository.findById(id);
        if(course.isPresent()){
            return course.get();
        }
        else return null;
    }
    @DeleteMapping("{id}")
    public void deleteCourseById(@PathVariable("id") Long id){

        iCourseRepository.deleteById(id);
    }
    @PutMapping("{id}")
    public Course updaateCourseById(@PathVariable("id") Long id,@RequestBody Course courseRequest){

        Course result = new Course();
        Optional<Course> courseData =iCourseRepository.findById(id);
        if(courseData.isPresent()){
            result = courseData.get();
        }
        if(courseRequest.getTitle() != null){
          result.setTitle(courseRequest.getTitle());

        }
        if(courseRequest.getCode() > 0){
            result.setCode(courseRequest.getCode());
        }
        return  iCourseRepository.save(result);

    }
}
