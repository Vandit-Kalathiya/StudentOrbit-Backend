package com.studentOrbit.generate_report_app.Service;

import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.studentOrbit.generate_report_app.Helper.MarksReportGenerateRequest;
import com.studentOrbit.generate_report_app.Model.*;
import com.studentOrbit.generate_report_app.Pdf.MarksReport.Footer;
import com.studentOrbit.generate_report_app.Pdf.MarksReport.PdfUtils;
import com.studentOrbit.generate_report_app.entity.Batches.Batch;
import com.studentOrbit.generate_report_app.entity.Faculty.Faculty;
import com.studentOrbit.generate_report_app.entity.Groups.Group;
import com.studentOrbit.generate_report_app.entity.Groups.Technology;
import com.studentOrbit.generate_report_app.entity.Student.Student;
import com.studentOrbit.generate_report_app.entity.Task.Rubrics;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class StudentPerformanceReport {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${base_url}")
    private String BASE_URL;

    private HttpEntity<Void> getEntity(HttpServletRequest request) {
        String token = "null";

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            throw new RuntimeException("Authentication token not found in cookies");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(headers);
    }

    private Student getStudentDetails(String username, HttpServletRequest request) {
        HttpEntity<Void> entity = getEntity(request);

        String url = BASE_URL + "/students/u/" + username;
        ResponseEntity<Student> res = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        if (!res.hasBody()) {
            throw new RuntimeException("Student not found");
        }

        return res.getBody();
    }

    private Group getGroupDetails(String name, HttpServletRequest request) {
        HttpEntity<Void> entity = getEntity(request);
        String url = BASE_URL + "/faculty/groups/" + ("gid/" + name);

        ResponseEntity<?> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        Object responseBody = response.getBody();
        Group group = new Group();

        if (responseBody instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) responseBody;

            if (map.get("id") instanceof String) {
                group.setId((String) map.get("id"));
            }
            if (map.get("uniqueGroupId") instanceof String) {
                group.setUniqueGroupId((String) map.get("uniqueGroupId"));
            }
            if (map.get("groupName") instanceof String) {
                group.setGroupName((String) map.get("groupName"));
            }
            if (map.get("groupDescription") instanceof String) {
                group.setGroupDescription((String) map.get("groupDescription"));
            }
            if (map.get("batchName") instanceof String) {
                group.setBatchName((String) map.get("batchName"));
            }
            if (map.get("groupLeader") instanceof String) {
                group.setGroupLeader((String) map.get("groupLeader"));
            }
            if (map.get("projectStatus") instanceof String) {
                group.setProjectStatus((String) map.get("projectStatus"));
            }
            if (map.get("startDate") instanceof String) {
                group.setStartDate((String) map.get("startDate"));
            }
            if (map.get("createdAt") instanceof String) {
                group.setCreatedAt(LocalDateTime.parse((String) map.get("createdAt")));
            }

            if (map.get("students") instanceof List) {
                List<?> studentsList = (List<?>) map.get("students");
                Set<Student> studentsSet = new HashSet<>();

                for (Object obj : studentsList) {
                    if (obj instanceof Map) {
                        Map<?, ?> studentMap = (Map<?, ?>) obj;
                        Student student = new Student();
                        studentsSet.add(student);
                    }
                }
                group.setStudents(studentsSet);
            }

            if (map.get("technologies") instanceof List) {
                List<?> techList = (List<?>) map.get("technologies");
                List<Technology> technologies = new ArrayList<>();

                for (Object techObj : techList) {
                    if (techObj instanceof Map) {
                        Map<?, ?> techMap = (Map<?, ?>) techObj;
                        Technology technology = new Technology();
                        technologies.add(technology);
                    }
                }
                group.setTechnologies(technologies);
            }

            if (map.get("batch") instanceof Map) {
                Map<?, ?> batchMap = (Map<?, ?>) map.get("batch");
                Batch batch = new Batch();
                group.setBatch(batch);
            }

            if (map.get("mentor") instanceof Map) {
                Map<?, ?> mentorMap = (Map<?, ?>) map.get("mentor");
                Faculty mentor = new Faculty();
                group.setMentor(mentor);
            }

            if (map.get("weeks") instanceof List) {
                List<?> weeksList = (List<?>) map.get("weeks");
                List<com.studentOrbit.generate_report_app.entity.Weeks.Week> weeks = new ArrayList<>();

                for (Object weekObj : weeksList) {
                    if (weekObj instanceof Map) {
                        Map<?, ?> weekMap = (Map<?, ?>) weekObj;
                        com.studentOrbit.generate_report_app.entity.Weeks.Week week = new com.studentOrbit.generate_report_app.entity.Weeks.Week();

                        if (weekMap.get("id") instanceof String) {
                            week.setId((String) weekMap.get("id"));
                        }
                        if (weekMap.get("weekNumber") instanceof Integer) {
                            week.setWeekNumber((Integer) weekMap.get("weekNumber"));
                        }

                        if (weekMap.get("tasks") instanceof List) {
                            List<?> tasksList = (List<?>) weekMap.get("tasks");
                            List<com.studentOrbit.generate_report_app.entity.Task.Task> tasks = new ArrayList<>();

                            for (Object taskObj : tasksList) {
                                if (taskObj instanceof Map) {
                                    Map<?, ?> taskMap = (Map<?, ?>) taskObj;
                                    com.studentOrbit.generate_report_app.entity.Task.Task task = new com.studentOrbit.generate_report_app.entity.Task.Task();

                                    if (taskMap.get("id") instanceof String) {
                                        task.setId((String) taskMap.get("id"));
                                    }
                                    if (taskMap.get("name") instanceof String) {
                                        task.setName((String) taskMap.get("name"));
                                    }
                                    if (taskMap.get("description") instanceof String) {
                                        task.setDescription((String) taskMap.get("description"));
                                    }
                                    if (taskMap.get("scoredMarks") instanceof Integer) {
                                        task.setScoredMarks((Integer) taskMap.get("scoredMarks"));
                                    }

                                    if (taskMap.get("rubrics") instanceof List) {
                                        List<?> rubricsList = (List<?>) taskMap.get("rubrics");
                                        List<Rubrics> rubrics = new ArrayList<>();

                                        for (Object rubricObj : rubricsList) {
                                            if (rubricObj instanceof Map) {
                                                Map<?, ?> rubricMap = (Map<?, ?>) rubricObj;
                                                Rubrics rubric = new Rubrics();

                                                if (rubricMap.get("rubricName") instanceof String) {
                                                    rubric.setRubricName((String) rubricMap.get("rubricName"));
                                                }
                                                if (rubricMap.get("rubricScore") instanceof Integer) {
                                                    rubric.setRubricScore((Integer) rubricMap.get("rubricScore"));
                                                }

                                                rubrics.add(rubric);
                                            }
                                        }
                                        task.setRubrics(rubrics);
                                    }

                                    task.setWeek(week);
                                    tasks.add(task);
                                }
                            }
                            week.setTasks(tasks);
                        }

                        week.setGroup(group);
                        weeks.add(week);
                    }
                }
                weeks.sort(Comparator.comparing(com.studentOrbit.generate_report_app.entity.Weeks.Week::getWeekNumber));
                group.setWeeks(weeks);
            }
        }
        System.out.println(group);

        return group;
    }

    private List<Student> getGroupMembers(String name, HttpServletRequest request) {
        HttpEntity<Void> entity = getEntity(request);

        String url2 = BASE_URL + "/faculty/groups/" + ("members/" + name);

        return Objects.requireNonNull(restTemplate.exchange(
                url2,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Set<Student>>() {
                }
        ).getBody()).stream().toList();
    }

    public ByteArrayInputStream main(MarksReportGenerateRequest marksReportGenerateRequest, HttpServletRequest request) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            if (marksReportGenerateRequest.getReportType().equals("student")) {
                StudentData studentData = createSampleStudentData(marksReportGenerateRequest, request);
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDoc = new PdfDocument(writer);
                pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new Footer());
                Document document = new Document(pdfDoc);

                PdfUtils.addHeader(document, studentData);
                PdfUtils.addStudentOrGroupInfo(document, studentData);
                PdfUtils.addCriteriaLegend(document, studentData.getCriteria());
                for (com.studentOrbit.generate_report_app.Model.Week week : studentData.getWeeks()) {
                    PdfUtils.addWeekSection(document, week, studentData.getCriteria());
                }
                PdfUtils.addPerformanceSummary(document, studentData);

                document.close();
            } else {
                // Placeholder for group report logic
                System.out.println("Group report generation not implemented.");
            }

            System.out.println("PDF generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private StudentData createSampleStudentData(MarksReportGenerateRequest marksReportGenerateRequest, HttpServletRequest request) {
        StudentData data = new StudentData();

        Student student = getStudentDetails(marksReportGenerateRequest.getName(), request);
        Group group = getGroupDetails(marksReportGenerateRequest.getProjectId(), request);

        data.setStudentName(student.getStudentName());
        data.setStudentId(student.getUsername());
        data.setProjectName(group.getGroupName());

        List<Criterion> criteria = Arrays.asList(
                new Criterion("CQ", "Code Quality"),
                new Criterion("TC", "Teamwork & Collaboration"),
                new Criterion("TM", "Task Completion on Time"),
                new Criterion("CI", "Creativity & Innovation"),
                new Criterion("PS", "Problem-Solving Ability"),
                new Criterion("RU", "Research & Understanding"),
                new Criterion("EE", "Effort & Engagement")
        );
        data.setCriteria(criteria);

        Map<String, String> criterionMap = criteria.stream()
                .collect(Collectors.toMap(Criterion::getName, Criterion::getAbbreviation));

        List<com.studentOrbit.generate_report_app.Model.Week> weeks = new ArrayList<>();
        int totalMarks = 0;
        int totalPossibleMarks = 0;

        for (com.studentOrbit.generate_report_app.entity.Weeks.Week groupWeek : group.getWeeks()) {
            com.studentOrbit.generate_report_app.Model.Week week = new com.studentOrbit.generate_report_app.Model.Week(groupWeek.getWeekNumber());

            for (com.studentOrbit.generate_report_app.entity.Task.Task groupTask : groupWeek.getTasks()) {
                com.studentOrbit.generate_report_app.Model.Task task = new com.studentOrbit.generate_report_app.Model.Task(groupTask.getName(), groupTask.getDescription());

                int scoredMarks = groupTask.getScoredMarks();
                task.setPointsEarned(scoredMarks);
                task.setTotalPoints(28);
                task.setScore((scoredMarks * 100) / 28);

                Map<String, Integer> ratings = new HashMap<>();
                for (Rubrics rubric : groupTask.getRubrics()) {
                    String abbreviation = criterionMap.get(rubric.getRubricName());
                    if (abbreviation != null) {
                        ratings.put(abbreviation, rubric.getRubricScore());
                    }
                }
                task.setRatings(ratings);

                week.addTask(task);

                totalMarks += scoredMarks;
                totalPossibleMarks += 28;
            }

            weeks.add(week);
        }

        data.setWeeks(weeks);
        data.setTotalMarksEarned(totalMarks);
        data.setTotalPossibleMarks(totalPossibleMarks);

        data.setOverallPercentage(totalPossibleMarks > 0 ? (totalMarks * 100) / totalPossibleMarks : 0);

        return data;
    }
}
