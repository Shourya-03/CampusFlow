# CampusFlow

CampusFlow is a smart college workflow and career management platform built on top of a traditional Student Management System.

It brings academic management, student productivity, skill tracking, and placement preparation into one application. Students can manage tasks, monitor attendance, track skills, check placement readiness, and discover internships, hackathons, contests, and other opportunities based on their profile.

## Features

### Student Dashboard
- Attendance percentage
- CGPA
- Placement readiness score
- Prioritized tasks
- Upcoming deadlines
- Skill overview

### Smart Task Management
Students can create tasks with:
- Deadline
- Importance
- Difficulty
- Estimated effort
- Completion status

Each task receives a priority score based on deadline urgency, importance, difficulty, and estimated effort.

### Attendance Prediction
The system can calculate:
- Number of classes needed to reach a target attendance percentage
- Number of classes that can still be missed while staying above the target

### Skill Tracking
Students can track proficiency in skills such as:
- Java
- Spring Boot
- SQL
- DSA
- Python
- Git
- Docker
- System Design
- Communication
- Aptitude

### Placement Readiness
A weighted score is generated from the student's skill profile. The system also highlights weaker areas that need improvement.

### Opportunity Matching
Admins and teachers can add opportunities such as:
- Internships
- Hackathons
- Coding contests
- Workshops
- Placement drives
- Scholarships

Students can see their match percentage for an opportunity based on their skills, along with basic eligibility conditions such as CGPA and year.

### Skill Gap Analysis
Students can select a target role and compare required skills with their current proficiency to identify skill gaps.

### Analytics
The analytics dashboard provides visual summaries for:
- Task completion
- Weekly productivity
- Attendance trends
- Skill distribution

### Role-Based Access
The application supports three roles:

- **ADMIN** - manages students, opportunities, and system data
- **TEACHER** - manages relevant academic and opportunity information
- **STUDENT** - manages personal tasks and skills and views recommendations

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.3
- Spring MVC
- Spring Data JPA
- Spring Security
- Maven

### Frontend
- Thymeleaf
- HTML
- CSS
- JavaScript
- Bootstrap
- Chart.js

### Database
- MySQL

### Security
- Session-based authentication
- BCrypt password hashing
- Role-based authorization

## Architecture

CampusFlow follows a simple MVC structure:

```text
Browser
   |
   v
Thymeleaf Views
   |
   v
Controllers
   |
   v
Services
   |
   v
Repositories
   |
   v
MySQL
```

Business logic is kept mainly in the service layer, while controllers handle requests and repositories handle database access.

## Core Logic

### Task Priority Score

Tasks are ranked using a weighted score based on:

```text
Deadline Urgency
Importance
Difficulty
Estimated Effort
```

The score is stored with the task and used to display higher-priority work first.

### Attendance Prediction

Attendance prediction uses the relationship between attended classes, total classes, future classes, and a target percentage to determine how many classes are required or can be missed.

### Placement Readiness

Student skill proficiencies are combined using category-based weights to calculate an overall placement readiness score.

### Opportunity Matching

The matching system checks basic eligibility and compares the student's skill proficiency with the skills required by an opportunity to produce a match percentage and a skill-by-skill breakdown.

## Database

The project uses MySQL. The existing Student Management System data is retained and extended with CampusFlow-specific entities.

Main entities include:

```text
User
Student
Attendance
Marks
Fee
Notice
Timetable

Task
Skill
StudentSkill
Opportunity
OpportunitySkill
```

## Project Structure

```text
src/
├── main/
│   ├── java/com/sms/
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── security/
│   │
│   └── resources/
│       ├── templates/
│       ├── static/
│       └── application.properties
│
└── test/
```

## Getting Started

### Prerequisites

Make sure the following are installed:

- Java 17
- Maven
- MySQL
- Git

### Clone the Repository

```bash
git clone https://github.com/your-username/campusflow.git
cd campusflow
```

### Create the Database

```sql
CREATE DATABASE studentdb;
```

### Configure Database Connection

Update `src/main/resources/application.properties` with your MySQL credentials.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/studentdb
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Run the Application

```bash
mvn spring-boot:run
```

Or build the project first:

```bash
mvn clean package
```

## Sample Data

The application can initialize sample data for:

- Skills
- Student skill levels
- Tasks
- Opportunities
- Opportunity skill requirements

This makes it easier to test the main CampusFlow features after setup.

## Future Improvements

Possible future additions include:

- Deadline reminders
- More detailed opportunity recommendations
- Resume analysis
- Placement drive management
- Exportable analytics
- Expanded role-based skill roadmaps
- Cloud deployment

## Why CampusFlow?

Traditional college management systems mainly focus on storing academic information. CampusFlow adds a practical layer on top of that foundation by helping students organize their work, understand their current skill level, and discover relevant career opportunities.

The project focuses on understandable business logic and clean Spring Boot MVC development rather than unnecessary architectural complexity.

## Author

**Shourya**

B.Tech Computer Science & Engineering

## License

This project is created for educational and academic purposes.
