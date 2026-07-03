# Frontend Implementation Guide: Magic-Link Email Login

Audience: both PresentNow Vue.js frontends. The backend (issue #19, commit `63bc891`) now
supports two independent auth mechanisms:

| Mechanism | Transport | Who manages it |
|---|---|---|
| Auth0 OIDC (existing) | `Authorization: Bearer <token>` header | Auth0 SDK in the frontend |
| Magic-link session (new) | HttpOnly cookie `presentnow_session` | Browser, automatically |

Both work against the same protected API (`/api/present-now/v1/*`). Nothing changes for the
existing Auth0 login path.

## Endpoints

Base path: `/api/present-now/v1/public/auth` (public, no auth required).

### 1. Request a magic link

```
POST /public/auth/magic-link
Content-Type: application/json

{ "email": "user@example.com" }
```

Responses:
- `202 Accepted` — always, even when the address is rate-limited (max 3 mails per address
  per 15 minutes). Never reveals whether a mail was actually sent.
- `400 Bad Request` — syntactically invalid email.

### 2. Verify the token

The email contains a link to the **frontend**: `<frontend-url>/auth/verify?token=<token>`.
The frontend route reads the token and exchanges it:

```
POST /public/auth/magic-link/verify
Content-Type: application/json

{ "token": "<token from URL query>" }
```

Responses:
- `204 No Content` — success. `Set-Cookie: presentnow_session=<JWT>; HttpOnly; Secure;
  SameSite=Lax; Path=/; Max-Age=604800` (7 days).
- `401 Unauthorized` — token invalid, expired (15 min) or already used (single-use).

### 3. Logout

```
POST /public/auth/logout
```

- `204 No Content` — clears the cookie (`Max-Age=0`).

## What to build in each frontend

### Login view

Add an email form next to the existing Auth0 login button:

```ts
async function requestMagicLink(email: string): Promise<void> {
  const res = await fetch(`${apiBase}/public/auth/magic-link`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email }),
  })
  if (res.status === 400) throw new Error('invalid-email')
  // 202: show "check your inbox" — always, do not distinguish rate limiting
}
```

UX: after submit always switch to a "Check your inbox" state. Do not indicate whether the
address is known or rate-limited (deliberate anti-enumeration behavior of the backend).

### `/auth/verify` route

New Vue Router route that the email link lands on:

```ts
// router
{ path: '/auth/verify', component: VerifyView }
```

```ts
// VerifyView setup
const token = new URLSearchParams(window.location.search).get('token')

async function verify(): Promise<void> {
  const res = await fetch(`${apiBase}/public/auth/magic-link/verify`, {
    method: 'POST',
    credentials: 'include', // REQUIRED so the Set-Cookie is stored
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token }),
  })
  if (res.status === 204) {
    router.push('/') // logged in
  } else {
    // 401: expired or already used — show error with a "request new link" action
  }
}
```

Call `verify()` on an explicit button click ("Log me in") rather than automatically in
`onMounted`. Mail scanners prefetch links; the backend link points at the frontend precisely
so that a prefetch does not burn the single-use token — an auto-POST on page load would
reintroduce that problem for aggressive scanners that execute JS.

### API client changes

The session cookie is only sent if every API call opts into credentials:

```ts
// fetch
fetch(url, { credentials: 'include', ... })

// axios
axios.defaults.withCredentials = true
```

Important: do **not** send an `Authorization` header with a stale/empty Auth0 token when the
user logged in via magic link. If the axios/fetch interceptor currently always attaches
`Authorization`, guard it:

```ts
if (auth0Token) config.headers.Authorization = `Bearer ${auth0Token}`
// otherwise send nothing — the cookie authenticates the request
```

(An `Authorization: Bearer` header routes the request into the OIDC mechanism, and an invalid
value there yields 401 even though the cookie is valid.)

### Detecting login state

The cookie is `HttpOnly` — JavaScript cannot read it. Detect the session by probing:

```ts
const res = await fetch(`${apiBase}/lists`, { credentials: 'include' })
const loggedIn = res.status === 200
```

Suggested Pinia shape: `authStore.mechanism: 'auth0' | 'magic-link' | null`, set to
`'magic-link'` after a successful verify, cleared on logout/401. On app start, if the Auth0 SDK
reports no session, run the probe before treating the user as logged out.

Display name / user id: magic-link users get `sub = email|<address>` and
`name = <local part>` (e.g. `frank` for `frank@example.com`). Wishlists created via the two
mechanisms belong to **different users** — no account linking yet.

### Logout

```ts
await fetch(`${apiBase}/public/auth/logout`, { method: 'POST', credentials: 'include' })
authStore.$reset()
```

If the user is logged in via Auth0, keep using the Auth0 SDK logout instead; call this
endpoint only for magic-link sessions (calling both is harmless).

### Handling session expiry

The session JWT lasts 7 days and cannot be refreshed. On any `401` from a protected endpoint,
reset the auth store and route to the login view.

## Dev setup (important)

The cookie is `Secure` + `SameSite=Lax`. Cross-origin XHR from `localhost:5173` to
`localhost:8080` will **not** carry the cookie under `SameSite=Lax`, and the backend dev CORS
config (`origins=*`) does not allow credentials.

**Recommended: use the Vite dev proxy so API calls are same-origin.**

```ts
// vite.config.ts
server: {
  proxy: {
    '/api/present-now': { target: 'http://localhost:8080', changeOrigin: true },
  },
},
```

Then `apiBase = '/api/present-now/v1'` in dev, cookies flow with no CORS/SameSite issues
(browsers accept `Secure` cookies on `localhost`). The backend dev profile assumes the mail
link targets `http://localhost:5173` (`%dev.com.github.presentnow.auth.frontend-url`).

In dev mode no real mail is sent — the Quarkus mailer is mocked. Read the generated login
link in the Quarkus Dev UI (Mailer → Mock Mailbox) or the backend log.

## Test checklist per frontend

- [ ] Email form submits, always shows "check your inbox" (also for repeated submits)
- [ ] Invalid email shows validation error (400)
- [ ] `/auth/verify?token=...` route exchanges token, redirects on success
- [ ] Expired/used token shows error + "request new link"
- [ ] Protected views load using only the cookie (no Authorization header)
- [ ] Auth0 login still works unchanged
- [ ] Logout clears the session (subsequent probe returns 401)
