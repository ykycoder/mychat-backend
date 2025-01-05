package org.yky.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.yky.utils.LocalDateUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserVO implements Serializable {

    private String id;
    private String mychatNum;
    private String mychatNumImg;
    private String mobile;
    private String nickname;
    private String realName;
    private Integer sex;
    private String face;
    private String email;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = LocalDateUtils.DATE_PATTERN, timezone = LocalDateUtils.TIMEZONE_GMT8)
    private LocalDate birthday;

    private String country;
    private String province;
    private String city;
    private String district;
    private String chatBg;
    private String signature;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = LocalDateUtils.DATETIME_PATTERN, timezone = LocalDateUtils.TIMEZONE_GMT8)
    private LocalDateTime createdTime;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = LocalDateUtils.DATETIME_PATTERN, timezone = LocalDateUtils.TIMEZONE_GMT8)
    private LocalDateTime updatedTime;

    private String userToken;
}