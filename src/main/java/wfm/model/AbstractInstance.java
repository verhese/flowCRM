package wfm.model;

import javax.persistence.*;

/**
 * Basic implementation of what an instance should look like.
 *
 * @author Sean Verheyen
 * @since 0.0.1
 */
@Entity
@Table(name = "flow_instances")
public abstract class AbstractInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private AbstractService service;

    /**
     * Default constructor for creating a new instance.
     * At the moment this does not contain any parameters yes, but mandatory fields will be added.
     *
     * @param service The AbstractService for which the instance is made.
     * @since 0.0.1
     */
    protected AbstractInstance(AbstractService service) {
        this.service = service;
    }

    /**
     * Getter for the id of the instance.
     *
     * @return The unique identifier of the instance.
     * @since 0.0.1
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for the service linked to the instance.
     *
     * @return The service linked to the instance.
     * @since 0.0.1
     */
    public AbstractService getService() {
        return service;
    }
}
