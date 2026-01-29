package com.example.fbk_balkan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {
    private Long id;

    @NotBlank(message = "Titel krävs")
    @Size(max = 255, message = "Titeln får inte vara längre än 255 tecken")
    private String title;

    @NotBlank(message = "Innehåll krävs")
    private String content;

    private String imageUrl;

    private String linkUrl;

    private boolean published = true;
}
