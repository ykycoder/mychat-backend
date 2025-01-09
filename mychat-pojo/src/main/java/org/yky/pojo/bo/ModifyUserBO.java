package org.yky.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ModifyUserBO {

    private String userId;

    private String face;
    private Integer sex;
    private String nickname;
    private String mychatNum;

    private String province;
    private String city;
    private String district;
    private String chatBg;
    private String signature;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Email
    private String email;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDate startWorkDate;

}
