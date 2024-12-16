package ongoing.backend.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleSecurityUser {
    private String id;
    private String username;
    private Integer bsnId;
    private List<String> roles;
    private boolean isShowLog = false;

}