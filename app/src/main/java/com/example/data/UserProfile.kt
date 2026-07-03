package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val dpIndicator: String, // "avatar_1", "avatar_2", etc. or a image URI
    val university: String,
    val degree: String,
    val gradingScale: String = "Standard", // "Standard" (A/A+=4.0) or "Moratuwa" (A+=4.2, A=4.0)
    val isDarkMode: Boolean? = null // null: System, true: Dark, false: Light
)

val SRI_LANKAN_UNIVERSITIES = listOf(
    "University of Colombo (UOC)",
    "University of Peradeniya (UOP)",
    "University of Sri Jayewardenepura (USJ)",
    "University of Kelaniya (UOK)",
    "University of Moratuwa (UOM)",
    "University of Jaffna (UOJ)",
    "University of Ruhuna (UOR)",
    "Open University of Sri Lanka (OUSL)",
    "Eastern University, Sri Lanka (EUSL)",
    "Rajarata University of Sri Lanka (RUSL)",
    "Sabaragamuwa University of Sri Lanka (SUSL)",
    "Wayamba University of Sri Lanka (WUSL)",
    "Uva Wellassa University (UWU)",
    "University of the Visual and Performing Arts (UVPA)",
    "Gampaha Wickramarachchi University of Indigenous Medicine (GWUIM)",
    "University of Vavuniya",
    "Bhiksu University of Sri Lanka",
    "Buddhist and Pali University of Sri Lanka",
    "General Sir John Kotelawala Defence University (KDU)"
)

val AVATAR_TEMPLATES = listOf(
    "avatar_lion" to "🦁 Ceylon Lion",
    "avatar_peacock" to "🦚 Ceylon Peacock",
    "avatar_lotus" to "🌸 Blue Water Lily (Lotus)",
    "avatar_grad" to "🎓 Scholar Cap",
    "avatar_tea" to "☕ Ceylon Tea Cup",
    "avatar_student" to "🧑‍🎓 University Student"
)
