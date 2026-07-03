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

## Authentication

PresentNow supports two authentication mechanisms:

- **Auth0 OIDC** — standard `Authorization: Bearer <token>` header, unchanged.
- **Magic-link email login** — a passwordless flow where the backend emails a one-time link. On verification the backend issues its own signed JWT, set as an `HttpOnly` cookie named `presentnow_session`. `smallrye-jwt` is configured with `mp.jwt.token.header=Cookie` for this flow, so it reads the session cookie without interfering with Auth0's bearer-token transport.

> **Note:** the two mechanisms are not linked. If the same person logs in once via Auth0 and once via magic link, they end up with two distinct user IDs — there is no account linking yet.

### Magic-Link Endpoints

All endpoints are under `/api/present-now/v1/public/auth`:

| Method | Path                  | Body            | Response                                                                                 |
| ------ | --------------------- | --------------- | ----------------------------------------------------------------------------------------- |
| POST   | `/magic-link`         | `{ "email" }`   | `202` always (even for unknown/rate-limited addresses); `400` on invalid email            |
| POST   | `/magic-link/verify`  | `{ "token" }`   | `204` + `Set-Cookie: presentnow_session` on success; `401` on invalid/expired/used token   |
| POST   | `/logout`             | —               | `204` + cleared session cookie                                                             |

Notes on behavior:

- `POST /magic-link` responds `202` regardless of whether the email is known, to avoid leaking account existence. Requests are silently rate-limited to 3 mails per 15 minutes per address.
- Magic-link tokens are single-use and expire after 15 minutes.
- The mailer is auto-mocked in `dev` and `test` profiles, so no real emails are sent locally.

### Configuration

Auth-related properties (prefix `com.github.presentnow.auth.*`):

| Property                                | Description                                      |
| ---------------------------------------- | ------------------------------------------------- |
| `com.github.presentnow.auth.issuer`                  | JWT issuer for backend-signed session tokens       |
| `com.github.presentnow.auth.cookie-name`             | Name of the session cookie (`presentnow_session`)  |
| `com.github.presentnow.auth.magic-link-expiry-minutes` | Magic-link token expiry, in minutes (default 15) |
| `com.github.presentnow.auth.session-expiry-days`     | Session cookie/JWT expiry, in days                 |
| `com.github.presentnow.auth.frontend-url`            | Frontend URL the magic link points to              |

JWT signing/verification keys:

- **Dev:** keys live under `src/main/resources/jwt-dev/`. **DEV ONLY — never use these keys in production.**
- **Test:** keys live under `src/test/resources/jwt/`.
- **Prod:** keys are not shipped in the repo. Set `SMALLRYE_JWT_SIGN_KEY_LOCATION` and `MP_JWT_VERIFY_PUBLICKEY_LOCATION` to point at the mounted secret PEM files.

SMTP configuration (required in prod; mailer is auto-mocked in dev/test):

| Variable        | Description       | Default |
| --------------- | ------------------ | ------- |
| `SMTP_HOST`     | SMTP server host    | —       |
| `SMTP_PORT`     | SMTP server port    | `587`   |
| `SMTP_USER`     | SMTP username       | —       |
| `SMTP_PASSWORD` | SMTP password       | —       |

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

## Persisted Data (DSGVO)

- `MagicLinkToken` table stores the email address and a SHA-256 hash of the token (never the raw token), plus creation/expiry timestamps. Expired rows are deleted opportunistically whenever a new magic-link request comes in.
- Users who sign in via magic link are represented in `WishList.username` as `email|<address>`.

## Frontend

A Vue.js frontend for PresentNow is developed and maintained in a separate repository.  
This backend is designed to work seamlessly with the Vue.js frontend.

- [PresentNow Frontend (Vue.js)](https://github.com/YOUR_ORG_OR_USERNAME/presentnow-frontend)  
  *(replace with actual link when available)*

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is [FOSS](https://en.wikipedia.org/wiki/Free_and_open-source_software). See [LICENSE](./LICENSE) for details.