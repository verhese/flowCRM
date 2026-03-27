package wfm.engine;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import wfm.config.AbstractConfig;
import wfm.config.FlowTaskState;
import wfm.config.impl.DefaultConfig;
import wfm.exception.FlowEngineException;
import wfm.model.AbstractInstance;
import wfm.model.AbstractLog;
import wfm.model.AbstractTask;
import wfm.model.simple.SimpleLog;
import wfm.pojo.FlowEngineResult;
import wfm.pojo.FlowLogType;

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
     * Abstract method execute which needs to be implemented in the actual Engine implementation.
     * <br/>
     * This method should be seen as the start point of the task execution where the actual implementation of the task is called.
     *
     * @throws FlowEngineException which can be thrown by the task execution
     * @since 0.0.1
     */
    public abstract void execute() throws FlowEngineException;

    /**
     * Abstract method initialize which can be used to add extra logic to the initialization of the Engine.
     *
     * @since 0.0.1
     */
    public abstract void initialize();

    /**
     * Abstract method handleError which can be used to add specific logic to the logging process of te Engine.
     *
     * @since 0.0.1
     */
    public abstract void handleError();

    /**
     * Execute the engine
     *
     * @return Generic object containing all feedback from the task execution.
     */
    public final FlowEngineResult run() {
        FlowEngineResult result;

        try {
            addLog(FlowLogType.LOG,null,"Starting task processing.");
            getTask().setState(FlowTaskState.EXECUTING);

            execute();
        } catch (FlowEngineException e) {
            handleError();
            addLog(FlowLogType.ERR, instance.getService().getCode(), e.getLocalizedMessage());
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

        if(engineHasError()){
            getTask().setState(FlowTaskState.ERROR);

            // TODO perform rollback
        } else {
            getTask().setState(FlowTaskState.FINISHED);
        }

        this.getTask().setLogList(logList);

        return new FlowEngineResult();
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
                task.setState(config.getErrState());
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
            // TODO throw error
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
            // TODO throw error
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
            // TODO throw error
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
