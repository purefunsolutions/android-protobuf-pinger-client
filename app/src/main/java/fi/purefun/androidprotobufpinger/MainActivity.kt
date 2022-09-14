package fi.purefun.androidprotobufpinger

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.grpc.ManagedChannelBuilder
import java.io.Closeable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch

import fi.purefun.androidprotobufpinger.databinding.ActivityMainBinding
import pinger.PingerGrpcKt
import pinger.pingRequest

class PingerRCP(uri: Uri) : Closeable {
    val responseState = mutableStateOf("")

    private val channel = let {
        println("Connecting to ${uri.host}:${uri.port}")

        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)

        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }

        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val pinger = PingerGrpcKt.PingerCoroutineStub(channel)

    /**
     * A native method that is implemented by the 'androidprotobufpinger' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(txt: String): String

    suspend fun ping(msg: String) {
        try {
            val request = pingRequest { this.msg = msg }
            val response = pinger.ping(request)
            responseState.value = stringFromJNI(response.reply)
        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
        }
    }

    override fun close() {
        channel.shutdownNow()
    }
}

@Composable
fun Pinger(pingerRCP: PingerRCP) {
    val scope = rememberCoroutineScope()
    val requestState = remember { mutableStateOf(TextFieldValue()) }
    Column(Modifier.fillMaxWidth().fillMaxHeight(),
           Arrangement.Top,
           Alignment.CenterHorizontally) {

        Text("Request", modifier = Modifier.padding(top = 10.dp))

        OutlinedTextField(requestState.value, { requestState.value = it })

        Button({
            scope.launch {
                pingerRCP.ping(requestState.value.text) } },
            Modifier.padding(10.dp)) {
                Text("Send Request")
            }

        Text("Response", modifier = Modifier.padding(top = 10.dp))

        if (pingerRCP.responseState.value.isNotEmpty()) {
            Text(pingerRCP.responseState.value)
        }
    }
}

class MainActivity : AppCompatActivity() {

    private val uri by lazy { Uri.parse("http://10.0.0.12:5678/") }
    private val pingerService by lazy { PingerRCP(uri) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(color = MaterialTheme.colors.background) {
                Pinger(pingerService)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pingerService.close()
    }

    companion object {
        // Used to load the 'androidprotobufpinger' library on application startup.
        init {
            System.loadLibrary("androidprotobufpinger")
        }
    }
}
