package com.nuvio.app.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nuvio.app.features.details.MetaScreenSettingsRepository
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.settings_trailer_delay
import nuvio.composeapp.generated.resources.settings_trailer_delay_description
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
internal fun TrailerDelaySetting(isTablet: Boolean, enabled: Boolean, delayMillis: Int) {
    var value by remember(delayMillis) { mutableFloatStateOf(delayMillis.toFloat()) }
    Column(Modifier.fillMaxWidth().padding(horizontal = if (isTablet) 20.dp else 16.dp, vertical = 14.dp)) {
        Text(
            stringResource(Res.string.settings_trailer_delay, (value.roundToInt() / 1000f).toString()),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            stringResource(Res.string.settings_trailer_delay_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Slider(
            value = value,
            onValueChange = { value = (it / 500f).roundToInt() * 500f },
            onValueChangeFinished = { MetaScreenSettingsRepository.setHeroTrailerDelayMillis(value.roundToInt()) },
            valueRange = 0f..10_000f,
            steps = 19,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
