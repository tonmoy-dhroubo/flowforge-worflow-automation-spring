package com.flowforge.auth.dto;

public record ErrorResponse(int status, String error, String message, String path) {}
