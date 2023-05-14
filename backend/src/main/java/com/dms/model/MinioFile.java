package com.dms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.beans.factory.annotation.Value;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class MinioFile {
    @value("${spring.application.url}")
    @Transient
    private String applicationUrl;

    @Id
    private String id;
    private String title;
    private String filename;
    private Long size;
    private String url;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    public String getTitle() {
        return filename != null && filename.lastIndexOf('.') != -1 ? filename.substring(0, filename.lastIndexOf('.')) : filename;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return applicationUrl + "/download/" + id.replace("\"", "");
    }
}
