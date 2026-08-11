# DEI Academic Management System - AcaMS

## Dependencies

- Require download
  - [Java 21](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  - [Maven](https://maven.apache.org/download.cgi)
  - [Node 14+](https://nodejs.org/en/) ([Node Version Manager](https://github.com/nvm-sh/nvm) recommended)
  - [Docker](https://www.docker.com/)
- No download required
  - [Spring-boot](https://spring.io/)
  - [Vue.js](https://vuejs.org/)


## Run Locally

Clone the project

```bash
git clone git@gitlab.rnl.tecnico.ulisboa.pt:<REPO>
```

Go to the project directory

```bash
cd src/
```

### Database

To run the database with Docker (recommended), run the following command:

```bash
docker compose up
```

Alternatively, you can create services that will be run in the background:

```bash
docker compose up -d
```

To stop the database, run the following command:

```bash
docker compose down
```

### Backend

Create a copy of the `application-local.properties` file.

```bash
cp ./backend/src/main/resources/application.properties.example ./backend/src/main/resources/application.properties
```

If you're running your database using Docker, the datasource variables should match the ones in `Docker-compose.yml`.

To build and run the backend, execute the following commands:

```bash
cd ./backend
mvn clean spring-boot:run
```

## Frontend

Create a copy of the `example.env` file named `.env`.

```bash
cp ./frontend/example.env ./frontend/.env
```

Now, you need to install the dependencies:

```bash
cd ./frontend
npm i
```

To run the frontend, run the following command:

```bash
npm run dev
```

## Access the Database

In order to access the database, you can use the following command (if you're using the provided Docker Compose file, `PORT` should be `7654`, `USER` should be `postgres` and `DB_NAME` should be `deidb`):

```bash
psql -h localhost -p <PORT> -U <USER> <DB_NAME>
```

## Features Implemented

### Authentication
- JWT-based login: `POST /auth/login` with email + password, returns a signed token
- Passwords hashed with BCrypt (`Person.password` never exposed via API — see `PersonDto` vs `CreatePersonDto` split)
- `JwtAuthFilter` validates the `Authorization: Bearer <token>` header on protected routes and populates Spring Security's context with the authenticated user's identity and role
- `POST /people` is currently open/unauthenticated to avoid a bootstrapping problem (can't create the first account if creation itself requires login); revisit once an initial Admin is seeded via `populate.sql`

### Courses & UCs
- Full CRUD for `Course` (`/courses`) and `Uc` (`/ucs`)
- A UC has one Regente (`MAIN_TEACHER`) and belongs to one or more Courses (many-to-many)
- Creating/updating a UC validates that the assigned regente is actually a `MAIN_TEACHER`
- Creating, updating, and deleting UCs is restricted to Administrators (`@PreAuthorize`); viewing is open to any authenticated user

### UC Membership
- Regente (or Admin) can add/remove Alunos and Assistentes for their UC (`/ucs/{ucId}/members`)
- Authorization is ownership-based: only the UC's actual Regente or an Administrator can manage its members, not just any teacher
- Duplicate memberships are rejected; viewing members is open to any authenticated user

### Evaluations (Tests & Projects)
- Tests (`/ucs/{ucId}/tests`) and Projects (`/ucs/{ucId}/projects`) are managed by the UC's Regente or an Administrator
- Projects can be individual or group-based (`isGroupProject`), with an enforced `maxGroupSize`
- Project groups (`/projects/{projectId}/groups`) can be created manually or auto-assigned randomly among the UC's enrolled students; auto-assignment is a one-time operation and will reject subsequent runs once groups already exist.

### Grading
- Grades can be assigned by the UC's Regente, its Assistentes, or an Administrator — a broader set of roles than evaluation creation, matching the spec's distinction between defining an evaluation and grading it
- A grade is tied to either a Test or a Project, and either an individual student or a project group, never both — enforced in `GradeService`
- Re-submitting a grade for the same test/person (or project/group) updates the existing grade rather than creating a duplicate
- Scores are validated to be within 0–20

### Grades View
- `GET /api/ucs/{ucId}/students/{personId}/grades` returns a student's grades in a UC plus a computed weighted average
- Average is computed only from evaluations that have been graded so far (ungraded evaluations are excluded rather than counted as zero) — weights re-normalize automatically as more grades are added through the semester
- Viewable by the student themselves, the UC's Regente or Assistentes, or an Administrator
- Test/Project creation enforces a weight budget: the sum of all weights in a UC cannot exceed 1.0 (with small floating-point tolerance), checked on both create and update

### Exam Review Workflow
- `POST /review-requests` — student submits a review request for a test grade, with justification and deadline
- `POST /review-requests/{id}/assistant-opinion` — Regente, Assistente, or Admin adds an opinion (optional step)
- `POST /review-requests/{id}/decide` — only the UC's Regente or an Admin can make the final approve/reject decision
- Full history is preserved on a single record (justification, opinion, decision) rather than a separate audit table — sufficient given each transition is timestamped and nothing is overwritten
- Grade changes resulting from an approved review are not automatic — the professor manually re-grades via the existing grading endpoint if the outcome warrants a score change, since the correct adjustment amount isn't derivable from the workflow itself

### Student Profile
- `GET /api/students/{studentId}/profile` — returns enrolled UCs with computed averages, plus pending (ungraded) tests and projects
- Visible only to the student themselves or an Administrator 
- "Projetos submetidos" (submitted project files) tracking was simplified out — the current model tracks grading completeness as a proxy for pending work, not actual file submission state, since no submission/file-upload entity was built
