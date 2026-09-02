package com.examly.springapp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
// MockMvc request builders
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


// Hamcrest matchers
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpringappApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    // ===================== DAY 3 - Directory Checks =====================
    @Test @Order(1)
    void Day3_test_Controller_Directory_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/controller").isDirectory());
    }

    @Test @Order(2)
    void Day3_test_Model_Directory_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/model").isDirectory());
    }

    @Test @Order(3)
    void Day3_test_Service_Directory_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service").isDirectory());
    }

    @Test @Order(4)
    void Day3_test_Repository_Directory_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/repository").isDirectory());
    }

    // ===================== DAY 4 - Model File Checks =====================
    @Test @Order(5)
    void Day4_test_DoctorModel_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/model/Doctor.java").isFile());
    }

    @Test @Order(6)
    void Day4_test_PatientModel_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/model/Patient.java").isFile());
    }

    @Test @Order(7)
    void Day4_test_Appointment_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/model/Appointment.java").isFile());
    }

    @Test @Order(8)
    void Day4_test_MedicalRecord_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/model/MedicalRecord.java").isFile());
    }

    @Test @Order(9)
    void Day4_test_Patient_Has_Entity_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.model.Patient");
            Class<?> annotation = Class.forName("jakarta.persistence.Entity");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @Entity annotation is missing on Patient class");
        } catch (ClassNotFoundException e) {
            fail("❌ Patient class not found.");
        } catch (Exception e) {
            fail("❌ Unable to check @Entity annotation on Patient.");
        }
    }

    @Test @Order(10)
    void test_Patient_Has_Id_Annotation_On_Field() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.model.Patient");
            Class<?> idAnnotation = Class.forName("jakarta.persistence.Id");
            boolean found = false;
            for (var field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent((Class<? extends Annotation>) idAnnotation)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "❌ No field in Patient class is annotated with @Id");
        } catch (ClassNotFoundException e) {
            fail("❌ Patient class not found.");
        } catch (Exception e) {
            fail("❌ Unable to verify @Id annotation in Patient class.");
        }
    }

    // ===================== DAY 5 - Repository Tests =====================
    // File Exists
    @Test @Order(11)
    void Day5_test_DoctorRepository_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/repository/DoctorRepository.java").isFile());
    }

    @Test @Order(12)
    void Day5_test_PatientRepository_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/repository/PatientRepository.java").isFile());
    }

    @Test @Order(13)
    void Day5_test_AppointmentRepository_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/repository/AppointmentRepository.java").isFile());
    }

    @Test @Order(14)
    void Day5_test_MedicalRecordRepository_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/repository/MedicalRecordRepository.java").isFile());
    }

    // @Repository Annotations
    @Test @Order(15)
    void Day5_test_DoctorRepository_Has_Repository_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.repository.DoctorRepository");
            Class<?> annotation = Class.forName("org.springframework.stereotype.Repository");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @Repository annotation is missing on DoctorRepository class");
        } catch (Exception e) { fail("❌ Unable to verify @Repository annotation on DoctorRepository."); }
    }

    @Test @Order(16)
    void Day5_test_PatientRepository_Has_Repository_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.repository.PatientRepository");
            Class<?> annotation = Class.forName("org.springframework.stereotype.Repository");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @Repository annotation is missing on PatientRepository class");
        } catch (Exception e) { fail("❌ Unable to verify @Repository annotation on PatientRepository."); }
    }

    @Test @Order(17)
    void Day5_test_AppointmentRepository_Has_Repository_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.repository.AppointmentRepository");
            Class<?> annotation = Class.forName("org.springframework.stereotype.Repository");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @Repository annotation is missing on AppointmentRepository class");
        } catch (Exception e) { fail("❌ Unable to verify @Repository annotation on AppointmentRepository."); }
    }

    @Test @Order(18)
    void Day5_test_MedicalRecordRepository_Has_Repository_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.repository.MedicalRecordRepository");
            Class<?> annotation = Class.forName("org.springframework.stereotype.Repository");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @Repository annotation is missing on MedicalRecordRepository class");
        } catch (Exception e) { fail("❌ Unable to verify @Repository annotation on MedicalRecordRepository."); }
    }

    @Test @Order(19)
    void Day5_test_AdminRepository_Has_Repository_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.repository.AdminRepository");
            Class<?> annotation = Class.forName("org.springframework.stereotype.Repository");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @Repository annotation is missing on AdminRepository class");
        } catch (Exception e) { fail("❌ Unable to verify @Repository annotation on AdminRepository."); }
    }

    // ===================== DAY 6 - Controller Tests =====================
    @Test @Order(20)
    void Day6_test_DoctorController_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/controller/DoctorController.java").isFile());
    }

    @Test @Order(21)
    void Day6_test_PatientController_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/controller/PatientController.java").isFile());
    }

    @Test @Order(22)
    void Day6_test_AppointmentController_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/controller/AppointmentController.java").isFile());
    }

    @Test @Order(23)
    void Day6_test_MedicalRecordController_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/controller/MedicalRecordController.java").isFile());
    }


    // @RestController checks
    @Test @Order(24)
    void Day6_test_DoctorController_Has_RestController_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.controller.DoctorController");
            Class<?> annotation = Class.forName("org.springframework.web.bind.annotation.RestController");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @RestController annotation is missing on DoctorController class");
        } catch (Exception e) { fail("❌ Unable to verify @RestController annotation on DoctorController."); }
    }

    @Test @Order(25)
    void Day6_test_PatientController_Has_RestController_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.controller.PatientController");
            Class<?> annotation = Class.forName("org.springframework.web.bind.annotation.RestController");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @RestController annotation is missing on PatientController class");
        } catch (Exception e) { fail("❌ Unable to verify @RestController annotation on PatientController."); }
    }

    @Test @Order(26)
    void Day6_test_AppointmentController_Has_RestController_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.controller.AppointmentController");
            Class<?> annotation = Class.forName("org.springframework.web.bind.annotation.RestController");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @RestController annotation is missing on AppointmentController class");
        } catch (Exception e) { fail("❌ Unable to verify @RestController annotation on AppointmentController."); }
    }

    @Test @Order(27)
    void Day6_test_MedicalRecordController_Has_RestController_Annotation() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.controller.MedicalRecordController");
            Class<?> annotation = Class.forName("org.springframework.web.bind.annotation.RestController");
            assertTrue(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation),
                "❌ @RestController annotation is missing on MedicalRecordController class");
        } catch (Exception e) { fail("❌ Unable to verify @RestController annotation on MedicalRecordController."); }
    }
    @Test @Order(28)

    void Day6_test_DoctorController_Has_PostMapping() {
    try {
        // Load the controller class
        Class<?> clazz = Class.forName("com.examly.springapp.controller.DoctorController");

        // Load @PostMapping annotation dynamically
        Class<?> postMapping = Class.forName("org.springframework.web.bind.annotation.PostMapping");

        boolean found = false;

        // Check if any method has @PostMapping
        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent((Class<? extends Annotation>) postMapping)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "❌ No method with @PostMapping found in DoctorController");

    } catch (ClassNotFoundException e) {
        fail("❌ DoctorController class not found.");
    } catch (Exception e) {
        fail("❌ Unable to verify @PostMapping annotation in DoctorController.");
    }
}


@Test @Order(29)

    void Day6_test_DoctorController_Has_GetMapping() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.controller.DoctorController");
            Class<?> getMapping = Class.forName("org.springframework.web.bind.annotation.GetMapping");

            boolean found = false;
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent((Class<? extends Annotation>) getMapping)) {
                    found = true;
                    break;
                }
            }

            assertTrue(found, "❌ No @GetMapping method found in DoctorController");

        } catch (ClassNotFoundException e) {
            fail("❌ DoctorController class not found.");
        } catch (Exception e) {
            fail("❌ Unable to verify @GetMapping in DoctorController.");
        }
    }


	@Test @Order(30)

    void Day6_test_DoctorController_Has_PutMapping() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.controller.DoctorController");
            Class<?> putMapping = Class.forName("org.springframework.web.bind.annotation.PutMapping");

            boolean found = false;
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent((Class<? extends Annotation>) putMapping)) {
                    found = true;
                    break;
                }
            }

            assertTrue(found, "❌ No @PutMapping method found in DoctorController");

        } catch (ClassNotFoundException e) {
            fail("❌ DoctorController class not found.");
        } catch (Exception e) {
            fail("❌ Unable to verify @PutMapping in DoctorController.");
        }
    }

	@Test @Order(31)

    void Day6_test_DoctorController_Has_DeleteMapping() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.controller.DoctorController");
            Class<?> deleteMapping = Class.forName("org.springframework.web.bind.annotation.DeleteMapping");

            boolean found = false;
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent((Class<? extends Annotation>) deleteMapping)) {
                    found = true;
                    break;
                }
            }

            assertTrue(found, "❌ No @DeleteMapping method found in DoctorController");

        } catch (ClassNotFoundException e) {
            fail("❌ DoctorController class not found.");
        } catch (Exception e) {
            fail("❌ Unable to verify @DeleteMapping in DoctorController.");
        }
    }

    @Test @Order(32)
    public void Day6_testCreateDoctor_NoBody_StatusBadRequest() throws Exception {
    
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
     
    @Test @Order(33)
    public void Day6_testGetAllDoctor_StatusNoCotent() throws Exception {
    
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
        

    // ===================== DAY 7 - @RequestMapping Tests =====================

@Test
@Order(35)
void Day7_test_DoctorController_Has_RequestMapping() {
    try {
        Class<?> clazz = Class.forName("com.examly.springapp.controller.DoctorController");
        Class<?> requestMapping = Class.forName("org.springframework.web.bind.annotation.RequestMapping");

        boolean found = clazz.isAnnotationPresent((Class<? extends Annotation>) requestMapping);
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent((Class<? extends Annotation>) requestMapping)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "❌ No @RequestMapping found on CategoryController (class or methods)");

    } catch (ClassNotFoundException e) {
        fail("❌ DoctorController class not found.");
    } catch (Exception e) {
        fail("❌ Unable to verify @RequestMapping in DoctorController.");
    }
}

@Test
@Order(36)
void Day7_test_PatientController_Has_RequestMapping() {
    try {
        Class<?> clazz = Class.forName("com.examly.springapp.controller.PatientController");
        Class<?> requestMapping = Class.forName("org.springframework.web.bind.annotation.RequestMapping");

        boolean found = clazz.isAnnotationPresent((Class<? extends Annotation>) requestMapping);
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent((Class<? extends Annotation>) requestMapping)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "❌ No @RequestMapping found on ProductController (class or methods)");

    } catch (ClassNotFoundException e) {
        fail("❌ PatientController class not found.");
    } catch (Exception e) {
        fail("❌ Unable to verify @RequestMapping in PatientController.");
    }
}

@Test
@Order(37)
void Day7_test_AppointmentController_Has_RequestMapping() {
    try {
        Class<?> clazz = Class.forName("com.examly.springapp.controller.AppointmentController");
        Class<?> requestMapping = Class.forName("org.springframework.web.bind.annotation.RequestMapping");

        boolean found = clazz.isAnnotationPresent((Class<? extends Annotation>) requestMapping);
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent((Class<? extends Annotation>) requestMapping)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "❌ No @RequestMapping found on AppointmentController (class or methods)");

    } catch (ClassNotFoundException e) {
        fail("❌ AppointmentController class not found.");
    } catch (Exception e) {
        fail("❌ Unable to verify @RequestMapping in AppointmentController.");
    }
}

@Test
@Order(38)
void Day7_test_MedicalRecordController_Has_RequestMapping() {
    try {
        Class<?> clazz = Class.forName("com.examly.springapp.controller.MedicalRecordController");
        Class<?> requestMapping = Class.forName("org.springframework.web.bind.annotation.RequestMapping");

        boolean found = clazz.isAnnotationPresent((Class<? extends Annotation>) requestMapping);
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent((Class<? extends Annotation>) requestMapping)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "❌ No @RequestMapping found on MedicalRecordController (class or methods)");

    } catch (ClassNotFoundException e) {
        fail("❌ MedicalRecordController class not found.");
    } catch (Exception e) {
        fail("❌ Unable to verify @RequestMapping in MedicalRecordController.");
    }
}

@Test
@Order(39)
void Day7_test_AdminController_Has_RequestMapping() {
    try {
        Class<?> clazz = Class.forName("com.examly.springapp.controller.AdminController");
        Class<?> requestMapping = Class.forName("org.springframework.web.bind.annotation.RequestMapping");

        boolean found = clazz.isAnnotationPresent((Class<? extends Annotation>) requestMapping);
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent((Class<? extends Annotation>) requestMapping)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "❌ No @RequestMapping found on AdminController (class or methods)");

    } catch (ClassNotFoundException e) {
        fail("❌ AdminController class not found.");
    } catch (Exception e) {
        fail("❌ Unable to verify @RequestMapping in AdminController.");
    }
}
@Test
@Order(40)
void Day7_test_DoctorController_Has_PathVariable() {
    try {
        Class<?> clazz = Class.forName("com.examly.springapp.controller.DoctorController");
        Class<?> pathVariable = Class.forName("org.springframework.web.bind.annotation.PathVariable");

        boolean found = false;

        for (Method method : clazz.getDeclaredMethods()) {
            for (Parameter param : method.getParameters()) {
                if (param.isAnnotationPresent((Class<? extends Annotation>) pathVariable)) {
                    found = true;
                    break;
                }
            }
            if (found) break;
        }

        assertTrue(found, "❌ No @PathVariable annotation found in any method parameter of CategoryController");

    } catch (ClassNotFoundException e) {
        fail("❌ DoctorController class not found.");
    } catch (Exception e) {
        fail("❌ Unable to verify @PathVariable in DoctorController.");
    }
}

@Test
	@Order(41)
    void Day7_test_PatientController_Has_PathVariable() {
        try {
            Class<?> clazz = Class.forName("com.examly.springapp.controller.PatientController");
            Class<?> pathVariable = Class.forName("org.springframework.web.bind.annotation.PathVariable");

            boolean found = false;

            for (Method method : clazz.getDeclaredMethods()) {
                for (Parameter param : method.getParameters()) {
                    if (param.isAnnotationPresent((Class<? extends Annotation>) pathVariable)) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            assertTrue(found, "❌ No @PathVariable found in any method parameter of ProductController");

        } catch (ClassNotFoundException e) {
            fail("❌ PatientController class not found.");
        } catch (Exception e) {
            fail("❌ Unable to verify @PathVariable in PatientController.");
        }
    }



    // ===================== DAY 8 - Service / ServiceImpl File Checks =====================
    @Test @Order(42)
    void Day8_test_DoctorService_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/DoctorService.java").isFile());
    }

    @Test @Order(43)
    void Day8_test_PatientService_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/PatientService.java").isFile());
    }

    @Test @Order(44)
    void Day8_test_AppointmentService_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/AppointmentService.java").isFile());
    }

    @Test @Order(45)
    void Day8_test_MedicalRecordService_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/MedicalRecordService.java").isFile());
    }

    @Test @Order(46)
    void Day8_test_AdminService_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/AdminService.java").isFile());
    }

    @Test @Order(47)
    void Day8_test_DoctorServiceImpl_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/DoctorServiceImpl.java").isFile());
    }

    @Test @Order(48)
    void Day8_test_PatientServiceImpl_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/PatientServiceImpl.java").isFile());
    }

    @Test @Order(49)
    void Day8_test_AppointmentServiceImpl_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/AppointmentServiceImpl.java").isFile());
    }

    @Test @Order(50)
    void Day8_test_MedicalRecordServiceImpl_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/MedicalRecordServiceImpl.java").isFile());
    }

    @Test @Order(51)
    void Day8_test_AdminServiceImpl_File_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/service/AdminServiceImpl.java").isFile());
    }

    // ===================== DAY 8 - DoctorController MockMvc Tests =====================

// POST /doctors
@Test @Order(52)
public void Day8_testAddDoctor() throws Exception {
    String requestBody = "{ \"name\": \"Dr. John\", \"specialization\": \"Cardiology\" }";

    mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Dr. John"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value("Cardiology"))
            .andReturn();
}

// GET /doctors
@Test @Order(53)
public void Day8_testGetAllDoctors() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Dr. John"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialization").value("Cardiology"))
            .andReturn();
}

// GET /doctors/{id}
@Test @Order(54)
public void Day8_testGetDoctorById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/doctors/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Dr. John"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value("Cardiology"))
            .andReturn();
}

// PUT /doctors/{id}
@Test @Order(55)
public void Day8_testUpdateDoctor() throws Exception {
    String requestBody = "{ \"name\": \"Dr. John Doe\", \"specialization\": \"Neurology\" }";

    mockMvc.perform(MockMvcRequestBuilders.put("/doctors/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Dr. John Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value("Neurology"))
            .andReturn();
}

// ===================== DAY 9 - DoctorController Pagination Tests =====================

// Check pageNumber
@Test @Order(56)
public void Day9_testPagination_PageNumberApplied() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/doctors/page/0/5")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageNumber").value(0));
}

// Check pageSize
@Test @Order(57)
public void Day9_testPagination_PageSizeApplied() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/doctors/page/1/10")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageSize").value(10));
}

// Check sorting object exists
@Test @Order(58)
public void Day9_testPagination_SortingPresent() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/doctors/page/0/5")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.sort").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.sort.sorted").isBoolean());
}

// Check content array exists
@Test @Order(59)
public void Day9_testPagination_ContentArrayExists() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/doctors/page/0/5")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
}

// Check totalElements exists
@Test @Order(60)
public void Day9_testPagination_TotalElementsExists() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/doctors/page/0/5")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").exists());
}

// Check totalPages exists
@Test @Order(61)
public void Day9_testPagination_TotalPagesExists() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/doctors/page/0/5")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").exists());
}
// ===================== DAY 10 - PatientController & DoctorController CRUD =====================

// POST /patients
@Test
@Order(62)
public void Day10_testAddPatient() throws Exception {
    String requestBody = "{ \"name\": \"John Doe\", \"email\": \"john@example.com\", \"phone\": \"1234567890\", \"address\": \"123 Street\", \"age\": 30 }";

    mockMvc.perform(MockMvcRequestBuilders.post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john@example.com"))
            .andReturn();
}

// GET /patients/{id}
@Test @Order(63)
public void Day10_testGetPatientById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/patients/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
            .andReturn();
}

// PUT /patients/{id}
@Test @Order(64)
public void Day10_testUpdatePatient() throws Exception {
    String requestBody = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.com\", \"phone\": \"9876543210\", \"address\": \"456 Avenue\", \"age\": 28 }";

    mockMvc.perform(MockMvcRequestBuilders.put("/patients/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jane Doe"))
            .andReturn();
}
// POST /doctor
@Test @Order(65)
public void Day10_testAddDoctor() throws Exception {
    String requestBody = "{ " +
            "\"name\": \"Dr. John Doe\", " +
            "\"email\": \"john.doe@example.com\", " +
            "\"phone\": \"1234567890\", " +
            "\"specialization\": \"Cardiology\", " +
            "\"roomNumber\": 101 " +
            "}";

    mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Dr. John Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value("Cardiology"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roomNumber").value(101))
            .andReturn();
}
//PUT
@Test @Order(66)
public void Day10_testUpdateDoctor() throws Exception {
    String requestBody = "{ " +
            "\"name\": \"Dr. John Updated\", " +
            "\"email\": \"john.updated@example.com\", " +
            "\"phone\": \"0987654321\", " +
            "\"specialization\": \"Neurology\", " +
            "\"roomNumber\": 102 " +
            "}";

    mockMvc.perform(MockMvcRequestBuilders.put("/doctors/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Dr. John Updated"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value("Neurology"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roomNumber").value(102))
            .andReturn();
}

// ===================== DAY 11 - MedicalRecordController CRUD =====================

// POST /medicalrecords
@Test @Order(67)


public void Day11_testAddMedicalRecord() throws Exception {
    String requestBody = "{ \"diagnosis\": \"Flu\", \"prescription\": \"Rest and fluids\", \"patient\": { \"id\": 1 } }";

    mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecords")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value("Flu"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.prescription").value("Rest and fluids"))
            .andReturn();
}
//Get
// Positive Test - Patient has medical records 
// Now, test the JPQL query endpoint
@Test @Order(68)
public void testGetMedicalRecordsByPatient_Positive() throws Exception {
    // 1. Create a patient first
    String patientJson = "{ \"name\": \"John Doe\", \"email\": \"john@example.com\", \"phone\": \"1234567890\", \"address\": \"123 Street\", \"age\": 30 }";
    MvcResult patientResult = mockMvc.perform(MockMvcRequestBuilders.post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn();

    // Extract patient ID from the response
    String patientResponse = patientResult.getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    Long patientId = mapper.readTree(patientResponse).get("id").asLong();

    // 2. Create a medical record for this patient
    String recordJson = "{ \"diagnosis\": \"Flu\", \"prescription\": \"Rest and fluids\", \"patient\": { \"id\": " + patientId + " } }";
    mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecords")
            .contentType(MediaType.APPLICATION_JSON)
            .content(recordJson)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated());

    // 3. Test JPQL endpoint
    mockMvc.perform(MockMvcRequestBuilders.get("/medicalrecords/patient/" + patientId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].diagnosis").value("Flu"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].prescription").value("Rest and fluids"));
}

//Get
// Negative Test - Patient has no medical records - JPQL
@Test @Order(69)
public void testGetMedicalRecordsByPatient_Negative() throws Exception {
    // Using a patient ID that doesn't exist in the database
    mockMvc.perform(MockMvcRequestBuilders.get("/medicalrecords/patient/9999")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andExpect(MockMvcResultMatchers.content().string("No medical records found"));
}

// GET /medicalrecords/{id}
@Test @Order(70)
public void Day11_testGetMedicalRecordById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/medicalrecords/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value("Flu"))
            .andReturn();
}

// PUT /medicalrecords/{id}
@Test @Order(71)
public void Day11_testUpdateMedicalRecord() throws Exception {
    String requestBody = "{ \"diagnosis\": \"Cold\", \"prescription\": \"Medication\", \"patient\": { \"id\": 1 } }";

    mockMvc.perform(MockMvcRequestBuilders.put("/medicalrecords/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value("Cold"))
            .andReturn();
}

// ===================== DAY 12 - AppointmentController CRUD =====================

// POST /appointments
@Test @Order(72)
public void Day12_testAddAppointment() throws Exception {
    String requestBody = "{ " +
            "\"patient\": { \"id\": 1 }, " +
            "\"doctor\": { \"id\": 1 }, " +
            "\"appointmentTime\": \"2025-12-15T10:00:00\", " +
            "\"status\": \"BOOKED\", " +
            "\"notes\": \"First visit\" " +
            "}";

    mockMvc.perform(MockMvcRequestBuilders.post("/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BOOKED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.notes").value("First visit"))
            .andReturn();
}
// ✅ POSITIVE CASE – data matches JPQL filter
@Test @Order(73)
public void Day12_testGetAppointmentsByStatus_Positive() throws Exception {
    mockMvc.perform(get("/appointments/status/BOOKED")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
           // .andExpect(jsonPath("$[0].patientName", is("John")))
            .andExpect(jsonPath("$[0].status", is("BOOKED")));
}

// ❌ NEGATIVE CASE – no data for this status
@Test @Order(74)
public void Day12_testGetAppointmentsByStatus_NoData() throws Exception {
    mockMvc.perform(get("/appointments/status/CANCELLED")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
           // .andExpect(jsonPath("$", hasSize(0))); // empty list expected
}


// GET /appointments/{id}
@Test @Order(75)
public void Day12_testGetAppointmentById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/appointments/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.notes").value("First visit"))
            .andReturn();
}

// PUT /appointments/{id}
@Test @Order(76)
public void Day12_testUpdateAppointment() throws Exception {
    String requestBody = "{ " +
            "\"patient\": { \"id\": 1 }, " +
            "\"doctor\": { \"id\": 1 }, " +
            "\"appointmentTime\": \"2025-12-15T11:00:00\", " +
            "\"status\": \"COMPLETED\", " +
            "\"notes\": \"Follow-up\" " +
            "}";

    mockMvc.perform(MockMvcRequestBuilders.put("/appointments/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("COMPLETED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.notes").value("Follow-up"))
            .andReturn();
}
@Test @Order(77)
void Day13_testExceptionDirectoryExists() {
    assertTrue(new File("src/main/java/com/examly/springapp/exception").isDirectory(),
               "Exception directory should exist");
}

// ------------------- GLOBAL EXCEPTION HANDLER FILE -------------------
@Test @Order(78)
void Day13_testGlobalExceptionFileExists() {
    assertTrue(new File("src/main/java/com/examly/springapp/exception/GlobalExceptionHandler.java").isFile(),
               "GlobalExceptionHandler.java file should exist");
}
@Test @Order(79)
    void Day14test_configuartion_Directory_Exists() {
        assertTrue(new File("src/main/java/com/examly/springapp/configuration").isDirectory());
    }

@Test @Order(80)
public void Day15_testAOPLogFileExists() {
    assertTrue(new File("src/main/java/com/examly/springapp/aop").isDirectory()); 
}
}
