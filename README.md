# PresentNow Backend

**PresentNow** is an open-source backend REST API (built with Java + Quarkus) for a web application that helps people collaboratively organize and manage gift-giving for occasions like birthdays.

## Project Idea

Finding the right present for someone can be challenging. PresentNow aims to make gift-giving easier for everyone involved:

- **Person A (Recipient)** creates or shares a wish list of presents.
- **Friends of A** can view the list, choose what they want to buy, and reserve presents to avoid duplicates.
- Friends can see who is buying which present, but Person A cannot see who is buying what—preserving the surprise!

## Features

- REST API built on [Quarkus](https://quarkus.io/) (Java)
- User and present management
- Present suggestion and reservation system
- Privacy: Only friends see who is buying which present; the recipient cannot
- Designed for easy integration with frontend clients

## Tech Stack

- **Backend:** Java 17+, Quarkus
- **Frontend:** [Vue.js](https://vuejs.org/) (in a separate repository)
- **Database:** Postgres
- **API:** RESTful endpoints

## Getting Started

### Prerequisites

- Java 17+ installed
- [Maven](https://maven.apache.org/) installed

### Running Locally

1. Clone the repository:
    ```bash
    git clone https://github.com/vvilip/presentnow-backend.git
    cd presentnow-backend
    ```
2. Start the Quarkus development server:
    ```bash
    ./mvnw quarkus:dev
    ```
3. The API will be available at `http://localhost:8080`.

### API Documentation

- (Add OpenAPI/Swagger details here if available)

## Frontend

A Vue.js frontend for PresentNow is developed and maintained in a separate repository.  
This backend is designed to work seamlessly with the Vue.js frontend.

- [PresentNow Frontend (Vue.js)](https://github.com/YOUR_ORG_OR_USERNAME/presentnow-frontend)  
  *(replace with actual link when available)*

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is [FOSS](https://en.wikipedia.org/wiki/Free_and_open-source_software). See [LICENSE](./LICENSE) for details.