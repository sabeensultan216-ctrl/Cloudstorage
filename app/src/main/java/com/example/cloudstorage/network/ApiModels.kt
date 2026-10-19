package com.example.cloudstorage.network

data class StorageOverviewResponse(
    val totalLimit: Long,
    val totalUsed: Long,
    val percentage: Int,
    val breakdown: StorageBreakdown,
    val fileCount: Int,
    val trashCount: Int
)

data class StorageBreakdown(
    val images: Long,
    val documents: Long,
    val videos: Long,
    val audio: Long,
    val others: Long
)

data class FileItemDto(
    val id: String,
    val userId: String,
    val name: String,
    val category: String,
    val size: Long,
    val mimeType: String,
    val isStarred: Boolean = false,
    val isTrash: Boolean = false,
    val isShared: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val storageUsed: Long,
    val storageLimit: Long
)

data class AuthResponse(
    val message: String,
    val user: UserDto,
    val token: String
)
