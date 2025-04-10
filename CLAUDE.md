# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands
- Build: `./mvnw clean install`
- Run: `./mvnw exec:java -Dexec.mainClass="com.ggoncalves.easycertcreator.main.EasyCertCreatorMain"`
- Run single test: `./mvnw test -Dtest=CertificateFileValidatorTest`
- Run test class with specific method: `./mvnw test -Dtest=CertificateFileValidatorTest#shouldValidateFileSuccessfully`

## Code Style Guidelines
- **Java Version**: Java 17
- **Naming**: CamelCase for methods/variables, PascalCase for classes
- **DI Framework**: Dagger 2 with @Inject annotations
- **Testing**: JUnit 5 with Mockito, use DisplayName annotations for clarity
- **Exception Handling**: Use custom exceptions in core/exception package, handle with ExceptionHandler
- **Formatting**: 2-space indentation, line length ≤ 100 characters
- **Imports**: Organize imports alphabetically, avoid wildcard imports
- **Logging**: Use SLF4J with Log4j2 implementation
- **Error Handling**: Delegate to ExceptionHandler for centralized handling