package wfm.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import wfm.pojo.FlowLogType;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Base implementation of the logging
 *
 * @author Sean Verheyen
 * @since 0.0.1
 */
@Entity
@Table(name = "flow_logs")
public abstract class AbstractLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;
    @Column(nullable = false)
    private final Long sequence = null;
    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private AbstractTask task;
    @JoinColumn(name = "instance_id", referencedColumnName = "id", nullable = false)
    private AbstractInstance instance;
    @Column(nullable = false, name = "log_type")
    private FlowLogType logType;
    @Column(nullable = false, length = 35)
    private String reference;
    @Column(nullable = false, length = 2000)
    private String message;
    @Column(nullable = false)
    private Timestamp timestamp;

    /**
     * Default constructor for when adding logging to a specific task.
     *
     * @param instance  The AbstractInstance which is linked to the logging.
     * @param task      The AbstractTask which is linked to the logging.
     * @param logType   The actual log level.
     * @param reference A reference of the logging, to be used for easier filtering and accessing of relevant logging.
     * @param message   The actual message which is logged.
     * @since 0.0.1
     */
    protected AbstractLog(AbstractInstance instance, AbstractTask task, FlowLogType logType, String reference, String message) {
        this.instance = instance;
        this.task = task;
        this.logType = logType;
        this.reference = reference;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Default constructor for when adding logging to an instance itself, without linking it to a task.
     *
     * @param instance  The AbstractInstance which is linked to the logging.
     * @param logType   The actual log level.
     * @param reference A reference of the logging, to be used for easier filtering and accessing of relevant logging.* @param message The actual message which is logged.
     * @param message   The actual message which is logged.
     * @since 0.0.1
     */
    protected AbstractLog(AbstractInstance instance, FlowLogType logType, String reference, String message) {
        this.instance = instance;
        this.logType = logType;
        this.reference = reference;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Getter for the id of the log.
     *
     * @return The unique identifier of the log.
     * @since 0.0.1
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for the sequence of the log.
     *
     * @return The sequence of the log.
     * @since 0.0.1
     */
    public Long getSequence() {
        return sequence;
    }

    /**
     * Getter for the task of the log.
     *
     * @return The identifier of the task linked to the log.
     * @since 0.0.1
     */
    public AbstractTask getTask() {
        return task;
    }

    /**
     * Getter for the instance of the log.
     *
     * @return The identifier of the instance linked to the log.
     * @since 0.0.1
     */
    public AbstractInstance getInstance() {
        return instance;
    }

    /**
     * Getter for the type of the log.
     *
     * @return The type of the log.
     * @since 0.0.1
     */
    public FlowLogType getLogType() {
        return logType;
    }

    /**
     * Getter for the reference of the log.
     *
     * @return The reference of the log.
     * @since 0.0.1
     */
    public String getReference() {
        return reference;
    }

    /**
     * Getter for the message of the log.
     *
     * @return The message of the log.
     * @since 0.0.1
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the timestamp of the log.
     *
     * @return The timestamp of the log.
     * @since 0.0.1
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AbstractLog that = (AbstractLog) o;

        return new EqualsBuilder().append(logType, that.logType).append(reference, that.reference).append(message, that.message).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(logType).append(reference).append(message).toHashCode();
    }
}
