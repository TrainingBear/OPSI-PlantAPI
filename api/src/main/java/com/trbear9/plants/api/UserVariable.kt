package com.trbear9.plants.api

import lombok.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import java.util.function.Consumer

@Getter
@AllArgsConstructor
@NoArgsConstructor
class UserVariable {
    @Setter
    private var tanah: String? = null

    @Singular
    val parameters: MutableMap<Class<out Parameters>, Parameters> =
        HashMap<Class<out Parameters>, Parameters>()

    var image: ByteArray? = null
    var filename: String? = null
    var hash: String? = null

    fun setImage(image: ByteArray, filename: String){
        this.image = image
        this.filename = filename
    }
    fun modify(par: SoilParameters) {
        (parameters[SoilParameters::class.java] as SoilParameters).modify(par)
    }

    fun add(vararg parms: Parameters) {
        for (par in parms) parameters.put(par.javaClass, par)
    }

    @SneakyThrows
    fun computeHash() {
        val digest = MessageDigest.getInstance("SHA-256")

        if (image != null) digest.update(image)
        if (tanah != null) digest.update(tanah!!.toByteArray(StandardCharsets.UTF_8))

        for (entry in parameters.entries) {
            digest.update(entry.key.getName().toByteArray())
            val parameter: Parameters = entry.value
            digest.update(parameter.toString().toByteArray())
            for (par in parameter.getParameters().entries) {
                digest.update(par.key.toByteArray())
                val value = par.value
                digest.update(value?.toByteArray() ?: parameter.toString().toByteArray())
            }
        }

        val hashBytes = digest.digest()
        hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)
    }

    fun fetch(consumer: Consumer<Parameters?>) {
        for (value in parameters.values) {
            consumer.accept(value)
        }
    }
}
