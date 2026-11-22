package ch.spinnennetz.steckbruecke

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.foundation.content.SpatialView
import com.pico.spatial.ui.foundation.gesture.TargetEntity
import com.pico.spatial.ui.foundation.gesture.detectSpatialTapGesture
import kotlinx.coroutines.CancellationException

@Composable
internal fun Workbench(modifier: Modifier = Modifier) {
    var assembly by remember { mutableStateOf(Assembly()) }
    var scene by remember { mutableStateOf<BridgeScene?>(null) }
    var assetFailure by remember { mutableStateOf(false) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose { scene?.close() }
    }

    Column(
        modifier = modifier
            .background(Color(0xFF07152D), RoundedCornerShape(28.dp))
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Steckbrücke", color = Color(0xFFF4FAFF), fontSize = 40.sp)
        Text(
            "Pick a loose part and fit it to the next socket.",
            color = Color(0xFFC9DBEB),
            fontSize = 22.sp,
        )
        Text(
            when {
                assetFailure -> "The model could not load. Close and reopen the workbench."
                scene == null -> "Loading the bridge workbench."
                else -> "${assembly.placed}/3  ${assembly.message()}"
            },
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            color = Color(0xFF9CFFDD),
            fontSize = 24.sp,
        )

        SpatialView(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .semantics { contentDescription = "Three-dimensional bridge workbench" }
                .pointerInput(context) {
                    detectSpatialTapGesture(context, targetedToEntity = TargetEntity.any()) { tap ->
                        val target = tap.targetEntity?.let { scene?.target(it) }
                        if (target != null) {
                            assembly = when (target) {
                                is WorkbenchTarget.LoosePart -> assembly.select(target.part)
                                is WorkbenchTarget.Socket -> assembly.place(target.part)
                            }
                        }
                    }
                },
            initial = { content, _ ->
                try {
                    val board = BridgeScene.load()
                    try {
                        board.show(assembly)
                        content.addEntity(board.root)
                        scene = board
                    } catch (error: Exception) {
                        board.close()
                        throw error
                    }
                } catch (cancel: CancellationException) {
                    throw cancel
                } catch (error: Exception) {
                    Log.e("Steckbruecke", "Unable to load the workbench", error)
                    assetFailure = true
                }
            },
            update = { _, _ -> scene?.show(assembly) },
        )

        Text("Panel controls", color = Color(0xFFC9DBEB), fontSize = 18.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (part in Part.entries) {
                Button(
                    enabled = scene != null && !assembly.isPlaced(part),
                    onClick = { assembly = assembly.select(part) },
                ) {
                    Text("Pick ${part.label.lowercase()}")
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (part in Part.entries) {
                Button(
                    enabled = scene != null && !assembly.isPlaced(part),
                    onClick = { assembly = assembly.place(part) },
                ) {
                    Text("${part.label} socket")
                }
            }
            Button(enabled = scene != null, onClick = { assembly = Assembly() }) {
                Text("Reset")
            }
        }
    }
}
