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

- Java 17 or higher
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

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

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
**Java Version**: 17
**Build Tool**: Maven