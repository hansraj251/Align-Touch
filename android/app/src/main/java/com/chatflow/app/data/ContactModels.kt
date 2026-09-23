package com.chatflow.app.data

data class Contact(
    val id: String,
    val owner_user_id: String,
    val linked_user_id: String?,
    val first_name: String,
    val last_name: String?,
    val country_code: String,
    val phone: String,
    val created_at: String?,
    val updated_at: String?
)

data class ContactListResponse(
    val success: Boolean,
    val contacts: List<Contact>
)

data class CreateContactRequest(
    val firstName: String,
    val lastName: String?,
    val countryCode: String,
    val phone: String
)

data class UpdateContactRequest(
    val firstName: String,
    val lastName: String?,
    val countryCode: String,
    val phone: String
)

data class CreateContactResponse(
    val success: Boolean,
    val contact: Contact
)
