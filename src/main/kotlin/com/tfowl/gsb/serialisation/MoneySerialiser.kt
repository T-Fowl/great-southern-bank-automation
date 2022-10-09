package com.tfowl.gsb.serialisation

import com.tfowl.gsb.model.Money
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MoneySerializer : KSerializer<Money> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Money", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Money = Money.parseOrNull(decoder.decodeString())!!


    override fun serialize(encoder: Encoder, value: Money) = encoder.encodeString("$value")
}