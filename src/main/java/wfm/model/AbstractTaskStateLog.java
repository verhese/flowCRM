package wfm.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Base implementation of a task state change log.
 */
@Entity
@Table(name = "flow_task_state_logs")
public abstract class AbstractTaskStateLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    private AbstractTask task;
    @Column(name = "new_state", nullable = false, length = 50)
    private String newState;
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
    @Column(name = "updated_by", nullable = false, length = 255)
    private String updatedBy;

    protected AbstractTaskStateLog() {
        // JPA constructor
    }

    protected AbstractTaskStateLog(AbstractTask task, String newState, Timestamp updatedAt, String updatedBy) {
        this.task = task;
        this.newState = newState;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public Long getId() {
        return id;
    }

    public AbstractTask getTask() {
        return task;
    }

    public String getNewState() {
        return newState;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
