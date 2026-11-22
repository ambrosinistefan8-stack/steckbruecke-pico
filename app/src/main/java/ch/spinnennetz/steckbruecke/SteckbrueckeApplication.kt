package ch.spinnennetz.steckbruecke

import android.app.Application
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope
import com.pico.spatial.ui.foundation.dsl.launch
import com.pico.spatial.ui.platform.stub.SpatialLaunchActivity

class LaunchActivity : SpatialLaunchActivity()

class SteckbrueckeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::createApp)
    }
}

private fun createApp(scope: SpatialAppScope) = with(scope) {
    DefaultWindowContainer {
        PicoTheme {
            Workbench(Modifier.windowConstraints(width = 1200.dp, height = 920.dp, depth = 900.dp))
        }
    }
}
