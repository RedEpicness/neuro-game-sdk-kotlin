package com.github.redepicness.neurogamesdk

import io.ktor.websocket.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject


@Serializable
internal data class NeuroMessage(
    val command: String,
    var game: String? = null, // Will be filled in automatically when sending
    val data: JsonObject? = null,
) {

    fun toFrame(): Frame.Text {
        return Frame.Text(json.encodeToString(this))
    }

    @Serializable
    data class Context(
        val message: String,
        val silent: Boolean,
    )

    @Serializable
    data class Action(
        val name: String,
        val description: String,
        val schema: JsonSchemaProperty? = null,
    )

    @Serializable
    data class ActionResult(
        val id: String,
        val success: Boolean,
        val message: String? = null,
    )

    @Serializable
    data class RegisterActions(
        val actions: List<Action>,
    )

    @Serializable
    data class UnregisterActions(
        @SerialName("action_names") val actionNames: List<String>,
    )

    @Serializable
    data class ForceActions(
        val state: String?,
        val query: String,
        @SerialName("ephemeral_context") val ephemeralContext: Boolean,
        @SerialName("action_names") val actionNames: List<String>,
    )

    @Serializable
    data class ActionExecute(
        val id: String,
        val name: String,
        val data: String? = null,
    )

    companion object {
        private val json = Json

        inline fun <reified T> message(command: String, game: String? = null, data: T) =
            NeuroMessage(command, game, json.encodeToJsonElement(data).jsonObject)

        fun startup(game: String? = null) = NeuroMessage("startup", game)

        fun context(message: String, silent: Boolean, game: String? = null) =
            message("context", game, Context(message, silent))

        fun registerActions(actions: List<Action>, game: String? = null) =
            message("actions/register", game, RegisterActions(actions))

        fun unregisterActions(actions: List<String>, game: String? = null) =
            message("actions/unregister", game, UnregisterActions(actions))

        fun forceActions(state: String?, query: String, ephemeralContext: Boolean, actionNames: List<String>, game: String? = null) =
            message("actions/force", game, ForceActions(state, query, ephemeralContext, actionNames))

        fun actionResult(id: String, success: Boolean, message: String? = null, game: String? = null) =
            message("action/result", game, ActionResult(id, success, message))

    }
}

