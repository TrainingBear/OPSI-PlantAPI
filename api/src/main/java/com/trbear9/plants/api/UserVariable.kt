package com.trbear9.plants.api

import lombok.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

@Getter
@AllArgsConstructor
@NoArgsConstructor
class UserVariable {
    @Setter
    private var tanah: String? = null

    //parameters
    var soil: SoilParameters = SoilParameters()
    var geo: GeoParameters = GeoParameters()
    var custom: CustomParameters? = null

    var image: ByteArray? = null
    var filename: String? = null
    var hash: String? = null

    fun setImage(image: ByteArray, filename: String){
        this.image = image
        this.filename = filename
    }

    @SneakyThrows
    fun computeHash() {
        val digest = MessageDigest.getInstance("SHA-256")
        if(image!=null) digest.update(image)
        if(tanah != null) digest.update(tanah?.toByteArray(StandardCharsets.UTF_8))

        soil?.parameters?.forEach { entry: Map.Entry<String?, String?> ->
            digest.update(entry.key?.toByteArray())
            digest.update(entry.value?.toByteArray() ?: entry.key?.toByteArray())
        }
        geo?.parameters?.forEach { entry: Map.Entry<String?, String?> ->
            digest.update(entry.key?.toByteArray())
            digest.update(entry.value?.toByteArray() ?: entry.key?.toByteArray())
        }
        custom?.parameters?.forEach { entry: Map.Entry<String?, String?> ->
            digest.update(entry.key?.toByteArray())
            digest.update(entry.value?.toByteArray() ?: entry.key?.toByteArray())
        }

        val hashBytes = digest.digest()
        hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)
    }
}
