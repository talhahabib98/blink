# Blink — URL Shortening Service

A production-grade URL shortening service built with Spring Boot 2.7 and
in-memory storage.

## Features

- Create short links with a randomly generated Base62 code, or supply your
  own custom alias
- 302 redirect from a short code to the original URL, with click tracking
- Configurable per-link expiry, with a scheduled background job that evicts
  expired links
- Request validation and consistent JSON error responses for every failure
  mode (not found, expired, duplicate alias, malformed URL, bad input)
- Per-IP fixed-window rate limiting
- Actuator health/info/metrics endpoints

Storage is entirely in-memory (`ConcurrentHashMap`), so data does not
survive a restart and the service is single-instance only. The repository
layer is defined behind an interface so a persistent backing store could be
swapped in later without touching the service or API layers.

## Running

```bash
mvn spring-boot:run
```

The service starts on `http://localhost:8080` by default.

## Configuration

All settings live under the `blink` prefix in
[`application.yml`](src/main/resources/application.yml):

| Property | Default | Description |
|---|---|---|
| `blink.base-url` | `http://localhost:8080` | Public base URL used when building short links in responses |
| `blink.short-code.length` | `7` | Length of generated short codes |
| `blink.default-expiry-days` | `365` | Default link lifetime when none is specified |
| `blink.cleanup.interval-ms` | `3600000` | How often the expired-link eviction job runs |
| `blink.rate-limit.enabled` | `true` | Toggle per-IP rate limiting |
| `blink.rate-limit.requests-per-minute` | `60` | Requests allowed per IP per minute |

## API

### Create a short URL

```
POST /api/v1/urls
Content-Type: application/json

{
  "originalUrl": "https://example.com/some/long/path",
  "customAlias": "my-link",   // optional
  "expiresInDays": 30          // optional, defaults to blink.default-expiry-days
}
```

Response `201 Created`:

```json
{
  "code": "my-link",
  "shortUrl": "http://localhost:8080/my-link",
  "originalUrl": "https://example.com/some/long/path",
  "createdAt": "2026-01-07T10:00:00Z",
  "expiresAt": "2026-02-06T10:00:00Z"
}
```

### Redirect

```
GET /{code}
```

Responds `302 Found` with a `Location` header pointing at the original URL,
and increments the link's click counter. Returns `404` for an unknown code
or `410 Gone` for an expired one.

### Stats

```
GET /api/v1/urls/{code}
```

Returns the link's metadata plus `clickCount` and `expired`.

### Delete

```
DELETE /api/v1/urls/{code}
```

Responds `204 No Content`.

### Errors

All error responses share one shape:

```json
{
  "timestamp": "2026-01-07T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "No short URL found for code 'abc123'",
  "details": []
}
```

## Testing

```bash
mvn test
```

Covers the code generator, the in-memory repository, service-layer business
rules (collision retry, validation, expiry, custom aliases), and the full
HTTP surface via `MockMvc`.
