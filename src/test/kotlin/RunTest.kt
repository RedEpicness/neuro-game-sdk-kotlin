import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import com.github.redepicness.neurogamesdk.NeuroAction
import com.github.redepicness.neurogamesdk.NeuroActionWithoutResponse
import com.github.redepicness.neurogamesdk.NeuroGameSDK
import kotlin.time.Duration.Companion.seconds

fun main() {
    val sdk = NeuroGameSDK("Epic Game", "ws://localhost:8000/")

    // sdk.registerActions(EchoAction)

    runBlocking {
        launch {
            while (true) {
                delay(5.seconds)
                sdk.forceAction("Current state!", "Please send an echo:", false, listOf(EchoAction, NoResponseAction))
            }
        }
        sdk.start()
    }
}

object NoResponseAction : NeuroActionWithoutResponse("no-response", "This is the no response action") {
    override fun successMessage(): String {
        return "Succesfull!"
    }

    override suspend fun process() {
        println("Processing...")
    }

}

object EchoAction : NeuroAction<String>(
    "echo",
    "A simple command that echoes the message received to the logs.",
    String.serializer(),
) {

    override fun validate(data: String) = true

    override fun successMessage(data: String) = "Successfully echoed: $data"

    override suspend fun process(data: String) {
        logger.info("ECHO: $data")
    }

}
