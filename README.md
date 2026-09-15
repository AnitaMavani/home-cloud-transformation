# Home Cloud Transformation

A graduate capstone prototype for storing and managing files on local hardware, with the goal of repurposing idle computers as home storage servers.

Built by a team of four, the application provides a browser-based interface for account registration, login, and file uploads. Its upload workflow splits files into smaller chunks, displays progress, and supports pausing and resuming transfers within the active application session.

**Status:** Academic prototype demonstrated locally. It is not a production-hosted service or a complete replacement for Google Drive.

## Project purpose

Many households have older computers that are no longer used regularly but still have usable storage. This project explores using that hardware for personal file storage through a familiar web interface.

The demonstrated application stores uploaded files on local disk. Repurposing idle computers is the intended use case; the capstone demonstration ran in a local development environment.

## Features

### Account registration and login

- Registration and login interfaces built with Vue.js.
- Input validation and feedback for authentication flows.
- Spring Boot endpoints supporting account registration and login.

### Chunked file uploads

- Client-side splitting of files into **512 KiB chunks**.
- Sequential upload of chunks to the backend.
- Upload progress and status feedback.
- Pause and resume controls within the active session.
- Backend receipt and reassembly of uploaded chunks.
- File metadata persisted in MySQL and uploaded content stored on local disk.

The upload workflow was manually checked with different file types, including documents and media. No verified maximum file size or performance benchmark is claimed.

## Technology stack

| Layer | Technologies |
| --- | --- |
| Frontend | Vue.js, JavaScript, HTML, CSS |
| Backend | Java, Spring Boot |
| Data access | MyBatis |
| Database | MySQL |
| Supporting service | Redis |
| Backend build | Maven |
| File storage | Local filesystem |

The backend Maven configuration declares Spring Boot 2.6.1 and a Java 8 target. Use the dependency manifests as the reference for the versions in this repository.

## Upload workflow

1. The user selects a file through the Vue.js interface.
2. The frontend reads the file in chunks and computes a file hash.
3. Each chunk is submitted with its index and upload metadata to the backend.
4. The interface updates progress and allows the user to pause or resume the transfer.
5. The backend reassembles the received chunks and stores file metadata in MySQL.

Chunking enables incremental transfer and progress control. The project does not claim a measured speed improvement, parallel chunk uploads, or recovery after a browser restart.

## Repository structure

```text
home-cloud-transformation/
├── homecloud-backend/
│   ├── pom.xml             # Maven dependencies and build configuration
│   └── src/main/           # Spring Boot application and resources
├── homecloud-front/
│   ├── package.json        # Frontend dependencies and scripts
│   └── src/
│       └── components/
│           ├── Login.vue   # Login and registration interface
│           ├── Uploader.vue
│           ├── Main.vue
│           ├── Table.vue
│           └── Request.js
└── README.md
```

## Local setup

The project requires a Java development environment, Maven, Node.js/npm, MySQL, and Redis. It also requires a writable local directory for uploaded files.

1. Clone the repository:

   ```bash
   git clone https://github.com/AnitaMavani/home-cloud-transformation.git
   cd home-cloud-transformation
   ```

2. Review `homecloud-backend/pom.xml` and the backend application configuration under `homecloud-backend/src/main/`. Configure local database access, Redis connectivity, and file-storage paths for your environment.
3. Prepare the required MySQL schema using the project's available schema or initialization resources. Start MySQL and Redis before starting the backend.
4. Start the Spring Boot application through your Java IDE or the Maven configuration.
5. Install frontend dependencies:

   ```bash
   cd homecloud-front
   npm install
   ```

6. Review the scripts in `package.json` and run the configured development script. Check that frontend API routing points to your local backend.

**Setup documentation is incomplete:** exact database initialization steps and environment-specific configuration still need to be documented and verified from a clean checkout. The steps above are an orientation guide, not a verified one-command setup.

## My contribution

As part of the four-person team, **Anita Mavani** owned the file-upload and login/registration features across the frontend and backend:

- Built Vue.js interfaces and integrated them with backend APIs.
- Implemented Spring Boot registration, login, and file-upload endpoints.
- Implemented client-side file chunking, upload progress, and pause/resume controls.
- Implemented backend chunk receipt, reassembly, and file-metadata persistence in MySQL.
- Manually tested authentication and upload workflows.

These contributions describe individual ownership within a team project; they do not imply sole authorship of the entire application.

## Testing

Manual verification included:

- Registration and login with valid and invalid inputs.
- Uploads involving different file types and sizes.
- Upload progress and pause/resume behavior.
- Correct reconstruction of uploaded files.
- Stored files and associated database records.

No automated test coverage, maximum tested file size, or quantified performance improvement is claimed here.

## Scope and limitations

- Demonstrated locally as an academic prototype.
- Google Drive was a conceptual reference for the user experience, not a claim of feature parity.
- The local demonstration does not establish distributed storage across multiple computers or offline operation of every feature.
- Production deployment, security hardening, backup and recovery, and broader reliability testing would require additional work and validation.

## Academic context

**CSCI 6806 — Computer Science Graduate Capstone Project**  
Master of Science in Applied Computer Science  
Fairleigh Dickinson University
