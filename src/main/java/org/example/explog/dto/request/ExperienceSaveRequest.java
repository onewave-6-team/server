package org.example.explog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ExperienceSaveRequest(
        @NotBlank(message = "내용을 입력해주세요.")
        String input
) {}