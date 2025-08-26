# Repository Guidelines

## Project Structure & Modules
- `src/`: Java sources (`com.github.miachm.sods.*`).
- `tests/`: Test sources (TestNG + Cucumber step defs).
- `resources/`: Test resources and Gherkin features (`resources/features/**`).
- `examples/`: Small runnable examples (e.g., `BasicUsage.java`).
- `docs/`: Published Javadoc site.
- `target/`: Maven build outputs.

## Build, Test, and Dev Commands
- Build: `mvn package`
  - Compiles sources and runs tests; outputs JARs in `target/`.
- Test only: `mvn test`
  - Executes TestNG + Cucumber features under `resources/features` with steps in `tests/`.
- Fast build (skip tests): `mvn -DskipTests package`
- Run specific tests: `mvn -Dtest=RunCucumberTest test`

## Coding Style & Naming
- Java 8 source/target. Avoid APIs beyond Java 8.
- Indentation: 4 spaces; braces K&R style consistent with existing files.
- Naming: classes `PascalCase`, methods/fields `camelCase`, constants `UPPER_SNAKE_CASE`, packages lowercase.
- Public API stability: keep backward compatibility; prefer additive changes.
- Javadoc for public types/methods; keep examples concise.
- Dependencies: keep minimal; this library favors lightweight design.

## Testing Guidelines
- Frameworks: TestNG + Cucumber.
- Feature files live in `resources/features/**`; step definitions in `tests/steps/**`.
- Add tests alongside changes; prefer behavior expressed in Gherkin scenarios.
- Data files for tests go under `resources/` and are loaded via classpath.
- Coverage: aim to cover new branches/edge cases (fail-fast behavior, exceptions).

## Commit & Pull Requests
- Messages: imperative, concise, scoped (e.g., "Add example about writing headers").
- Reference issues when relevant: `Fix #123` or `Refs #123`.
- PRs: include description of motivation, approach, and impact on public API; link issues; add/adjust tests; update `examples/` if behavior is user-visible.
- Screenshots not required; attach small sample ODS files if helpful.

## Security & Configuration Tips
- Do not embed secrets in tests/resources.
- Large test artifacts: keep small; compress or generate at runtime if possible.
- Ensure changes work on CI (`mvn -B package`).

