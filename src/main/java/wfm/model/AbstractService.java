package wfm.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Basic implementation of the service level.
 *
 * @author Sean Verheyen
 * @since 0.0.1
 */
@Entity
@Table(name = "flow_services")
public abstract class AbstractService {
    @Id
    private String code;

    private AbstractService() {
    }

    /**
     * Standard constructor for creating a service implementation.
     *
     * @param code The unique code which is used to identify the service.
     */
    protected AbstractService(String code) {
        this.code = code;
    }

    /**
     * Getter for the code of the service.
     *
     * @return The unique code of the service.
     * @since 0.0.1
     */
    public String getCode() {
        return code;
    }
}
