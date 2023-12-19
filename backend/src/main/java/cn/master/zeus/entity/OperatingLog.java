package cn.master.zeus.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "operating_log")
public class OperatingLog implements Serializable {

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * Project ID
     */
    private String projectId;

    /**
     * operating method
     */
    private String operateMethod;

    /**
     * source create u
     */
    private String createUser;

    /**
     * operating user id
     */
    private String operateUser;

    /**
     * operating source id
     */
    private String sourceId;

    /**
     * operating type
     */
    private String operateType;

    /**
     * operating module
     */
    private String operateModule;

    /**
     * operating title
     */
    private String operateTitle;

    /**
     * operating path
     */
    private String operatePath;

    /**
     * operating content
     */
    private String operateContent;

    /**
     * operating params
     */
    private String operateParams;

    /**
     * Update timestamp
     */
    private LocalDateTime operateTime;
    private Boolean delFlag;
}
