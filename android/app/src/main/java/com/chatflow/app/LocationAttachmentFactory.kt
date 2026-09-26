package com.chatflow.app

data class LocationAttachment(
    val latitude: Double,
    val longitude: Double,
    val kind: AttachmentKind = AttachmentKind.LOCATION
)

object LocationAttachmentFactory {

    fun fromCoordinates(
        latitude: Double,
        longitude: Double
    ): LocationAttachment {

        require(
            latitude in -90.0..90.0
        ) {
            "Latitude must be between -90 and 90"
        }

        require(
            longitude in -180.0..180.0
        ) {
            "Longitude must be between -180 and 180"
        }

        return LocationAttachment(
            latitude = latitude,
            longitude = longitude
        )
    }
}
