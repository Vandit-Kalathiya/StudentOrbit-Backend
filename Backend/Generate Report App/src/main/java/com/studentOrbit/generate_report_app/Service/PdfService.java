package com.studentOrbit.generate_report_app.Service;

import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.studentOrbit.generate_report_app.Helper.PdfGenerateRequest;
import com.studentOrbit.generate_report_app.Model.TaskData;
import com.studentOrbit.generate_report_app.Model.WeekData;
import com.studentOrbit.generate_report_app.Pdf.NormalReport.FooterHandler;
import com.studentOrbit.generate_report_app.Pdf.NormalReport.Header;
import com.studentOrbit.generate_report_app.Pdf.NormalReport.PdfUtils;
import com.studentOrbit.generate_report_app.entity.Attachment.Attachment;
import com.studentOrbit.generate_report_app.entity.Batches.Batch;
import com.studentOrbit.generate_report_app.entity.Faculty.Faculty;
import com.studentOrbit.generate_report_app.entity.Groups.Group;
import com.studentOrbit.generate_report_app.entity.Groups.Technology;
import com.studentOrbit.generate_report_app.entity.Student.Student;
import com.studentOrbit.generate_report_app.entity.Task.Rubrics;
import com.studentOrbit.generate_report_app.entity.Task.Task;
import com.studentOrbit.generate_report_app.entity.Weeks.Week;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PdfService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${base_url}")
    private String BASE_URL;

    public List<WeekData> fetchWeekData(PdfGenerateRequest pdfGenerateRequest, HttpServletRequest request) {
        String reportType = pdfGenerateRequest.getReportType();
        String identifier = pdfGenerateRequest.getIdentifier();
        String projectName = pdfGenerateRequest.getProjectName();

        List<WeekData> weekDataList = new ArrayList<>();
        Map<String, List<Attachment>> allTasksAttachmentMap = new HashMap<>();

        HttpEntity<Void> entity = getEntity(request);
        Group group = getGroupDetails(projectName, entity);

        String url = BASE_URL + "/faculty/groups/members/" + projectName;
        List<Student> members = Objects.requireNonNull(restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Set<Student>>() {}
        ).getBody()).stream().toList();

        List<Week> weeks = group.getWeeks().stream()
                .filter(week -> pdfGenerateRequest.getWeeks().contains(week.getWeekNumber()))
                .toList();

        List<Task> tasks = new ArrayList<>();
        if (reportType.equalsIgnoreCase("student")) {
            weeks.forEach(w -> w.getTasks().forEach(t -> {
                List<String> assignees = t.getAssignee().stream().map(Student::getUsername).toList();
                if (assignees.contains(identifier)) {
                    tasks.add(t);
                }
            }));
        } else {
            weeks.forEach(w -> tasks.addAll(w.getTasks()));
        }

        tasks.sort(Comparator.comparing(t -> t.getWeek().getWeekNumber()));

        tasks.forEach(task -> {
            String fileUrl = "http://localhost:1820/" + task.getId();
            ResponseEntity<List<Attachment>> fileResponse = restTemplate.exchange(
                    fileUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );
            List<Attachment> attachments = fileResponse.getBody();
            allTasksAttachmentMap.put(task.getId(), attachments);
        });

        Map<Integer, List<TaskData>> groupedTasks = new LinkedHashMap<>();
        tasks.forEach(task -> {
            List<String> assignees = task.getAssignee().stream().map(Student::getUsername).toList();
            TaskData taskData = new TaskData(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    assignees,
                    task.getWeek().getEndDate(),
                    task.getCompletedDate() != null ? task.getCompletedDate().toLocalDate() : null,
                    task.getStatus(),
                    task.getComments() != null ? task.getComments() : List.of(),
                    task.getRubrics() != null ? task.getRubrics() : List.of()
            );
            int weekNumber = task.getWeek().getWeekNumber();
            groupedTasks.computeIfAbsent(weekNumber, k -> new ArrayList<>()).add(taskData);
        });

        groupedTasks.forEach((weekNumber, taskDataList) -> {
            WeekData weekData = new WeekData(weekNumber, taskDataList);
            weekDataList.add(weekData);
        });

        this.allTasksAttachmentMap = allTasksAttachmentMap;
        this.group = group;
        this.members = members;
        this.reportType = reportType;
        this.identifier = identifier;

        return weekDataList;
    }

    private transient Map<String, List<Attachment>> allTasksAttachmentMap;
    private transient Group group;
    private transient List<Student> members;
    private transient String reportType;
    private transient String identifier;

    public ByteArrayInputStream createPdf(String username, List<WeekData> weekDataList) {
        log.info("Starting PDF generation for user: {}", username);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(12, 36, 72, 36);

            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterHandler(document));

            Header.add(document, reportType, identifier, group.getUniqueGroupId());
            PdfUtils.addProjectInfo(document, group, members);

            for (WeekData weekData : weekDataList) {
                PdfUtils.addWeekSection(document, weekData, allTasksAttachmentMap, reportType, identifier);
            }

            document.close();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Error generating PDF: ", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private HttpEntity<Void> getEntity(HttpServletRequest request) {
        String token = null;
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

    private Group getGroupDetails(String name, HttpEntity<Void> entity) {
        String url = BASE_URL + "/faculty/groups/gid/" + name;
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> map = response.getBody();
        if (map == null) throw new RuntimeException("Group not found");

        Group group = new Group();
        group.setId((String) map.get("id"));
        group.setUniqueGroupId((String) map.get("uniqueGroupId"));
        group.setGroupName((String) map.get("groupName"));
        group.setGroupDescription((String) map.get("groupDescription"));
        group.setBatchName((String) map.get("batchName"));
        group.setGroupLeader((String) map.get("groupLeader"));
        group.setProjectStatus((String) map.get("projectStatus"));
        group.setStartDate((String) map.get("startDate"));
        if (map.get("createdAt") instanceof String) {
            group.setCreatedAt(LocalDateTime.parse((String) map.get("createdAt")));
        }

        if (map.get("students") instanceof List) {
            List<?> studentsList = (List<?>) map.get("students");
            Set<Student> studentsSet = new HashSet<>();
            for (Object obj : studentsList) {
                if (obj instanceof Map) {
                    Map<String, Object> studentMap = (Map<String, Object>) obj;
                    Student student = new Student();
                    student.setId((String) studentMap.get("id"));
                    student.setUsername((String) studentMap.get("username"));
                    student.setStudentName((String) studentMap.get("studentName"));
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
                    Map<String, Object> techMap = (Map<String, Object>) techObj;
                    Technology technology = new Technology();
                    technology.setId((String) techMap.get("id"));
                    technology.setName((String) techMap.get("name"));
                    technologies.add(technology);
                }
            }
            group.setTechnologies(technologies);
        }

        if (map.get("batch") instanceof Map) {
            Map<String, Object> batchMap = (Map<String, Object>) map.get("batch");
            Batch batch = new Batch();
            batch.setId((String) batchMap.get("id"));
            batch.setBatchName((String) batchMap.get("batchName"));
            group.setBatch(batch);
        }

        if (map.get("mentor") instanceof Map) {
            Map<String, Object> mentorMap = (Map<String, Object>) map.get("mentor");
            Faculty mentor = new Faculty();
            mentor.setId((String) mentorMap.get("id"));
            mentor.setName((String) mentorMap.get("name"));
            group.setMentor(mentor);
        }

        if (map.get("weeks") instanceof List) {
            List<?> weeksList = (List<?>) map.get("weeks");
            List<Week> weeks = new ArrayList<>();
            for (Object weekObj : weeksList) {
                if (weekObj instanceof Map) {
                    Map<String, Object> weekMap = (Map<String, Object>) weekObj;
                    Week week = new Week();
                    week.setId((String) weekMap.get("id"));
                    week.setWeekNumber((Integer) weekMap.get("weekNumber"));
                    week.setEndDate(weekMap.get("endDate") instanceof String ? LocalDate.parse((String) weekMap.get("endDate")) : null);

                    if (weekMap.get("tasks") instanceof List) {
                        List<?> tasksList = (List<?>) weekMap.get("tasks");
                        List<Task> tasks = new ArrayList<>();
                        for (Object taskObj : tasksList) {
                            if (taskObj instanceof Map) {
                                Map<String, Object> taskMap = (Map<String, Object>) taskObj;
                                Task task = new Task();
                                task.setId((String) taskMap.get("id"));
                                task.setName((String) taskMap.get("name"));
                                task.setDescription((String) taskMap.get("description"));
                                task.setStatus((String) taskMap.get("status"));
                                if (taskMap.get("scoredMarks") instanceof Integer) {
                                    task.setScoredMarks((Integer) taskMap.get("scoredMarks"));
                                }
                                if (taskMap.get("createdDate") instanceof String) {
                                    task.setCreatedDate(LocalDate.parse((String) taskMap.get("createdDate")));
                                }
                                if (taskMap.get("submittedDate") instanceof String) {
                                    task.setSubmittedDate(LocalDateTime.parse((String) taskMap.get("submittedDate")));
                                }
                                if (taskMap.get("completedDate") instanceof String) {
                                    task.setCompletedDate(LocalDateTime.parse((String) taskMap.get("completedDate")));
                                }
                                if (taskMap.get("assignee") instanceof List) {
                                    List<?> assigneeList = (List<?>) taskMap.get("assignee");
                                    List<Student> students = new ArrayList<>();
                                    for (Object assigneeObj : assigneeList) {
                                        if (assigneeObj instanceof Map) {
                                            Map<String, Object> studentMap = (Map<String, Object>) assigneeObj;
                                            Student student = new Student();
                                            student.setId((String) studentMap.get("id"));
                                            student.setUsername((String) studentMap.get("username"));
                                            student.setStudentName((String) studentMap.get("studentName"));
                                            students.add(student);
                                        }
                                    }
                                    task.setAssignee(students);
                                }
                                if (taskMap.get("comments") instanceof List) {
                                    List<?> commentsList = (List<?>) taskMap.get("comments");
                                    List<com.studentOrbit.generate_report_app.entity.Comment.Comment> comments = new ArrayList<>();
                                    for (Object commentObj : commentsList) {
                                        if (commentObj instanceof Map) {
                                            Map<String, Object> commentMap = (Map<String, Object>) commentObj;
                                            com.studentOrbit.generate_report_app.entity.Comment.Comment comment = new com.studentOrbit.generate_report_app.entity.Comment.Comment();
                                            comment.setId((String) commentMap.get("id"));
                                            comment.setCommentDescription((String) commentMap.get("commentDescription"));
                                            comment.setTask(task);
                                            comments.add(comment);
                                        }
                                    }
                                    task.setComments(comments);
                                }
                                if (taskMap.get("rubrics") instanceof List) {
                                    List<?> rubricsList = (List<?>) taskMap.get("rubrics");
                                    List<Rubrics> rubrics = new ArrayList<>();
                                    for (Object rubricObj : rubricsList) {
                                        if (rubricObj instanceof Map) {
                                            Map<String, Object> rubricMap = (Map<String, Object>) rubricObj;
                                            Rubrics rubric = new Rubrics();
                                            rubric.setRubricName((String) rubricMap.get("rubricName"));
                                            if (rubricMap.get("rubricScore") instanceof Integer) {
                                                rubric.setRubricScore((Integer) rubricMap.get("rubricScore"));
                                            }
                                            rubric.setTask(task);
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
            weeks.sort(Comparator.comparing(Week::getWeekNumber));
            group.setWeeks(weeks);
        }

        return group;
    }
}