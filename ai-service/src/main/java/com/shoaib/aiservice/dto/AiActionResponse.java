package com.shoaib.aiservice.dto;

import com.shoaib.aiservice.util.AiActionType;
import java.util.UUID;

public record AiActionResponse(AiActionType type, boolean visible, String label, UUID categoryId) {}
