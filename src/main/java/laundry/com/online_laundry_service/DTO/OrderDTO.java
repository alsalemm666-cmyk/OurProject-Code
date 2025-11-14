package laundry.com.online_laundry_service.DTO;

import lombok.Data;

@Data
public class OrderDTO {
    private Long userId;
    private Long serviceId;
    private String status;
}
