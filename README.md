# flowCRM - Workflow Execution Engine

A robust workflow execution engine written in Java 17 that manages the lifecycle of workflow instances, tasks, and their state transitions through an abstract engine pattern.

## 🚀 Features

- **State-Driven Architecture**: Dual-layer state machine for workflow and task management
- **Extensible Engine Pattern**: Abstract engine for custom workflow implementations
- **Comprehensive Logging**: Built-in logging system with automatic state transitions
- **JPA Integration**: Ready for database persistence
- **Test Framework**: Complete test suite with mock implementations

## 🏗️ Architecture

### Core Components

The system follows a hierarchical structure:
- **Service**: Defines the workflow type
- **Instance**: Represents a workflow execution
- **Task**: Individual executable units within a workflow
- **Log**: Audit trail and execution history

### State Management

- **Instance States**: Track workflow lifecycle (CREATED → CLOSED)
- **Task States**: 10-state lifecycle from registration to completion

## 📋 Prerequisites

- Java 25 or higher
- Maven 3.6+
- Git

## 🛠️ Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/SVERHEYEN/flowCRM.git
   cd flowCRM
   ```

2. **Build the project:**
   ```bash
   mvn clean compile
   ```

3. **Run tests:**
   ```bash
   mvn test
   ```

## 📖 Usage

### Creating a Custom Engine

Extend `AbstractEngine` to implement your workflow logic:

```java
public class MyWorkflowEngine extends AbstractEngine {
    @Override
    public void execute() throws FlowEngineException {
        // Your business logic here
    }

    @Override
    public void initialize() {
        // Pre-execution setup
    }

    @Override
    public void handleError() {
        // Error handling logic
    }
}
```

### Basic Usage

```java
// Create entities
SimpleService service = new SimpleService("MY_SERVICE");
SimpleInstance instance = new SimpleInstance(service);
SimpleTask task = new SimpleTask(instance);

// Setup and run engine
MyWorkflowEngine engine = new MyWorkflowEngine(instance, task);
FlowEngineResult result = engine.run();
```
### Liquibase Database Setup

The default is an in-memory H2 setup (`liquibase.properties`). For local MySQL, use one of these approaches:

1. Activate Maven profile:

```bash
mvn -Pmysql test
```

2. Override via environment variables (secure/CI-friendly):

```bash
export LIQUIBASE_URL="jdbc:mysql://localhost:3306/flowCRM?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export LIQUIBASE_USERNAME="root"
export LIQUIBASE_PASSWORD="secret"
export LIQUIBASE_DRIVER="com.mysql.cj.jdbc.Driver"
mvn test
```

3. Override via `-D` system properties:

```bash
mvn test \
  -Dliquibase.url="jdbc:mysql://localhost:3306/flowCRM?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  -Dliquibase.username=root \
  -Dliquibase.password=secret \
  -Dliquibase.driver=com.mysql.cj.jdbc.Driver
```
## 🧪 Testing

Run the test suite:

```bash
mvn test
```

## 🌐 Localization Support

The engine now supports internationalized error messages via resource bundles under `src/main/resources`.

- Default locale: `en` (fallback for unknown/unsupported locales)
- Supported locales:
  - `en` (`messages_en.properties` or `messages.properties`)
  - `en-GB` (`messages_en_GB.properties`)
  - `nl` (`messages_nl.properties`)
  - `nl-BE` (`messages_nl_BE.properties`)
  - `nl-NL` (`messages_nl_NL.properties`)

Framework utilities:

- `wfm.util.LocaleUtils` for locale normalization and fallback
- `wfm.util.MessageResolver` for key-based message lookup with pluralization/formatting support

Error codes are managed in `wfm.exception.FlowErrorCode`; exception wrappers are centralized in `wfm.exception.FlowEngineException`.

## 📁 Project Structure

```
flowCRM/
├── src/main/java/wfm/
│   ├── config/          # Configuration classes and state enums
│   ├── engine/          # AbstractEngine and implementations
│   ├── exception/       # Custom exceptions
│   ├── model/           # Entity classes and simple implementations
│   └── pojo/            # Data transfer objects
├── src/test/java/       # Unit tests
├── .github/             # AI coding agent instructions
├── javadoc/             # Generated documentation
└── pom.xml
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📚 Documentation

- **API Docs**: Generated Javadoc in `./javadoc/` directory

## 📄 License

This project is licensed under the terms specified in the LICENSE file.

---

**Disclaimer**: This documentation was created with the assistance of GitHub Copilot.

**Version**: 0.1
**Java Version**: 25
**Build Tool**: Maven