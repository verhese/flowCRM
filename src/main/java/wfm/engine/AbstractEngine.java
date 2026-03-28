package wfm.engine;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import wfm.config.AbstractConfig;
import wfm.config.FlowTaskState;
import wfm.config.impl.DefaultConfig;
import wfm.exception.FlowEngineException;
import wfm.exception.FlowErrorCode;
import wfm.model.AbstractInstance;
import wfm.model.AbstractLog;
import wfm.model.AbstractTask;
import wfm.model.simple.SimpleLog;
import wfm.pojo.FlowEngineResult;
import wfm.pojo.FlowLogType;
import wfm.util.TransactionUtil;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract implementation of the wfm engine.
 * This is used as a base for further and custom implementations
 *
 * @author Sean Verheyen
 * @since 0.0.1
 */
public abstract class AbstractEngine {
    List<AbstractLog> logList = new LinkedList<>();
    private AbstractInstance instance;
    private AbstractTask task;
    private AbstractConfig config;

    /**
     * Default constructor for when the engine is executed for a task.
     *
     * @param instance The AbstractInstance for which the engine is executed.
     * @param task     The AbstractTask for which the engine is executed.
     * @since 0.0.1
     */
    protected AbstractEngine(AbstractInstance instance, AbstractTask task) {
        this.instance = instance;
        this.task = task;
        this.config = new DefaultConfig();

        initialize();
    }

    /**
     * Default constructor for when the engine is executed for an instance.
     *
     * @param instance instance  The AbstractInstance for which the engine is executed.
     * @since 0.0.1
     */
    protected AbstractEngine(AbstractInstance instance) {
        this.instance = instance;
        this.config = new DefaultConfig();

        initialize();
    }

    /**
     * Default empty constructor for when the engine is executed for an instance.
     *
     * @since 0.0.1
     */
    protected AbstractEngine() {
        this.config = new DefaultConfig();

        initialize();
    }

    /**
     * Validate method for the engine.
     * <br/>
     * Default implementation throws NO_VALIDATION_LOGIC and subclasses should override this.
     *
     * @throws FlowEngineException when no validation logic is provided
     */
    protected void validate() throws FlowEngineException {
        throw new FlowEngineException(FlowErrorCode.NO_VALIDATION_LOGIC);
    }

    /**
     * Execute method for the engine.
     * <br/>
     * Default implementation will raise a NO_LOGIC flow exception to force subclasses to override this method.
     *
     * @throws FlowEngineException when no execute logic is provided by subclass
     * @since 0.0.1
     */
    public void execute() throws FlowEngineException {
        throw new FlowEngineException(FlowErrorCode.NO_EXECUTION_LOGIC);
    }

    /**
     * Abstract method initialize which can be used to add extra logic to the initialization of the Engine.
     *
     * @since 0.0.1
     */
    public abstract void initialize();

    /**
     * Handle error callback for the engine.
     * <br/>
     * Default implementation throws NO_ERROR_LOGIC and subclasses should override this.
     *
     * @throws FlowEngineException when no error handling logic is provided
     * @since 0.0.1
     */
    public void handleError() throws FlowEngineException {
        throw new FlowEngineException(FlowErrorCode.NO_ERROR_LOGIC);
    }

    /**
     * Execute the engine
     *
     * @return Generic object containing all feedback from the task execution.
     */
    public final FlowEngineResult run() {
        FlowEngineResult result;

        beginTransaction();

        try {
            addLog(FlowLogType.LOG, null, "Starting task validation");
            updateTaskState(FlowTaskState.PENDING);
            validate();

            addLog(FlowLogType.LOG,null,"Starting task processing.");
            updateTaskState(FlowTaskState.EXECUTING);
            execute();
        } catch (FlowEngineException e) {
            FlowEngineException errorToLog = e;
            try {
                handleError();
            } catch (FlowEngineException handleErrorEx) {
                errorToLog = handleErrorEx;
            }
            addLog(FlowLogType.ERR, instance.getService().getCode(), errorToLog.getLocalizedMessage());
        } finally {
            result = stopTask();
        }

        return result;
    }

    /**
     * Stop the task processing.
     * This method will handle the writing of the error logs and any commits or rollbacks of data which might be needed.
     *
     * @return FlowEngineResult object containing the result, log lines and custom properties of the executed task.
     * @since 0.0.1
     */
    private FlowEngineResult stopTask(){
        try {
            if(engineHasError()){
                rollbackTransaction();
                updateTaskState(FlowTaskState.ERROR);
            } else {
                updateTaskState(FlowTaskState.FINISHED);
                commitTransaction();
            }
        } catch (Exception e) {
            // In case commit/rollback fails, ensure task is marked error and add a log entry.
            updateTaskState(FlowTaskState.ERROR);
            addLog(FlowLogType.ERR,
                   instance != null && instance.getService() != null ? instance.getService().getCode() : null,
                   "Transaction failure: " + e.getMessage());
            rollbackTransaction();
        } finally {
            this.getTask().setLogList(logList);
        }

        return new FlowEngineResult();
    }

    /**
     * Commit the transaction after successful task execution.
     * <br/>
     * Default implementation is a no-op; subclasses may override with actual DB transaction logic.
     */
    protected void commitTransaction() {
        TransactionUtil.commitTransaction(getEntityManager(), getJdbcConnection());
    }

    /**
     * Rollback the transaction after failed task execution.
     * <br/>
     * Default implementation is a no-op; subclasses may override with actual DB transaction logic.
     */
    protected void rollbackTransaction() {
        TransactionUtil.rollbackTransaction(getEntityManager(), getJdbcConnection());
    }

    /**
     * Begin a new transaction for this engine run.
     */
    protected void beginTransaction() {
        TransactionUtil.beginTransaction(getEntityManager(), getJdbcConnection());
    }

    /**
     * Provide an EntityManager for JPA-based transaction handling.
     * Default implementation returns null and can be overridden in concrete engines.
     */
    protected EntityManager getEntityManager() {
        return null;
    }

    /**
     * Provide a JDBC connection for transaction handling when JPA is not used.
     * Default implementation returns null and can be overridden in concrete engines.
     */
    protected Connection getJdbcConnection() {
        return null;
    }

    protected Long getPersistentTaskId() {
        return getTask() != null ? getTask().getId() : null;
    }

    protected void updateTaskState(FlowTaskState state) {
        if (getTask() != null) {
            getTask().setState(state);
        }
        TransactionUtil.updateTaskState(getEntityManager(), getJdbcConnection(), getPersistentTaskId(), state);
    }

    /**
     * Add logging to the executed instance and/or task.
     *
     * @param logType   The logging level which is to be applied.
     * @param reference A reference of the logging, to be used for easier filtering and accessing of relevant logging.
     * @param message   The actual log message.
     */
    protected void addLog(FlowLogType logType, String reference, String message) {
        if (logType != null && StringUtils.isNotBlank(message)) {
            SimpleLog log = new SimpleLog(instance, task, logType, reference, message);
            logList.add(log);

            // when the log line is an error, then we should update the status of the task to the configured error state.
            if (FlowLogType.ERR.equals(logType)) {
                updateTaskState(config.getErrState());
            }
        }
    }

    public List<AbstractLog> getLogList() {
        return logList;
    }

    public AbstractInstance getInstance() {
        return instance;
    }

    /**
     * Add a flow instance to the engine.
     *
     * @param instance AbstractInstance for which the engine will be executed.
     * @return AbstractEngine class according to the Builder Pattern.
     * @throws FlowEngineException Error which is thrown when the provided instance results to null.
     */
    protected AbstractEngine setInstance(AbstractInstance instance) throws FlowEngineException {
        if (instance != null) {
            this.instance = instance;
        } else {
            throw new FlowEngineException(FlowErrorCode.INVALID_INSTANCE);
        }
        return this;
    }

    public AbstractTask getTask() {
        return task;
    }

    /**
     * Add a flow task to the engine.
     * <br/>
     * If no instance is set yet, then the instance will be derived from the provided task.
     *
     * @param task AbstractTask for which the engine will be executed.
     * @return AbstractEngine class according to the Builder Pattern.
     * @throws FlowEngineException Error which is thrown when the provided task results to null.
     */
    protected AbstractEngine setTask(AbstractTask task) throws FlowEngineException {
        if (task != null) {
            this.task = task;
            if (this.instance == null) {
                setInstance(task.getInstance());
            }
        } else {
            throw new FlowEngineException(FlowErrorCode.INVALID_TASK);
        }
        return this;
    }

    public AbstractConfig getConfig() {
        return config;
    }

    /**
     * Add custom configuration to the engine.
     * <br/>
     * This configuration is optional. When no configuration is provided then the default configuration will be used.
     *
     * @param config Instance of AbstractConfig which is used to replace the standard engine configuration.
     * @return AbstractEngine class according to the Builder Pattern.
     * @throws FlowEngineException Error which is thrown when the provided config results to null.
     */
    protected AbstractEngine setConfig(AbstractConfig config) throws FlowEngineException {
        if (config != null) {
            this.config = config;
        } else {
            throw new FlowEngineException(FlowErrorCode.INVALID_CONFIG);
        }
        return this;
    }

    /**
     * Check whether an error occurred during the execution of the task.
     *
     * @return Boolean indicating that an error occured.
     */
    protected final boolean engineHasError(){
        boolean hasError = false;

        if(CollectionUtils.isNotEmpty(logList)){
            Optional<AbstractLog> logLine = logList.stream().filter(abstractLog -> FlowLogType.ERR.equals(abstractLog.getLogType())).findAny();
            hasError = logLine.isPresent();
        }

        return hasError;
    }
}
