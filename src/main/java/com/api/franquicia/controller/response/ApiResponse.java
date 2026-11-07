package com.api.franquicia.controller.response;

public record ApiResponse<T>(int status, T data) {}
