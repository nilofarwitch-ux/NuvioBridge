# Nuvio Bridge

Phone-friendly first prototype.

Current features:
- QuickJS 1.0.14
- Local HTTP server on 127.0.0.1:8765
- GET /ping
- GET /js-test

`/js-test` executes JavaScript locally through QuickJS and returns the result.

Build:
- GitHub Actions uses Java 17 + Gradle 9.3.1.
