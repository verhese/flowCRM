package wfm.model;

import wfm.config.FlowTaskState;

import javax.persistence.*;
import java.util.List;

/**
 * Basic implementation of the Task Executable
 *
 * @author Sean Verheyen
 * @since 0.0.1
 */
@Entity
@Table(name = "flow_tasks")
public abstract class AbstractTask {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instance_id", referencedColumnName = "id")
    private AbstractInstance instance;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_code", referencedColumnName = "code")
    private AbstractService service;
    private FlowTaskState state;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<AbstractLog> logList = new java.util.ArrayList<>();
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<AbstractTaskStateLog> taskStateLogList = new java.util.ArrayList<>();

    /**
     * The default constructor for the creation of a task.
     * The state of the task is defaulted to FlowTaskState.REGISTERED.
     *
     * @param instance AbstractInstance implementation for which the task is created.
     * @see wfm.config.FlowTaskState
     * @since 0.0.1
     */
    protected AbstractTask(AbstractInstance instance) {
        this.instance = instance;
        this.service = instance.getService();
        this.state = FlowTaskState.REGISTERED;
    }

    /**
     * Getter for the id of the task.
     *
     * @return The unique identifier of the task.
     * @since 0.0.1
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for the instance of the task.
     *
     * @return The instance to which the task is linked.
     * @since 0.0.1
     */
    public AbstractInstance getInstance() {
        return instance;
    }

    /**
     * Getter for the service of the task.
     *
     * @return The service linked to the task.
     * @since 0.0.1
     */
    public AbstractService getService() {
        return service;
    }

    /**
     * Getter for the current state of the task.
     *
     * @return The current state of the task.
     * @since 0.0.1
     */
    public FlowTaskState getState() {
        return state;
    }

    /**
     * Setter for the state of the task.
     *
     * @param state
     * @since 0.0.1
     */
    public void setState(FlowTaskState state) {
        this.state = state;
    }

    /**
     * Retrieve the logs linked to the task.
     *
     * @since 0.0.1
     */
    public List<AbstractLog> getLogList() {
        return logList;
    }

    /**
     * Set the logs linked to the task
     *
     * @param logList
     * @since 0.0.1
     */
    public void setLogList(List<AbstractLog> logList){
        this.logList = logList;
    }

    /**
     * Retrieve the state change logs linked to the task.
     *
     * @return The state change logs linked to the task.
     * @since 0.0.1
     */
    public List<AbstractTaskStateLog> getTaskStateLogList() {
        return taskStateLogList;
    }

    /**
     * Set the state change logs linked to the task.
     *
     * @param taskStateLogList The state change logs linked to the task.
     * @since 0.0.1
     */
    public void setTaskStateLogList(List<AbstractTaskStateLog> taskStateLogList) {
        this.taskStateLogList = taskStateLogList;
    }
}
