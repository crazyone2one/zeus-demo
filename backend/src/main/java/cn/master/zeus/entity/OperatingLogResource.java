package cn.master.zeus.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
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
@Table(value = "operating_log_resource")
public class OperatingLogResource implements Serializable {

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * Operating log ID
     */
    private String operatingLogId;

    /**
     * operating source id
     */
    private String sourceId;

}
