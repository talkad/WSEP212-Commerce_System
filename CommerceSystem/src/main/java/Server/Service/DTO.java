package Server.Service;

import java.util.List;

public class DTO {
    String type;
    String val;
    List<DTO> dto;

    public DTO(String type, String val, List<DTO> dto) {
        this.type = type;
        this.val = val;
        this.dto = dto;
    }


}
