package dev.ag6.mclauncher.minecraft.piston

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

//TODO: Redo this lol
data class PistonArguments(val game: List<Argument>, val jvm: List<Argument>)

sealed class Argument {
    data class Simple(val value: String) : Argument()
    data class Conditional(val argument: ConditionalArgument) : Argument()
}

data class ConditionalArgument(
    val rules: List<Rule>, val value: ArgumentValue
)

sealed class ArgumentValue {
    data class Single(val value: String) : ArgumentValue()
    data class Multiple(val values: List<String>) : ArgumentValue()
}

data class Rule(
    val action: String,

    val features: Map<String, Boolean>? = null,

    val os: OsRule? = null
)

data class OsRule(
    val name: String? = null, val arch: String? = null
)

class ArgumentDeserializer : JsonDeserializer<Argument> {
    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ): Argument {
        return if (json.isJsonPrimitive) {
            Argument.Simple(json.asString)
        } else {
            Argument.Conditional(context.deserialize(json, ConditionalArgument::class.java))
        }
    }
}

class ArgumentValueDeserializer : JsonDeserializer<ArgumentValue> {
    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ): ArgumentValue {
        return if (json.isJsonPrimitive) {
            ArgumentValue.Single(json.asString)
        } else {
            val listType = object : TypeToken<List<String>>() {}.type
            ArgumentValue.Multiple(context.deserialize(json, listType))
        }
    }
}



