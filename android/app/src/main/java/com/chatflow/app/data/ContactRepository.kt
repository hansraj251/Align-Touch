package com.chatflow.app.data

class ContactRepository {

    suspend fun getContacts(
        token: String
    ): ContactListResponse {

        return ApiClient.authApi.getContacts(
            authorization =
                "Bearer $token"
        )
    }

    suspend fun getContact(
        token: String,
        contactId: String
    ): CreateContactResponse {

        return ApiClient.authApi.getContact(
            contactId = contactId,
            authorization =
                "Bearer $token"
        )
    }

    suspend fun createContact(
        token: String,
        request: CreateContactRequest
    ): CreateContactResponse {

        return ApiClient.authApi.createContact(
            request = request,
            authorization =
                "Bearer $token"
        )
    }
}
