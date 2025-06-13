# Coding Contest System

A web application of a coding contest control system

## How to run

The main requirement is to have Docker installed on your local machine.
if you don't have it installed, you can do it here: https://docs.docker.com/get-docker/

Next, clone repository to your local machine:

```bash
    git clone https://github.com/Shimady563/coding-contest-system.git
```

To build all services before running the application, you can use the following command:

Linux:

```bash
    ./build.sh
```

Windows:

```bash
    .\build.sh
```

If you want to run the application using prebuild images, you can use the following command:

```bash
    docker compose up -d
```

The application will be available at http://localhost:5173

You can view documentation at http://localhost:8080/api/v1/swagger-ui.html
and http://localhost:8081/api/v1/auth/swagger-ui.html

To stop the application run:

```bash
    docker compose down
```